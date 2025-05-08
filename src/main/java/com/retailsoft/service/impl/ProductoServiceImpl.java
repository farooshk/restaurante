package com.retailsoft.service.impl;

import com.retailsoft.dto.IngredienteDTO;
import com.retailsoft.dto.ProductoDTO;
import com.retailsoft.entity.Ingrediente;
import com.retailsoft.entity.Producto;
import com.retailsoft.repository.PedidoRepository;
import com.retailsoft.repository.ProductoRepository;
import com.retailsoft.service.FileStorageService;
import com.retailsoft.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private PedidoRepository pedidoRepository; // Para verificar si el producto está en pedidos

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductoDTO> buscarPorId(Long id) {
        return productoRepository.findById(id).map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaEnPedidos(Long id) {
        // Implementar lógica para verificar si el producto está en algún pedido
        return pedidoRepository.existsByItemsPedidoProductoId(id);
    }

    @Override
    @Transactional
    public ProductoDTO guardar(ProductoDTO productoDTO) {
        Producto producto;
        String imagenAnterior = null;

        // Si es actualización, obtener la entidad existente
        if (productoDTO.getId() != null) {
            producto = productoRepository.findById(productoDTO.getId())
                    .orElse(new Producto());

            // Guardar la URL de la imagen anterior
            imagenAnterior = producto.getUrlImagen();

            // Si no se proporciona una nueva imagen, mantener la existente
            if (productoDTO.getUrlImagen() == null || productoDTO.getUrlImagen().isEmpty()) {
                productoDTO.setUrlImagen(imagenAnterior);
            }
        } else {
            producto = new Producto();
        }

        // Actualizar propiedades básicas
        producto.setNombre(productoDTO.getNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setDisponible(productoDTO.isDisponible());
        producto.setUrlImagen(productoDTO.getUrlImagen());

        // Actualizar categoría
        if (productoDTO.getCategoria() != null && productoDTO.getCategoria().getId() != null) {
            Categoria categoria = new Categoria();
            categoria.setId(productoDTO.getCategoria().getId());
            producto.setCategoria(categoria);
        }

        // Actualizar ingredientes
        if (productoDTO.getIngredientes() != null) {
            Set<Ingrediente> ingredientes = productoDTO.getIngredientes().stream()
                    .map(dto -> {
                        Ingrediente ingrediente = new Ingrediente();
                        ingrediente.setId(dto.getId());
                        return ingrediente;
                    })
                    .collect(Collectors.toSet());
            producto.setIngredientes(ingredientes);
        }

        // Guardar y convertir a DTO
        producto = productoRepository.save(producto);

        // Si se actualizó la imagen, eliminar la anterior
        if (imagenAnterior != null && !imagenAnterior.equals(producto.getUrlImagen())) {
            fileStorageService.eliminarArchivo(imagenAnterior);
        }

        return convertirADTO(producto);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        // Obtener el producto para eliminar su imagen
        productoRepository.findById(id).ifPresent(producto -> {
            if (producto.getUrlImagen() != null) {
                fileStorageService.eliminarArchivo(producto.getUrlImagen());
            }
            productoRepository.deleteById(id);
        });
    }

    private ProductoDTO convertirADTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setDisponible(producto.isDisponible());
        dto.setUrlImagen(producto.getUrlImagen());

        // Convertir categoría
        if (producto.getCategoria() != null) {
            CategoriaDTO categoriaDTO = new CategoriaDTO();
            categoriaDTO.setId(producto.getCategoria().getId());
            categoriaDTO.setNombre(producto.getCategoria().getNombre());
            dto.setCategoria(categoriaDTO);
        }

        // Convertir ingredientes
        if (producto.getIngredientes() != null) {
            List<IngredienteDTO> ingredientesDTO = producto.getIngredientes().stream()
                    .map(ingrediente -> {
                        IngredienteDTO ingredienteDTO = new IngredienteDTO();
                        ingredienteDTO.setId(ingrediente.getId());
                        ingredienteDTO.setNombre(ingrediente.getNombre());
                        return ingredienteDTO;
                    })
                    .collect(Collectors.toList());
            dto.setIngredientes(ingredientesDTO);
        }

        return dto;
    }
}
