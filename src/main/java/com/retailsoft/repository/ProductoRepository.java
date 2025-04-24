package com.retailsoft.repository;

import com.retailsoft.entity.Categoria;
import com.retailsoft.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCategoriaOrderByNombreAsc(Categoria categoria);
    List<Producto> findByActivoTrueOrderByNombreAsc();
    List<Producto> findByActivoTrueAndCategoriaOrderByNombreAsc(Categoria categoria);
}
