package com.example.naijaWallet.userAccount;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/naijaWallet")
public class UserController {
    private final UserService userService;

    @GetMapping("/account/{userId}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable UUID userId
    ) {
        UserResponse response = userService.getUser(userId);

        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
