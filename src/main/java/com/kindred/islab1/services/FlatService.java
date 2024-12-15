package com.kindred.islab1.services;


import com.kindred.islab1.authentication.ImportStatus;
import com.kindred.islab1.authentication.Roles;
import com.kindred.islab1.entities.*;
import com.kindred.islab1.exceptions.ImportException;
import com.kindred.islab1.exceptions.ResourceNotFoundException;
import com.kindred.islab1.repositories.*;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@Service
public class FlatService {

    private final FlatRepository flatRepository;

    @Autowired
    public FlatService(FlatRepository flatRepository) {
        this.flatRepository = flatRepository;
    }

    @Autowired
    HouseRepository houseRepository;

    @Autowired
    CoordinatesRepository coordinatesRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleService roleService;

    @Autowired
    ImportHistoryRepository importHistoryRepository;

    public List<ImportHistory> getImportHistory(String username) {
        return importHistoryRepository.findAllByOwnerIdOrderByCreationDateDesc(userRepository.findByUsername(username).orElseThrow().getId());
    }

    public List<ImportHistory> getAllImportHistory() {
        return importHistoryRepository.findAllByOrderByCreationDateDesc();
    }

    public boolean validateFlat(Flat flat) {
        if (flatRepository.findFirstByHouse_Id(flat.getHouse().getId()).isPresent()) {
            Flat controlFlat = flatRepository.findFirstByHouse_Id(flat.getHouse().getId()).orElseThrow();
            return controlFlat.getCoordinates().getX() == flat.getCoordinates().getX() && controlFlat.getCoordinates().getY() == flat.getCoordinates().getY();
        }
        return true;
    }

