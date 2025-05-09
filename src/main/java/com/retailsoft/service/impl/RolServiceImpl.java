package com.retailsoft.service.impl;

import com.retailsoft.dto.RolDTO;
import com.retailsoft.entity.Rol;
import com.retailsoft.repository.RolRepository;
import com.retailsoft.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> listarTodos() {
        return rolRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> buscarPorIds(List<Long> ids) {
        return rolRepository.findAllById(ids).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private RolDTO convertirADTO(Rol rol) {
        RolDTO dto = new RolDTO();
        dto.setId(rol.getId());
        dto.setNombre(rol.getNombre());
        return dto;
    }
}
