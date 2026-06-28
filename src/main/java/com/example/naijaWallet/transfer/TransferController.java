package com.example.naijaWallet.transfer;

import com.example.naijaWallet.config.UserPrincipal;

import com.example.naijaWallet.userAccount.UserAccount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/naijaWallet")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TransferController {
    private final TransferService transferService;

    @Operation(summary = "mWallet Transfer")
    @ApiResponses({
            @ApiResponse(responseCode = "400", ref = "#/components/responses/400"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/404"),
            @ApiResponse(responseCode = "429", ref = "#/components/responses/429"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/500")
    })
    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transferMoney(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody TransferRequest request
    ) {
        UserAccount userAccount = userPrincipal.getUser();
        TransferResponse response = transferService.transferMoney(userAccount, idempotencyKey, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "My mWallet Transfer")
    @ApiResponses({
            @ApiResponse(responseCode = "404", ref = "#/components/responses/404"),
            @ApiResponse(responseCode = "429", ref = "#/components/responses/429"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/500")
    })
    @GetMapping("/wallet/transfers/{transferId}")
    public ResponseEntity<TransferResponse> getTransferById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID transferId
    ) {
        UserAccount userAccount = userPrincipal.getUser();
        TransferResponse response = transferService.getTransferId(userAccount, transferId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "My mWallet Transfers")
    @ApiResponses({
            @ApiResponse(responseCode = "429", ref = "#/components/responses/429"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/500")
    })
    @GetMapping("/wallet/transfers")
    public ResponseEntity<Page<TransferResponse>> getAllTransfers(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ParameterObject
            @PageableDefault(
                    page = 0
            )
            Pageable pageable
    ) {
        UserAccount userAccount = userPrincipal.getUser();

        Page<TransferResponse> response =
                transferService.getAllTransfers(userAccount, pageable);

        return ResponseEntity.ok(response);
    }
}
