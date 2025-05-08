package com.retailsoft.repository;

import com.retailsoft.dto.ProductoDTO;
import com.retailsoft.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<ProductoDTO> findByCategoriaId(Long id);
    boolean existsByIngredientesId(Long id);
}
