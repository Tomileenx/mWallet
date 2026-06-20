package com.example.naijaWallet.userAccount;


import com.example.naijaWallet.auth.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "wallet", ignore = true)
    @Mapping(target = "refreshTokens", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    UserAccount toEntity(RegisterRequest request);

    UserResponse toResponse(UserAccount userAccount);
}
