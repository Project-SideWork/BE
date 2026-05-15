package com.sidework.user.application.adapter;

import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.common.exception.ResourceAlreadyExistException;
import com.sidework.common.mail.component.EmailHelper;
import com.sidework.common.response.ApiResponse;
import com.sidework.common.response.status.ErrorStatus;
import com.sidework.user.application.docs.UserControllerDocs;
import com.sidework.user.application.port.in.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController implements UserControllerDocs {
    private final UserCommandUseCase commandService;
    private final UserQueryUseCase queryService;
    private final EmailHelper emailHelper;


    @PostMapping("/email/validation")
    public ResponseEntity<ApiResponse<Void>> checkEmailAvailableAndSendCode(
            @RequestBody @Validated EmailCommand command
    ) {
        boolean exists = queryService.checkEmailExists(command.email());
        if (exists) {
            throw new ResourceAlreadyExistException(ErrorStatus.EMAIL_ALREADY_EXISTS);
        }

        emailHelper.processEmailCodeSend(command.email());
        return ResponseEntity.ok(ApiResponse.onSuccessVoid());
    }

    @PostMapping("/email/verification")
    public ResponseEntity<ApiResponse<Boolean>> verifyEmailCode(
            @RequestBody @Validated VerificationCodeCommand command
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(emailHelper.processVerify(command.email(), command.code())));
    }


    @PostMapping
    public ResponseEntity<ApiResponse<Void>> postNewUser(@RequestBody @Validated SignUpCommand command) {
        commandService.signUp(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.onSuccessCreated());
    }

    @GetMapping("/github")
    public GithubInfoResponse getMyGithubToken(@RequestHeader("X-User-Id") Long userId) {
        return queryService.queryGithubInformation(userId);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> getCurrentUser(@AuthenticationPrincipal AuthenticatedUserDetails user) {
        return ResponseEntity.ok(ApiResponse.onSuccess(queryService.queryUserSummary(user.getId())));
    }
}
