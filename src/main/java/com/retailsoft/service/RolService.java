package com.retailsoft.service;

import com.retailsoft.dto.RolDTO;
import com.retailsoft.entity.Rol;

import java.util.List;

public interface RolService {

    Rol listarTodos();
    List<RolDTO> buscarPorIds(List<Long> id);
}
