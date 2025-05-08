package com.retailsoft.service.impl;

import com.retailsoft.dto.CategoriaDTO;
import com.retailsoft.entity.Categoria;
import com.retailsoft.repository.CategoriaRepository;
import com.retailsoft.service.CategoriaService;
import com.retailsoft.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, FileStorageService fileStorageService) {
        this.categoriaRepository = categoriaRepository;
        this.fileStorageService = fileStorageService;
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
    @Transactional(readOnly = true)
    public boolean tieneProductosAsociados(Long id) {
        Optional<Categoria> categoriaOpt = categoriaRepository.findById(id);
        return categoriaOpt.map(c -> !c.getProductos().isEmpty()).orElse(false);
    }

    @Override
    @Transactional
    public CategoriaDTO guardar(CategoriaDTO categoriaDTO) {
        Categoria categoria;
        String imagenAnterior = null;

        // Si es actualización, obtener la entidad existente
        if (categoriaDTO.getId() != null) {
            categoria = categoriaRepository.findById(categoriaDTO.getId())
                    .orElse(new Categoria());

            // Guardar la URL de la imagen anterior
            imagenAnterior = categoria.getUrlImagen();

            // Si no se proporciona una nueva imagen, mantener la existente
            if (categoriaDTO.getUrlImagen() == null || categoriaDTO.getUrlImagen().isEmpty()) {
                categoriaDTO.setUrlImagen(imagenAnterior);
            }
        } else {
            categoria = new Categoria();
        }

        // Actualizar propiedades
        categoria.setNombre(categoriaDTO.getNombre());
        categoria.setUrlImagen(categoriaDTO.getUrlImagen());

        // Guardar y convertir a DTO
        categoria = categoriaRepository.save(categoria);

        // Si se actualizó la imagen, eliminar la anterior
        if (imagenAnterior != null && !imagenAnterior.equals(categoria.getUrlImagen())) {
            fileStorageService.eliminarArchivo(imagenAnterior);
        }

        return convertirADTO(categoria);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        // Obtener la categoría para eliminar su imagen
        categoriaRepository.findById(id).ifPresent(categoria -> {
            if (categoria.getUrlImagen() != null) {
                fileStorageService.eliminarArchivo(categoria.getUrlImagen());
            }
            categoriaRepository.deleteById(id);
        });
    }

    private CategoriaDTO convertirADTO(Categoria categoria) {
        return CategoriaDTO.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .urlImagen(categoria.getUrlImagen())
                .cantidadProductos(categoria.getProductos().size())
                .build();
    }
}
