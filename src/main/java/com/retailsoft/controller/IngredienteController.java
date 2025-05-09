package com.retailsoft.controller;

import com.retailsoft.dto.IngredienteDTO;
import com.retailsoft.service.IngredienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/ingredientes")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class IngredienteController {

    @Autowired
    private IngredienteService ingredienteService;

    @GetMapping
    public String listarIngredientes(Model model) {
        List<IngredienteDTO> ingredientes = ingredienteService.listarTodos();
        model.addAttribute("ingredientes", ingredientes);
        return "admin/ingredientes/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("ingrediente", new IngredienteDTO());
        return "admin/ingredientes/form";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<IngredienteDTO> ingredienteOpt = ingredienteService.buscarPorId(id);

        if (ingredienteOpt.isPresent()) {
            model.addAttribute("ingrediente", ingredienteOpt.get());
            return "admin/ingredientes/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "El ingrediente no existe");
            return "redirect:/admin/ingredientes";
        }
    }

    @PostMapping("/guardar")
    public String guardarIngrediente(
            @Valid @ModelAttribute("ingrediente") IngredienteDTO ingrediente,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el ingrediente");
            return "admin/ingredientes/form";
        }

        try {
            ingredienteService.guardar(ingrediente);
            redirectAttributes.addFlashAttribute("mensaje", "Ingrediente guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el ingrediente: " + e.getMessage());
        }

        return "redirect:/admin/ingredientes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarIngrediente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (ingredienteService.estaEnProductos(id)) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar el ingrediente porque se encuentra asociado a productos");
        } else {
            ingredienteService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado exitosamente.");
        }
        return "redirect:/admin/ingredientes";
    }
}
