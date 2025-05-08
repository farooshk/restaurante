package com.retailsoft.service.impl;

import com.retailsoft.dto.IngredienteDTO;
import com.retailsoft.entity.Ingrediente;
import com.retailsoft.repository.IngredienteRepository;
import com.retailsoft.repository.ProductoRepository;
import com.retailsoft.service.IngredienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IngredienteServiceImpl implements IngredienteService {

    @Autowired
    private IngredienteRepository ingredienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<IngredienteDTO> listarTodos() {
        return ingredienteRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IngredienteDTO> buscarPorId(Long id) {
        return ingredienteRepository.findById(id).map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredienteDTO> buscarPorIds(List<Long> ids) {
        return ingredienteRepository.findAllById(ids).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaEnProductos(Long id) {
        // Verificar si el ingrediente está en algún producto
        return productoRepository.existsByIngredientesId(id);
    }

    @Override
    @Transactional
    public IngredienteDTO guardar(IngredienteDTO ingredienteDTO) {
        Ingrediente ingrediente;

        // Si es actualización, obtener la entidad existente
        if (ingredienteDTO.getId() != null) {
            ingrediente = ingredienteRepository.findById(ingredienteDTO.getId())
                    .orElse(new Ingrediente());
        } else {
            ingrediente = new Ingrediente();
        }

        // Actualizar propiedades
        ingrediente.setNombre(ingredienteDTO.getNombre());
        ingrediente.setDescripcion(ingredienteDTO.getDescripcion());
        ingrediente.setDisponible(ingredienteDTO.isDisponible());

        // Guardar y convertir a DTO
        ingrediente = ingredienteRepository.save(ingrediente);

        return convertirADTO(ingrediente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        ingredienteRepository.deleteById(id);
    }

    private IngredienteDTO convertirADTO(Ingrediente ingrediente) {
        IngredienteDTO dto = new IngredienteDTO();
        dto.setId(ingrediente.getId());
        dto.setNombre(ingrediente.getNombre());
        dto.setDescripcion(ingrediente.getDescripcion());
        dto.setDisponible(ingrediente.isDisponible());
        dto.setCantidadProductos(ingrediente.getProductos() != null ? ingrediente.getProductos().size() : 0);
        return dto;
    }
}
