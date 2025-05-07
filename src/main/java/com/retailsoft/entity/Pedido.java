package com.retailsoft.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private String mesa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> items = new ArrayList<>();

    @Column(nullable = false)
    private Integer total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    private boolean comandaImpresa = false;

    private boolean anulado = false;

    private String motivoAnulacion;

    public enum EstadoPedido {
        CREADO, EN_PREPARACION, LISTO, ENTREGADO, PAGADO, ANULADO
    }

    // Método para calcular el total del pedido
    public Integer calcularTotal() {
        return items.stream()
                .map(item -> item.calcularSubtotal() != null ? item.calcularSubtotal() : 0)
                .reduce(0, Integer::sum);
    }

    // Método para actualizar el total
    public void actualizarTotal() {
        this.total = calcularTotal();
    }
}
