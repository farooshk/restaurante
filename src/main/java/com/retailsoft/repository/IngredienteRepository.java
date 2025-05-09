package com.retailsoft.repository;

import com.retailsoft.entity.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

    List<Ingrediente> findByEsAdicionalTrue();
    List<Ingrediente> findAllByOrderByNombreAsc();
    @Query("SELECT i FROM Ingrediente i LEFT JOIN FETCH i.productos")
    List<Ingrediente> findAllWithProductos();
}
