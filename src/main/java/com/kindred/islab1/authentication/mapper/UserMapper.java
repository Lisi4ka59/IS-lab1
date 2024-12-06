package com.kindred.islab1.authentication.mapper;

import com.kindred.islab1.authentication.dto.LoginResponse;
import com.kindred.islab1.authentication.dto.UserResponse;
import com.kindred.islab1.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "username", target = "username")
    LoginResponse toLoginResponse(User user);

    @Mapping(source = "username", target = "username")
    UserResponse toUserResponse(User user);
}
