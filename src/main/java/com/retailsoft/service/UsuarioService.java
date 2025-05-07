package com.retailsoft.service;

import com.retailsoft.dto.UsuarioDTO;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<UsuarioDTO> listarTodos();
    Optional<UsuarioDTO> buscarPorId(Long id);
    Optional<UsuarioDTO> buscarPorUsername(String username);
    UsuarioDTO guardar(UsuarioDTO usuarioDTO, String password);
    void inactivar(Long id);
    void eliminar(Long id);
    boolean existePorUsername(String username);
    List<UsuarioDTO> listarUsuariosQueTomanPedidos();
}