    public House createHouse(House house, String username) {
        house.setOwnerId(userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not exist")).getId());
        return houseRepository.save(house);
    }

    public List<House> getHouses() {
        return houseRepository.findAllByOrderByNameAsc();
    }

    public Coordinates createCoordinates(Coordinates coordinates, String username) {
        coordinates.setOwnerId(userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not exist")).getId());
        return coordinatesRepository.save(coordinates);
    }

    public List<Coordinates> getCoordinates() {
        return coordinatesRepository.findAllByOrderByIdAsc();
    }

    public Flat createFlat(Flat flat, String username) {
        flat.setCoordinates(coordinatesRepository.findById(flat.getCoordinates().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find this coordinates")));
        flat.setHouse(houseRepository.findById(flat.getHouse().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find this house")));
        flat.setOwnerId(userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not exist")).getId());
        if (validateFlat(flat)) {
            return flatRepository.save(flat);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "This house build in the another coordinates");
    }

    public Flat getFlat(Long id) {
        return flatRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + id));
    }

    public Flat updateFlat(Flat flatDetails, String username) {
        Flat flat = getFlat(flatDetails.getId());
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
        if (flat.getHouse().getOwnerId() == user.getId() || user.getRole().contains(roleService.ensureRoleExists(Roles.ADMIN))) {
            flat.getHouse().setName(flatDetails.getHouse().getName());
            flat.getHouse().setNumberOfFlatsOnFloor(flatDetails.getHouse().getNumberOfFlatsOnFloor());
            flat.getHouse().setYear(flatDetails.getHouse().getYear());
        }
        if (flat.getCoordinates().getOwnerId() == user.getId() || user.getRole().contains(roleService.ensureRoleExists(Roles.ADMIN))) {
            flat.getCoordinates().setX(flatDetails.getCoordinates().getX());
            flat.getCoordinates().setY(flatDetails.getCoordinates().getY());
        }
        if (flat.getOwnerId() == user.getId() || user.getRole().contains(roleService.ensureRoleExists(Roles.ADMIN))) {
            flat.setName(flatDetails.getName());
            flat.setArea(flatDetails.getArea());
            flat.setPrice(flatDetails.getPrice());
            flat.setBalcony(flatDetails.getBalcony());
            flat.setTimeToMetroOnFoot(flatDetails.getTimeToMetroOnFoot());
            flat.setNumberOfRooms(flatDetails.getNumberOfRooms());
            flat.setIsNew(flatDetails.getIsNew());
            flat.setFurnish(flatDetails.getFurnish());
            flat.setView(flatDetails.getView());
        }
        return flatRepository.save(flat);
    }

    @Transactional
    public void deleteFlat(Long id, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
        Flat flat = getFlat(id);
        if (flat.getOwnerId() == user.getId() || user.getRole().contains(roleService.ensureRoleExists(Roles.ADMIN))) {
            flatRepository.deleteById(id);
            if (!flatRepository.existsByCoordinatesId(flat.getCoordinates().getId())) {
                coordinatesRepository.deleteById(flat.getCoordinates().getId());
            }
            if (!flatRepository.existsByHouseId(flat.getHouse().getId())) {
                houseRepository.deleteById(flat.getHouse().getId());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "U do not own this flat");
        }
    }

    public float averageNumberOfRooms() {
        return (float) flatRepository.findAll().stream()
                .map(Flat::getNumberOfRooms)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
    }

    public Flat getFlatWithMaxArea() {
        return flatRepository.findAll().stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparingDouble(flat -> Optional.of(flat.getArea()).orElse(0.0)))
                .orElseThrow(() -> new ResourceNotFoundException("No flats available"));
    }

    public long countFlatsWithIsNew(boolean isNewValue) {
        return flatRepository.findAll().stream()
                .filter(flat -> flat.getIsNew() == isNewValue)
                .count();
    }

    public Flat getMostExpensiveFlat(List<Long> ids) {
        return ids.stream()
                .map(this::getFlat)
                .max(Comparator.comparingDouble(flat -> Optional.of(flat.getPrice()).orElse(0.0)))
                .orElseThrow(() -> new ResourceNotFoundException("No flats found with the given IDs"));
    }

    public Flat getMostExpensiveFlatWithoutBalcony() {
        return flatRepository.findAll().stream()
                .filter(flat -> flat != null && Boolean.FALSE.equals(flat.getBalcony()))
                .max(Comparator.comparingDouble(flat -> Optional.of(flat.getPrice()).orElse(0.0)))
                .orElseThrow(() -> new ResourceNotFoundException("No flats without a balcony found"));
    }

    public List<Flat> getAllFlats() {
        return flatRepository.findAll();
    }


    private List<String> getSheetNames(Workbook workbook) {
        List<String> sheetNames = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheetNames.add(workbook.getSheetName(i));
        }
        return sheetNames;
    }


    @Transactional
    public Map<String, Object> importFlats(MultipartFile flatFile, String username) throws IOException {
        // Получаем список всех листов
        Map<String, Object> response = new HashMap<>();

        Workbook workbook = new XSSFWorkbook(flatFile.getInputStream());
        List<String> sheetNames = getSheetNames(workbook);
        ImportHistory importHistory = new ImportHistory();

        // Вызываем соответствующие методы, если лист существует
        if (sheetNames.contains("Houses")) {
            List<House> houses = saveAllHouse(parseHousesFromSheet(workbook.getSheet("Houses"), username));
            response.put("houses", houses);
            response.put("housesImported", houses.size());
            importHistory.setCreatedHouses(houses.size());

        }
        if (sheetNames.contains("Coordinates")) {
            List<Coordinates> coordinatesList = saveAllCoordinates(parseCoordinatesFromSheet(workbook.getSheet("Coordinates"), username));
            response.put("coordinates", coordinatesList);
            response.put("coordinatesImported", coordinatesList.size());
            importHistory.setCreatedCoordinates(coordinatesList.size());
        }
        if (sheetNames.contains("Flats")) {
            List<Flat> flats = saveAllFlats(parseFlatsFromSheet(workbook.getSheet("Flats"), username), username);
            response.put("flats", flats);
            response.put("flatsImported", flats.size());
            importHistory.setCreatedFlats(flats.size());
        }
        importHistory.setUsername(username);
        importHistory.setOwnerId(userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")).getId());
        importHistory.setStatus(ImportStatus.SUCCESS);
        importHistoryRepository.save(importHistory);
        return response;
    }

    @Transactional
    public List<Flat> saveAllFlats(List<Flat> flats, String username) {
        List<Flat> savedFlats = new ArrayList<>();
        if (flats == null || flats.isEmpty()) {
            throw new ImportException("Flat list is empty or null", ImportStatus.FAILED_DUE_TO_INCORRECT_DATA_IN_FILE, username, HttpStatus.BAD_REQUEST);

        }
        for (Flat flat : flats) {
            if (!validateFlat(flat)) {
                throw new ImportException("Flat validation failed!", ImportStatus.FAILED_DUE_TO_VALIDATION, username, HttpStatus.BAD_REQUEST);
            }
            savedFlats.add(flatRepository.save(flat));
        }
        return savedFlats;
    }

    @Transactional
    public List<House> saveAllHouse(List<House> house) {
        if (house == null || house.isEmpty()) {
            return Collections.emptyList();
        }
        return houseRepository.saveAll(house);
    }

    @Transactional
    public List<Coordinates> saveAllCoordinates(List<Coordinates> coordinates) {
        if (coordinates == null || coordinates.isEmpty()) {
            return Collections.emptyList();
        }
        return coordinatesRepository.saveAll(coordinates);
    }

    private List<Flat> parseFlatsFromSheet(Sheet sheet, String username) {
        List<Flat> flats = new ArrayList<>();
        Iterator<Row> rowIterator = sheet.iterator();

        if (!rowIterator.hasNext()) {
            throw new ImportException("Flats sheet is empty", ImportStatus.FAILED_DUE_TO_INCORRECT_DATA_IN_FILE, username, HttpStatus.BAD_REQUEST);
        }

        Row headerRow = rowIterator.next();
        Map<String, Integer> headerMap = getHeaderMap(headerRow);

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            try {
                flats.add(parseFlatFromRow(row, headerMap, username));
            } catch (Exception e) {
                throw new ImportException("Error parsing flat at row " + (row.getRowNum() + 1) + ": " + e.getMessage(), ImportStatus.FAILED_DUE_TO_INCORRECT_DATA_IN_FILE, username, HttpStatus.BAD_REQUEST);
            }
        }
        return flats;
    }


    private Map<String, Integer> getHeaderMap(Row headerRow) {
        Map<String, Integer> headerMap = new HashMap<>();
        for (Cell cell : headerRow) {
            String header = cell.getStringCellValue().trim().toLowerCase();
            headerMap.put(header, cell.getColumnIndex());
        }
        return headerMap;
    }

    private Flat parseFlatFromRow(Row row, Map<String, Integer> headerMap, String username) {
        Flat flat = new Flat();
        flat.setName(getStringValue(row, headerMap.get("name")));
        flat.setArea(getDoubleValue(row, headerMap.get("area")));
        flat.setPrice(getDoubleValue(row, headerMap.get("price")));
        flat.setBalcony(getBooleanValue(row, headerMap.get("balcony")));
        flat.setTimeToMetroOnFoot(getDoubleValue(row, headerMap.get("time_to_metro_on_foot")));
        flat.setNumberOfRooms(getLongValue(row, headerMap.get("number_of_rooms")));
        flat.setIsNew(getBooleanValue(row, headerMap.get("is_new")));
        flat.setFurnish(Furnish.valueOf(getStringValue(row, headerMap.get("furnish")).toUpperCase()));
        flat.setView(View.valueOf(getStringValue(row, headerMap.get("view")).toUpperCase()));

        flat.setHouse(houseRepository.findById(getLongValue(row, headerMap.get("house_id"))).orElseThrow());

        flat.setCoordinates(coordinatesRepository.findById(getLongValue(row, headerMap.get("coordinate_id"))).orElseThrow());

        flat.setOwnerId(userRepository.findByUsername(username).orElseThrow().getId());

        if (flat.getName() == null || flat.getArea() <= 0 || flat.getPrice() <= 0) {
            throw new ImportException("Validation failed", ImportStatus.FAILED_DUE_TO_VALIDATION, username, HttpStatus.BAD_REQUEST);
        }
        return flat;
    }

    private List<House> parseHousesFromSheet(Sheet sheet, String username) {
        List<House> houses = new ArrayList<>();

        Iterator<Row> rowIterator = sheet.iterator();
        if (!rowIterator.hasNext()) {
            return houses;
        }
        Row headerRow = rowIterator.next();
        Map<String, Integer> headerMap = getHeaderMap(headerRow);

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            try {
                House house = new House();
                house.setName(getStringValue(row, headerMap.get("name")));
                house.setYear(getIntValue(row, headerMap.get("year")));
                house.setNumberOfFlatsOnFloor(getIntValue(row, headerMap.get("number_of_flats_on_floor")));
                house.setOwnerId(userRepository.findByUsername(username).orElseThrow().getId());
                houses.add(house);
            } catch (IllegalArgumentException | NullPointerException e) {
                throw new ImportException("Error parsing house at row " + (row.getRowNum() + 1) + ": " + e.getMessage(), ImportStatus.FAILED_DUE_TO_INCORRECT_DATA_IN_FILE, username, HttpStatus.BAD_REQUEST);

            }
        }
        return houses;
    }

    private List<Coordinates> parseCoordinatesFromSheet(Sheet sheet, String username) {
        List<Coordinates> coordinatesList = new ArrayList<>();

        Iterator<Row> rowIterator = sheet.iterator();
        if (!rowIterator.hasNext()) {
            return coordinatesList;
        }
        Row headerRow = rowIterator.next();
        Map<String, Integer> headerMap = getHeaderMap(headerRow);

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            try {
                Coordinates coordinates = new Coordinates();
                coordinates.setX(getFloatValue(row, headerMap.get("x")));
                coordinates.setY(getDoubleValue(row, headerMap.get("y")));
                coordinates.setOwnerId(userRepository.findByUsername(username).orElseThrow().getId());
                coordinatesList.add(coordinates);
            } catch (IllegalArgumentException | NullPointerException e) {
                throw new ImportException("Error parsing coordinates at row " + (row.getRowNum() + 1) + ": " + e.getMessage(), ImportStatus.FAILED_DUE_TO_INCORRECT_DATA_IN_FILE, username, HttpStatus.BAD_REQUEST);
            }
        }

        return coordinatesList;
    }


    private String getStringValue(Row row, Integer colIndex) {
        return colIndex != null ? row.getCell(colIndex).getStringCellValue().trim() : null;
    }

    private double getDoubleValue(Row row, Integer colIndex) {
        return colIndex != null ? row.getCell(colIndex).getNumericCellValue() : 0.0;
    }

    private boolean getBooleanValue(Row row, Integer colIndex) {
        return colIndex != null && row.getCell(colIndex).getBooleanCellValue();
    }

    private long getLongValue(Row row, Integer colIndex) {
        return colIndex != null ? (long) row.getCell(colIndex).getNumericCellValue() : 0;
    }

    private int getIntValue(Row row, Integer colIndex) {
        return colIndex != null ? (int) row.getCell(colIndex).getNumericCellValue() : 0;
    }

    private float getFloatValue(Row row, Integer colIndex) {
        return colIndex != null ? (float) row.getCell(colIndex).getNumericCellValue() : 0.0f;
    }
}
