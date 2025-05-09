package com.retailsoft.service;

import com.retailsoft.dto.RolDTO;

import java.util.List;

public interface RolService {

    List<RolDTO> listarTodos();
    List<RolDTO> buscarPorIds(List<Long> id);
}
