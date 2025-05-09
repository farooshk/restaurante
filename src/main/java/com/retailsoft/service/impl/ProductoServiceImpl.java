package com.retailsoft.service.impl;

import com.retailsoft.dto.CategoriaDTO;
import com.retailsoft.dto.IngredienteDTO;
import com.retailsoft.dto.ProductoDTO;
import com.retailsoft.entity.Categoria;
import com.retailsoft.entity.Ingrediente;
import com.retailsoft.entity.Producto;
import com.retailsoft.repository.PedidoRepository;
import com.retailsoft.repository.ProductoRepository;
import com.retailsoft.service.FileStorageService;
import com.retailsoft.service.ProductoService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        List<Producto> productos = productoRepository.findAll();
        // Crear una lista nueva para desacoplar de la sesión de Hibernate
        List<Producto> productosSeguro = new ArrayList<>(productos);

        return productosSeguro.stream()
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
        return pedidoRepository.existsByItemsProductoId(id);
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

        // Convertir ingredientes - MODIFICAR ESTA PARTE
        if (producto.getIngredientes() != null) {
            try {
                // Primero, forzar la inicialización
                Hibernate.initialize(producto.getIngredientes());

                // Una vez inicializada, crear la lista de DTOs
                List<IngredienteDTO> ingredientesDTO = new ArrayList<>();

                for (Ingrediente ingrediente : producto.getIngredientes()) {
                    IngredienteDTO ingredienteDTO = new IngredienteDTO();
                    ingredienteDTO.setId(ingrediente.getId());
                    ingredienteDTO.setNombre(ingrediente.getNombre());
                    ingredienteDTO.setDescripcion(ingrediente.getDescripcion());
                    ingredienteDTO.setPrecioPorcion(ingrediente.getPrecioPorcion());
                    ingredienteDTO.setDisponible(ingrediente.isDisponible());
                    ingredienteDTO.setEsAdicional(ingrediente.isEsAdicional());
                    ingredienteDTO.setCantidadProductos(ingrediente.getProductos().size());
                    ingredientesDTO.add(ingredienteDTO);
                }

                dto.setIngredientes(ingredientesDTO);
            } catch (Exception e) {
                // Si hay algún problema, al menos establecer una lista vacía
                dto.setIngredientes(new ArrayList<>());
            }
        }

        return dto;
    }
}
