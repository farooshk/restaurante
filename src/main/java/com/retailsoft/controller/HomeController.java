package com.retailsoft.controller;

import com.retailsoft.dto.CategoriaDTO;
import com.retailsoft.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));

        List<CategoriaDTO> categorias = categoriaService.listarTodas();

        model.addAttribute("categorias", categorias);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("username", authentication.getName());

        return "index";
    }
}
