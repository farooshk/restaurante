package com.retailsoft.controller;

import com.retailsoft.dto.UsuarioDTO;
import com.retailsoft.service.RolService;
import com.retailsoft.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    @GetMapping
    public String listarUsuarios(Model model) {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new UsuarioDTO());
        model.addAttribute("roles", rolService.listarTodos());
        return "admin/usuarios/form";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<UsuarioDTO> usuarioOpt = usuarioService.buscarPorId(id);

        if (usuarioOpt.isPresent()) {
            model.addAttribute("usuario", usuarioOpt.get());
            model.addAttribute("roles", rolService.listarTodos());
            return "admin/usuarios/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "El usuario no existe");
            return "redirect:/admin/usuarios";
        }
    }

    @PostMapping("/guardar")
    public String guardarUsuario(
            @Valid @ModelAttribute("usuario") UsuarioDTO usuario,
            BindingResult result,
            @RequestParam(value = "rolesIds", required = false) List<Long> rolesIds,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validación personalizada para nuevos usuarios
        if (usuario.getId() == null && (usuario.getPassword() == null || usuario.getPassword().isEmpty())) {
            result.rejectValue("password", "NotEmpty", "La contraseña es obligatoria para nuevos usuarios");
        }

        // Si hay errores, volver al formulario
        if (result.hasErrors()) {
            model.addAttribute("roles", rolService.listarTodos());
            return "admin/usuarios/form";
        }

        try {
            // Asignar roles
            if (rolesIds != null && !rolesIds.isEmpty()) {
                usuario.setRoles(rolService.buscarPorIds(rolesIds));
            } else {
                usuario.setRoles(new ArrayList<>());
            }

            usuarioService.guardar(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el usuario: " + e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            // Verificar si el usuario tiene pedidos
            if (usuarioService.tienePedidos(id)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "No se puede eliminar el usuario porque tiene pedidos asociados"));
            }

            usuarioService.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar el usuario: " + e.getMessage()));
        }
    }
}
