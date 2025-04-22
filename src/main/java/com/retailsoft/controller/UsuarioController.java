package com.retailsoft.controller;

import com.retailsoft.dto.UsuarioDTO;
import com.retailsoft.entity.Usuario;
import com.retailsoft.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarUsuarios(Model model) {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", new UsuarioDTO());
        model.addAttribute("tiposUsuario", Usuario.TipoUsuario.values());
        return "admin/usuarios/form";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        usuarioService.buscarPorId(id).ifPresent(usuario -> {
            model.addAttribute("usuario", usuario);
            model.addAttribute("tiposUsuario", Usuario.TipoUsuario.values());
        });
        return "admin/usuarios/form";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute UsuarioDTO usuario, @RequestParam(required = false) String password) {
        usuarioService.guardar(usuario, password);
        return "redirect:/admin/usuarios";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
