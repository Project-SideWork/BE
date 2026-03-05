package com.sidework.controller;

import com.sidework.controller.docs.FcmTestControllerDocs;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FcmTestController implements FcmTestControllerDocs {

	@GetMapping("/fcm-test")
	public String fcmTest() {
		return "forward:/fcm-test.html";
	}
}
