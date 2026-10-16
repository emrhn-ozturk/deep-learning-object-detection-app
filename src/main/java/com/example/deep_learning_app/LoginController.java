package com.example.deep_learning_app;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;
import java.time.Instant;

@Controller
public class LoginController {

    private static final SecureRandom RANDOM = new SecureRandom();

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        session.removeAttribute("authenticated");
        session.removeAttribute("otpCode");
        session.removeAttribute("otpExpiry");
        session.removeAttribute("otpAttempts");

        if (username.equals("emir") && password.equals("123")) {

            int generatedOtp = 100000 + RANDOM.nextInt(900000);

            session.setAttribute("otpCode", String.valueOf(generatedOtp));
            session.setAttribute("otpExpiry", Instant.now().plusSeconds(300));
            session.setAttribute("otpAttempts", 0);

            System.out.println("Development OTP: " + generatedOtp);

            return "redirect:/verify-otp";
        }

        model.addAttribute("error", "Kullanıcı adı veya şifre hatalı.");

        return "index";
    }
}



