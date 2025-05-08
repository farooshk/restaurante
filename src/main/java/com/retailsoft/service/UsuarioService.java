package com.retailsoft.service;

import com.retailsoft.dto.UsuarioDTO;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<UsuarioDTO> listarTodos();
    Optional<UsuarioDTO> buscarPorId(Long id);
    UsuarioDTO guardar(UsuarioDTO usuarioDTO, String password);
    void eliminar(Long id);
    List<UsuarioDTO> listarUsuariosQueTomanPedidos();
    boolean tienePedidos(Long id);
    Optional<UsuarioDTO> buscarPorUsername(String username);
    void inactivar(Long id);
    boolean existePorUsername(String username);
    UsuarioDTO guardar(UsuarioDTO usuarioDTO);
}
