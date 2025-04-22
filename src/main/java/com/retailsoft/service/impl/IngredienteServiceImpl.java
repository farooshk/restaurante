package com.retailsoft.service.impl;

import com.retailsoft.dto.IngredienteDTO;
import com.retailsoft.entity.Ingrediente;
import com.retailsoft.repository.IngredienteRepository;
import com.retailsoft.service.IngredienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IngredienteServiceImpl implements IngredienteService {

    private final IngredienteRepository ingredienteRepository;

    @Autowired
    public IngredienteServiceImpl(IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredienteDTO> listarTodos() {
        return ingredienteRepository.findAllByOrderByNombreAsc().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredienteDTO> listarAdicionales() {
        return ingredienteRepository.findByEsAdicionalTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IngredienteDTO> buscarPorId(Long id) {
        return ingredienteRepository.findById(id).map(this::convertirADTO);
    }

    @Override
    @Transactional
    public IngredienteDTO guardar(IngredienteDTO ingredienteDTO) {
        Ingrediente ingrediente = convertirAEntidad(ingredienteDTO);
        ingrediente = ingredienteRepository.save(ingrediente);
        return convertirADTO(ingrediente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        ingredienteRepository.deleteById(id);
    }

    private IngredienteDTO convertirADTO(Ingrediente ingrediente) {
        return IngredienteDTO.builder()
                .id(ingrediente.getId())
                .nombre(ingrediente.getNombre())
                .precioPorcion(ingrediente.getPrecioPorcion())
                .esAdicional(ingrediente.isEsAdicional())
                .build();
    }

    private Ingrediente convertirAEntidad(IngredienteDTO dto) {
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setId(dto.getId());
        ingrediente.setNombre(dto.getNombre());
        ingrediente.setPrecioPorcion(dto.getPrecioPorcion());
        ingrediente.setEsAdicional(dto.isEsAdicional());
        return ingrediente;
    }
}
