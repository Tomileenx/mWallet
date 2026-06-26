package com.example.naijaWallet.userAccount;

import com.example.naijaWallet.exception.NotFound;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepo repo;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponse getMyAccount(UserAccount userAccount) {
        return userMapper.toResponse(userAccount);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public UserResponse getUser(UUID userId) {
        UserAccount account = repo.findById(userId)
                .orElseThrow(() -> new NotFound("Account not found"));

        return userMapper.toResponse(account);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Pageable pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), 10),
                Sort.by("createdAt").descending()
        );

        Page<UserAccount> users = repo.findAll(pageRequest);

        return users.map(userMapper::toResponse);
    }

}
