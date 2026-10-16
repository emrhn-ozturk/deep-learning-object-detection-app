package com.example.deep_learning_app;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {

        Object authenticated = session.getAttribute("authenticated");
        System.out.println("AUTH CHECK: " + authenticated);

        if (!Boolean.TRUE.equals(authenticated)) {
            return "redirect:/";
        }

        return "dashboard";
    }
}

