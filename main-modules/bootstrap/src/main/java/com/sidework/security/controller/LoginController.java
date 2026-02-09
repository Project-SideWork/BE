package com.sidework.security.controller;

import com.sidework.security.dto.LoginCommand;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class LoginController {

    @PostMapping("/login")
    public void login(@RequestBody LoginCommand command) {

    }
}