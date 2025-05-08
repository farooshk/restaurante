package com.retailsoft.controller;

import com.retailsoft.dto.ProductoDTO;
import com.retailsoft.service.CategoriaService;
import com.retailsoft.service.FileStorageService;
import com.retailsoft.service.IngredienteService;
import com.retailsoft.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public String listarProductos(Model model) {
        List<ProductoDTO> productos = productoService.listarTodos();
        model.addAttribute("productos", productos);
        return "admin/productos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("producto", new ProductoDTO());
        cargarDatosComboBox(model);
        return "admin/productos/form";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ProductoDTO> productoOpt = productoService.buscarPorId(id);

        if (productoOpt.isPresent()) {
            model.addAttribute("producto", productoOpt.get());
            cargarDatosComboBox(model);
            return "admin/productos/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "El producto no existe");
            return "redirect:/admin/productos";
        }
    }

    @PostMapping("/guardar")
    public String guardarProducto(
            @Valid @ModelAttribute("producto") ProductoDTO producto,
            BindingResult result,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            @RequestParam(value = "ingredientesIds", required = false) List<Long> ingredientesIds,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarDatosComboBox(model);
            return "admin/productos/form";
        }

        try {
            // Manejar la imagen si se proporciona una nueva
            if (imagenFile != null && !imagenFile.isEmpty()) {
                String rutaImagen = fileStorageService.almacenarArchivo(imagenFile, "productos");
                producto.setUrlImagen(rutaImagen);
            }

            // Asignar ingredientes
            if (ingredientesIds != null && !ingredientesIds.isEmpty()) {
                producto.setIngredientes(ingredienteService.buscarPorIds(ingredientesIds));
            } else {
                producto.setIngredientes(new ArrayList<>());
            }

            productoService.guardar(producto);
            redirectAttributes.addFlashAttribute("mensaje", "Producto guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el producto: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            // Verificar si el producto está en algún pedido
            if (productoService.estaEnPedidos(id)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "No se puede eliminar el producto porque está asociado a pedidos"));
            }

            productoService.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar el producto: " + e.getMessage()));
        }
    }

    private void cargarDatosComboBox(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("ingredientes", ingredienteService.listarTodos());
    }
}
