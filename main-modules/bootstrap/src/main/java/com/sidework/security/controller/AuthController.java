package com.sidework.security.controller;

import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.security.dto.LoginCommand;
import com.sidework.security.docs.AuthControllerDocs;
import com.sidework.security.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class AuthController implements AuthControllerDocs {

    @PostMapping("/login")
    public void login(@RequestBody LoginCommand command) {

    }

    @PostMapping("/logout")
    public void logout() {

    }

    @PostMapping("/reissue")
    public void reissueToken() {

    }

    @GetMapping("/login/github")
    public void linkGithub(
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/github");
    }
}
