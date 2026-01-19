package com.sidework.user.application.adapter;

import com.sidework.common.response.ApiResponse;
import com.sidework.user.application.port.in.SignUpCommand;
import com.sidework.user.application.port.in.UserCommandUseCase;
import com.sidework.user.application.port.in.UserQueryUseCase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserCommandUseCase commandService;
    private final UserQueryUseCase queryService;

    @GetMapping("/email")
    public ResponseEntity<ApiResponse<EmailExistResponse>> getEmailAvailable(@RequestParam @Email @NotNull String email) {
        boolean res = queryService.checkEmailExists(email);
        return ResponseEntity.ok(ApiResponse.onSuccess(new EmailExistResponse(res)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> postNewUser(@Validated @RequestBody SignUpCommand command) {
        commandService.signUp(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.onSuccessCreated());
    }

}
