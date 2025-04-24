package com.retailsoft.controller;

import com.retailsoft.dto.CategoriaDTO;
import com.retailsoft.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/admin/categorias")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listarCategorias(Model model) {
        List<CategoriaDTO> categorias = categoriaService.listarTodas();
        model.addAttribute("categorias", categorias);
        return "admin/categorias/lista";
    }

    @PostMapping("/guardar")
    public String guardarCategoria(@ModelAttribute CategoriaDTO categoria) {
        categoriaService.guardar(categoria);
        return "redirect:/admin/categorias";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
