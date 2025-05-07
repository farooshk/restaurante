package com.retailsoft.service;

import com.retailsoft.dto.ProductoDTO;

import java.util.List;
import java.util.Optional;

public interface ProductoService {

    List<ProductoDTO> listarTodos();
    List<ProductoDTO> listarActivos();
    List<ProductoDTO> listarPorCategoria(Long categoriaId);
    Optional<ProductoDTO> buscarPorId(Long id);
    ProductoDTO guardar(ProductoDTO productoDTO);
    void eliminar(Long id);
    void cambiarEstado(Long id, boolean activo);
}
