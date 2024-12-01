package com.kindred.islab1.controllers;

import com.kindred.islab1.entities.Coordinates;
import com.kindred.islab1.entities.Flat;
import com.kindred.islab1.entities.House;
import com.kindred.islab1.services.FlatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/flats")
public class FlatController {
    private final FlatService flatService;

    @Autowired
    public FlatController(FlatService flatService) {
        this.flatService = flatService;
    }

    // TODO
    //  Разработанная система должна удовлетворять следующим требованиям:
    //  ✔    Основное назначение информационной системы - управление объектами, созданными на основе заданного в варианте класса.
    //  ✔    Необходимо, чтобы с помощью системы можно было выполнить следующие операции с объектами: создание нового объекта, получение информации об объекте по ИД, обновление объекта (модификация его атрибутов), удаление объекта. Операции должны осуществляться в отдельных окнах (интерфейсах) приложения.При получении информации об объекте класса должна также выводиться информация о связанных с ним объектах.
    //  ✔    При создании объекта класса необходимо дать пользователю возможность связать новый объект с объектами вспомогательных классов, которые могут быть связаны с созданным объектом и уже есть в системе.
    //  ✔    Выполнение операций по управлению объектами должно осуществляться на серверной части (не на клиенте), изменения должны синхронизироваться с базой данных.
    //  ✔    На главном экране системы должен выводиться список текущих объетов в виде таблицы (каждый атрибут объекта - отдельная колонка в таблице). При отображении таблицы должна использоваться пагинация (если все объекты не помещаются на одном экране).
    //      Нужно обеспечить возможность фильтровать/сортировать строки таблицы, которые показывают объекты (по значениям любой из строковых колонок). Фильтрация элементов должна производиться по неполному совпадению.
    //  ✔    Переход к обновлению (модификации) объекта должен быть возможен из таблицы с общим списком объектов и из области с визуализацией объекта (при ее реализации).
    //      При добавлении/удалении/изменении объекта, он должен автоматически появиться/исчезнуть/измениться в интерфейсах у других пользователей, авторизованных в системе.
    //  ✔    Если при удалении объекта с ним связан другой объект, связанные объекты должны удаляться.
    //  ✔    Пользователю системы должен быть предоставлен интерфейс для авторизации/регистрации нового пользователя. У каждого пользователя должен быть один пароль. Требования к паролю: пароль должен быть содержать не менее n символов. В системе предполагается использование следующих видов пользователей (ролей):обычные пользователи и администраторы. Если в системе уже создан хотя бы один администратор, зарегистрировать нового администратора можно только при одобрении одним из существующих администраторов (у администратора должен быть реализован интерфейс со списком заявок и возможностью их одобрения).
    //  ✔    Редактировать и удалять объекты могут только пользователи, которые их создали, и администраторы (администраторы могут удалять все объекты).
    //  ✔    Зарегистрированные пользователи должны иметь возможность просмотра всех объектов, но модифицировать (обновлять) могут только принадлежащие им (объект принадлежит пользователю, если он его создал). Для модификации объекта должно открываться отдельное диалоговое окно. При вводе некорректных значений в поля объекта должны появляться информативные сообщения о соответствующих ошибках.
    //  ✔ В системе должен быть реализован отдельный пользовательский интерфейс для выполнения специальных операций над объектами:
    //  ✔    Рассчитать среднее значение поля numberOfRooms для всех объектов.
    //  ✔    Вернуть один (любой) объект, значение поля area которого является максимальным.
    //  ✔    Вернуть количество объектов, значение поля new которых меньше заданного.
    //  ✔    Найти самую дорогую квартиру без балкона.
    //  ✔    Выбрать из трёх квартир с заданными пользователем id наиболее дорогую
    //  ✔    Представленные операции должны быть реализованы в рамках компонентов бизнес-логики приложения без прямого использования функций и процедур БД.
    //  ✔ Особенности хранения объектов, которые должны быть реализованы в системе:
    //  ✔    Организовать хранение данных об объектах в реляционной СУБД (PostgreSQL). Каждый объект, с которым работает ИС, должен быть сохранен в базе данных.
    //  ✔    Все требования к полям класса (указанные в виде комментариев к описанию классов) должны быть выполнены на уровне ORM и БД.
    //  ✔    Для генерации поля id использовать средства базы данных.
    //  ✔    Пароли при хранении хэшировать алгоритмом SHA-384.
    //  ✔    При хранении объектов сохранять информацию о пользователе, который создал этот объект, а также фиксировать даты и пользователей, которые обновляли и изменяли объекты. Для хранения информации о пользователях и об изменениях объектов нужно продумать и реализовать соответствующие таблицы.
    //  ✔    Таблицы БД, не отображающие заданные классы объектов, должны содержать необходимые связи с другими таблицами и соответствовать 3НФ.
    //  ✔    Для подключения к БД на кафедральном сервере использовать хост pg, имя базы данных - studs, имя пользователя/пароль совпадают с таковыми для подключения к серверу.
    //  ✔ При создании системы нужно учитывать следующие особенности организации взаимодействия с пользователем.
    //  ✔    Система должна реагировать на некорректный пользовательский ввод, ограничивая ввод недопустимых значений и информируя пользователей о причине ошибки.
    //  ✔    Переходы между различными логически обособленными частями системы должны осуществляться с помощью меню.
    //  ✔    Во всех интерфейсах системы должно быть реализовано отображение информации о текущем пользователе (кто авторизован) и предоставляться возможность изменить текущего пользователя.
    //  -    [Опциональное задание - +2 балл] В отдельном окне ИС должна осуществляться визуализация объектов коллекции. При визуализации использовать данные о координатах и размерах объекта. Объекты от разных пользователей должны быть нарисованы разными цветами. При нажатии на объект должна выводиться информация об этом объекте.
    //  -    При добавлении/удалении/изменении объекта, он должен автоматически появиться/исчезнуть/измениться на области у всех других клиентов.
    //  ✔ При разработке ИС должны учитываться следующие требования:
    //  ✔    В качестве основы для реализации ИС необходимо использовать Spring MVC.
    //  ✔    Для создания уровня хранения необходимо использовать Hibernate.
    //  ✔    Разные уровни приложения должны быть отделены друг от друга, разные логические части ИС должны находиться в отдельных компонентах.


