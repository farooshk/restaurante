package com.retailsoft.service.impl;

import com.retailsoft.dto.IngredienteDTO;
import com.retailsoft.dto.ProductoDTO;
import com.retailsoft.entity.Ingrediente;
import com.retailsoft.entity.Producto;
import com.retailsoft.repository.CategoriaRepository;
import com.retailsoft.repository.IngredienteRepository;
import com.retailsoft.repository.ProductoRepository;
import com.retailsoft.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final IngredienteRepository ingredienteRepository;

    @Autowired
    public ProductoServiceImpl(ProductoRepository productoRepository,
                               CategoriaRepository categoriaRepository,
                               IngredienteRepository ingredienteRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.ingredienteRepository = ingredienteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarActivos() {
        return productoRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarPorCategoria(Long categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .map(categoria -> productoRepository.findByActivoTrueAndCategoriaOrderByNombreAsc(categoria).stream()
                        .map(this::convertirADTO)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductoDTO> buscarPorId(Long id) {
        return productoRepository.findById(id).map(this::convertirADTO);
    }

    @Override
    @Transactional
    public ProductoDTO guardar(ProductoDTO productoDTO) {
        Producto producto = convertirAEntidad(productoDTO);
        producto = productoRepository.save(producto);
        return convertirADTO(producto);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean activo) {
        productoRepository.findById(id).ifPresent(producto -> {
            producto.setActivo(activo);
            productoRepository.save(producto);
        });
    }

    private ProductoDTO convertirADTO(Producto producto) {
        ProductoDTO dto = ProductoDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .descripcion(producto.getDescripcion())
                .categoriaId(producto.getCategoria().getId())
                .categoriaNombre(producto.getCategoria().getNombre())
                .activo(producto.isActivo())
                .build();

        // Convertir ingredientes base
        dto.setIngredientesBase(producto.getIngredientesBase().stream()
                .map(this::convertirAIngredienteDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private Producto convertirAEntidad(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setId(dto.getId());
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setDescripcion(dto.getDescripcion());
        producto.setActivo(dto.isActivo());

        // Obtener la categoría
        if (dto.getCategoriaId() != null) {
            categoriaRepository.findById(dto.getCategoriaId())
                    .ifPresent(producto::setCategoria);
        }

        // Obtener ingredientes base
        if (dto.getIngredientesBase() != null && !dto.getIngredientesBase().isEmpty()) {
            Set<Ingrediente> ingredientes = new HashSet<>();
            for (IngredienteDTO ingredienteDTO : dto.getIngredientesBase()) {
                ingredienteRepository.findById(ingredienteDTO.getId())
                        .ifPresent(ingredientes::add);
            }
            producto.setIngredientesBase(ingredientes);
        }

        return producto;
    }

    private IngredienteDTO convertirAIngredienteDTO(Ingrediente ingrediente) {
        return IngredienteDTO.builder()
                .id(ingrediente.getId())
                .nombre(ingrediente.getNombre())
                .precioPorcion(ingrediente.getPrecioPorcion())
                .esAdicional(ingrediente.isEsAdicional())
                .build();
    }
}
