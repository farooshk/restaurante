package com.retailsoft.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String mostrarLogin(Model model, HttpSession session) {
        Object mensajeLogout = session.getAttribute("mensajeLogout");
        if (mensajeLogout != null) {
            model.addAttribute("mensajeLogout", mensajeLogout.toString());
            session.removeAttribute("mensajeLogout");
        }
        return "login";
    }

    @PostMapping("/login-error")
    public String loginError(RedirectAttributes attributes) {
        attributes.addFlashAttribute("error", "Credenciales incorrectas");
        return "redirect:/login";
    }

    @GetMapping("/acceso-denegado")
    public String accessDenied() {
        return "error/403";
    }
}
