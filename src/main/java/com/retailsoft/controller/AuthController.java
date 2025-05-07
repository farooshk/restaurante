package com.retailsoft.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String mostrarLogin(Model model, HttpSession session) {
        Object mensajeLogout = session.getAttribute("mensajeLogout");
        if (mensajeLogout != null) {
            model.addAttribute("mensajeLogout", mensajeLogout.toString());
            session.removeAttribute("mensajeLogout");
        }

        Object mensajeError = session.getAttribute("mensajeError");
        if (mensajeError != null) {
            model.addAttribute("mensajeError", mensajeError.toString());
            session.removeAttribute("mensajeError");
        }

        return "login";
    }

    @GetMapping("/acceso-denegado")
    public String accessDenied() {
        return "error/403";
    }
}
