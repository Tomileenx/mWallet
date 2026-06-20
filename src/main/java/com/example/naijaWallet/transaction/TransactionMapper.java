package com.example.naijaWallet.transaction;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target = "wallet_id", ignore = true)
    TransactionResponse toResponse(Transaction transaction);
}
