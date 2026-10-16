
package com.example.deep_learning_app;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;

@Controller
public class OtpController {

    @GetMapping("/verify-otp")
    public String showOtpPage(HttpSession session) {
        if (session.getAttribute("otpCode") == null) {
            return "redirect:/";
        }

        return "otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String otp,
            HttpSession session,
            Model model) {

        String storedOtp = (String) session.getAttribute("otpCode");
        Instant expiry = (Instant) session.getAttribute("otpExpiry");

        if (storedOtp == null || expiry == null) {
            return "redirect:/";
        }

        if (Instant.now().isAfter(expiry)) {
            clearOtp(session);
            model.addAttribute("error", "OTP süresi doldu. Yeniden giriş yap.");
            return "otp";
        }

        int attempts = (int) session.getAttribute("otpAttempts");

        if (attempts >= 3) {
            clearOtp(session);
            return "redirect:/";
        }

        if (!storedOtp.equals(otp)) {
            attempts++;
            session.setAttribute("otpAttempts", attempts);

            if (attempts >= 3) {
                clearOtp(session);
                return "redirect:/";
            }

            model.addAttribute(
                    "error",
                    "Hatalı OTP. Kalan deneme hakkın: " + (3 - attempts)
            );

            return "otp";
        }

        clearOtp(session);
        session.setAttribute("authenticated", true);

        return "redirect:/dashboard";
    }

    private void clearOtp(HttpSession session) {
        session.removeAttribute("otpCode");
        session.removeAttribute("otpExpiry");
        session.removeAttribute("otpAttempts");
    }
}