    @PostMapping
    public ResponseEntity<Map<String, Object>> createFlat(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody Flat flat) {
        Map<String, Object> response = new HashMap<>();

        response.put("flat", flatService.createFlat(flat, userDetails.getUsername()));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/house")
    public ResponseEntity<Map<String, Object>> createHouse(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody House house) {
        Map<String, Object> response = new HashMap<>();
        response.put("house", flatService.createHouse(house, userDetails.getUsername()));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/coordinates")
    public ResponseEntity<Map<String, Object>> createCoordinates(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody Coordinates coordinates) {
        Map<String, Object> response = new HashMap<>();
        response.put("coordinates", flatService.createCoordinates(coordinates, userDetails.getUsername()));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/houses")
    public ResponseEntity<Map<String, Object>> getHouse() {
        Map<String, Object> response = new HashMap<>();
        response.put("houses", flatService.getHouses());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/coordinates")
    public ResponseEntity<Map<String, Object>> getCoordinates() {
        Map<String, Object> response = new HashMap<>();
        response.put("coordinates", flatService.getCoordinates());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getFlat(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("flat", flatService.getFlat(id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateFlat(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody Flat flatDetails) {
        Map<String, Object> response = new HashMap<>();
        response.put("updatedFlat", flatService.updateFlat(flatDetails, userDetails.getUsername()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteFlat(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        flatService.deleteFlat(id, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/flat-list")
    public ResponseEntity<Map<String, Object>> getFlatList() {
        Map<String, Object> response = new HashMap<>();
        response.put("flats", flatService.getAllFlats());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
