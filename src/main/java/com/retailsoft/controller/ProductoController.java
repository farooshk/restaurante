package com.retailsoft.controller;

import com.retailsoft.dto.CategoriaDTO;
import com.retailsoft.dto.IngredienteDTO;
import com.retailsoft.dto.ProductoDTO;
import com.retailsoft.service.CategoriaService;
import com.retailsoft.service.IngredienteService;
import com.retailsoft.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/productos")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private IngredienteService ingredienteService;

    @GetMapping
    public String listarProductos(Model model) {
        List<ProductoDTO> productos = productoService.listarTodos();
        List<CategoriaDTO> categorias = categoriaService.listarTodas();
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        return "admin/productos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoProducto(Model model) {
        List<CategoriaDTO> categorias = categoriaService.listarTodas();
        List<IngredienteDTO> ingredientes = ingredienteService.listarTodos();

        model.addAttribute("producto", new ProductoDTO());
        model.addAttribute("categorias", categorias);
        model.addAttribute("ingredientes", ingredientes);

        return "admin/productos/form";
    }

    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable Long id, Model model) {
        productoService.buscarPorId(id).ifPresent(producto -> {
            List<CategoriaDTO> categorias = categoriaService.listarTodas();
            List<IngredienteDTO> ingredientes = ingredienteService.listarTodos();

            model.addAttribute("producto", producto);
            model.addAttribute("categorias", categorias);
            model.addAttribute("ingredientes", ingredientes);
        });

        return "admin/productos/form";
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute ProductoDTO producto) {
        productoService.guardar(producto);
        return "redirect:/admin/productos";
    }

    @PutMapping("/{id}/estado")
    @ResponseBody
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        productoService.cambiarEstado(id, activo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
