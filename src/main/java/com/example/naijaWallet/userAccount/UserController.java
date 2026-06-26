package com.example.naijaWallet.userAccount;

import com.example.naijaWallet.config.UserPrincipal;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/naijaWallet")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;

    @GetMapping("/account")
    public ResponseEntity<UserResponse> getMyAccount(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        UserAccount userAccount = userPrincipal.getUser();
        UserResponse response = userService.getMyAccount(userAccount);

        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
