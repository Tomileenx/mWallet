package com.example.naijaWallet.refreshToken;


import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface RefreshMapper {
    RefreshResponse toResponse(RefreshToken refreshToken);
}
