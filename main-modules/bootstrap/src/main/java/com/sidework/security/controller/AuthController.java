package com.sidework.security.controller;

import com.sidework.security.dto.LoginCommand;
import com.sidework.security.docs.AuthControllerDocs;
import org.springframework.web.bind.annotation.*;

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
}
