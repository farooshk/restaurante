package com.retailsoft.service;

import com.retailsoft.dto.CategoriaDTO;

import java.util.List;
import java.util.Optional;

public interface CategoriaService {

    List<CategoriaDTO> listarTodas();
    Optional<CategoriaDTO> buscarPorId(Long id);
    CategoriaDTO guardar(CategoriaDTO categoriaDTO);
    void eliminar(Long id);
    boolean tieneProductosAsociados(Long id);
}
