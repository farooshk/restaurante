package com.retailsoft.service.impl;

import com.retailsoft.dto.CategoriaDTO;
import com.retailsoft.entity.Categoria;
import com.retailsoft.repository.CategoriaRepository;
import com.retailsoft.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Autowired
    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarTodas() {
        return categoriaRepository.findAllByOrderByNombreAsc().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoriaDTO> buscarPorId(Long id) {
        return categoriaRepository.findById(id).map(this::convertirADTO);
    }

    @Override
    @Transactional
    public CategoriaDTO guardar(CategoriaDTO categoriaDTO) {
        Categoria categoria = convertirAEntidad(categoriaDTO);
        categoria = categoriaRepository.save(categoria);
        return convertirADTO(categoria);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        categoriaRepository.deleteById(id);
    }

    private CategoriaDTO convertirADTO(Categoria categoria) {
        return CategoriaDTO.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .urlImagen(categoria.getUrlImagen())
                .cantidadProductos(categoria.getProductos().size())
                .build();
    }

    private Categoria convertirAEntidad(CategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setId(dto.getId());
        categoria.setNombre(dto.getNombre());
        categoria.setUrlImagen(dto.getUrlImagen());
        return categoria;
    }
}
