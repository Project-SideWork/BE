package com.sidework.user.application.adapter;

import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.common.response.ApiResponse;
import com.sidework.user.application.docs.UserControllerDocs;
import com.sidework.user.application.port.in.GithubInfoResponse;
import com.sidework.user.application.port.in.SignUpCommand;
import com.sidework.user.application.port.in.UserCommandUseCase;
import com.sidework.user.application.port.in.UserQueryUseCase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {
    private final UserCommandUseCase commandService;
    private final UserQueryUseCase queryService;

    @GetMapping("/email")
    public ResponseEntity<ApiResponse<EmailExistResponse>> getEmailAvailable(@RequestParam("email") @Email @NotNull String email) {
        boolean res = queryService.checkEmailExists(email);
        return ResponseEntity.ok(ApiResponse.onSuccess(new EmailExistResponse(res)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> postNewUser(@Validated @RequestBody SignUpCommand command) {
        commandService.signUp(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.onSuccessCreated());
    }

    @GetMapping("/github")
    public ResponseEntity<ApiResponse<GithubInfoResponse>> getMyGithubToken(@AuthenticationPrincipal AuthenticatedUserDetails user) {
        return ResponseEntity.ok(ApiResponse.onSuccess(queryService.queryGithubToken(user.getId())));
    }
}
