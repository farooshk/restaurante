package com.retailsoft.repository;

import com.retailsoft.entity.Pedido;
import com.retailsoft.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioAndFechaHoraBetweenOrderByFechaHoraDesc(Usuario usuario, LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT p FROM Pedido p WHERE p.fechaHora BETWEEN ?1 AND ?2 AND p.anulado = false ORDER BY p.fechaHora DESC")
    List<Pedido> findPedidosDelDia(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.fechaHora BETWEEN ?1 AND ?2 AND p.anulado = false")
    Double obtenerTotalVentasDiarias(LocalDateTime inicio, LocalDateTime fin);
}
