package com.retailsoft.controller;

import com.retailsoft.dto.IngredienteDTO;
import com.retailsoft.service.IngredienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/guardar")
    public String guardarIngrediente(@ModelAttribute IngredienteDTO ingrediente) {
        ingredienteService.guardar(ingrediente);
        return "redirect:/admin/ingredientes";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminarIngrediente(@PathVariable Long id) {
        ingredienteService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
