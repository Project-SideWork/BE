package com.sidework.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FcmTestController {

	@GetMapping("/fcm-test")
	public String fcmTest() {
		return "forward:/fcm-test.html";
	}
}
