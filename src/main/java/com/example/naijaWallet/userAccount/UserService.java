package com.example.naijaWallet.userAccount;

import com.example.naijaWallet.exception.NotFound;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepo repo;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponse getUser(UUID userId) {
        UserAccount account = repo.findById(userId)
                .orElseThrow(() -> new NotFound("Account not found"));

        return userMapper.toResponse(account);
    }
}
