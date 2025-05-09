package com.retailsoft.controller;

import com.retailsoft.dto.CategoriaDTO;
import com.retailsoft.service.CategoriaService;
import com.retailsoft.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/categorias")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public String listarCategorias(Model model) {
        List<CategoriaDTO> categorias = categoriaService.listarTodas();
        model.addAttribute("categorias", categorias);
        model.addAttribute("nuevaCategoria", new CategoriaDTO());
        return "admin/categorias/lista";
    }

    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute("categoria", new CategoriaDTO());
        return "admin/categorias/form";
    }

    @GetMapping("/editar/{id}")
    public String editarCategoria(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<CategoriaDTO> categoriaOpt = categoriaService.buscarPorId(id);

        if (categoriaOpt.isPresent()) {
            model.addAttribute("categoria", categoriaOpt.get());
            return "admin/categorias/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "La categoría no existe");
            return "redirect:/admin/categorias";
        }
    }

    @PostMapping("/guardar")
    public String guardarCategoria(
            @Valid @ModelAttribute("categoria") CategoriaDTO categoria,
            BindingResult result,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/categorias/form";
        }

        try {
            // Manejar la imagen si se proporciona una nueva
            if (imagenFile != null && !imagenFile.isEmpty()) {
                String rutaImagen = fileStorageService.almacenarArchivo(imagenFile, "categorias");
                categoria.setUrlImagen(rutaImagen);
            }

            categoriaService.guardar(categoria);
            redirectAttributes.addFlashAttribute("mensaje", "Categoría guardada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la categoría: " + e.getMessage());
        }

        return "redirect:/admin/categorias";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (categoriaService.tieneProductosAsociados(id)) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar la categoría porque tiene productos asociados");
        } else {
            categoriaService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Categoría eliminada exitosamente.");
        }
        return "redirect:/admin/categorias";
    }
}
