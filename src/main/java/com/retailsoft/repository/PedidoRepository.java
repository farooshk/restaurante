package com.retailsoft.repository;

import com.retailsoft.entity.Pedido;
import com.retailsoft.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    boolean existsByItemsProductoId(Long id);
    boolean existsByUsuarioId(Long id);

    List<Pedido> findByUsuarioAndFechaHoraBetweenOrderByFechaHoraDesc(Usuario usuario, LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT p FROM Pedido p WHERE p.fechaHora BETWEEN ?1 AND ?2 AND p.anulado = false ORDER BY p.fechaHora DESC")
    List<Pedido> findPedidosDelDia(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.fechaHora BETWEEN ?1 AND ?2 AND p.anulado = false")
    Double obtenerTotalVentasDiarias(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT new map(i.producto.categoria.nombre as categoria, SUM(i.cantidad * i.precioUnitario) as total) " +
            "FROM ItemPedido i " +
            "WHERE i.pedido.fechaHora BETWEEN :inicio AND :fin " +
            "AND (:meseroId IS NULL OR i.pedido.usuario.id = :meseroId) " +
            "AND i.pedido.anulado = false " +
            "GROUP BY i.producto.categoria.nombre")
    List<Map<String, Object>> obtenerVentasPorCategoriaYMesero(@Param("inicio") LocalDateTime inicio,
                                                               @Param("fin") LocalDateTime fin,
                                                               @Param("meseroId") Long meseroId);

    @Query("SELECT new map(FUNCTION('DATE', p.fechaHora) as fecha, SUM(p.total) as total) " +
            "FROM Pedido p WHERE p.fechaHora BETWEEN :inicio AND :fin " +
            "AND (:meseroId IS NULL OR p.usuario.id = :meseroId) " +
            "AND p.anulado = false GROUP BY FUNCTION('DATE', p.fechaHora)")
    List<Map<String, Object>> obtenerVentasPorDiaYMesero(LocalDateTime inicio, LocalDateTime fin, Long meseroId);

    @Query("SELECT new map(p.producto.categoria.nombre as categoria, SUM(p.cantidad * p.precioUnitario) as total) " +
            "FROM ItemPedido p WHERE p.pedido.fechaHora BETWEEN :inicio AND :fin GROUP BY p.producto.categoria.nombre")
    List<Map<String, Object>> obtenerVentasPorCategoria(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT new map(FUNCTION('DATE', p.fechaHora) as fecha, SUM(p.total) as total) " +
            "FROM Pedido p WHERE p.fechaHora BETWEEN :inicio AND :fin GROUP BY FUNCTION('DATE', p.fechaHora) ORDER BY fecha")
    List<Map<String, Object>> obtenerVentasPorDia(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
