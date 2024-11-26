package com.kindred.islab1.authentication.mapper;

import com.kindred.islab1.authentication.dto.LoginResponse;
import com.kindred.islab1.authentication.dto.UserResponse;
import com.kindred.islab1.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    LoginResponse toLoginResponse(User user);
    UserResponse toUserResponse(User user);
}