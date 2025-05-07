package com.retailsoft.service;

import com.retailsoft.dto.IngredienteDTO;

import java.util.List;
import java.util.Optional;

public interface IngredienteService {

    List<IngredienteDTO> listarTodos();
    List<IngredienteDTO> listarAdicionales();
    Optional<IngredienteDTO> buscarPorId(Long id);
    IngredienteDTO guardar(IngredienteDTO ingredienteDTO);
    void eliminar(Long id);
}
