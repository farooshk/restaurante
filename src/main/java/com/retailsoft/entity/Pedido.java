package com.retailsoft.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString(exclude = {"usuario", "items"}) // Excluir las relaciones
@NoArgsConstructor
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

    // Constructor con ID para referencias
    public Pedido(Long id) {
        this.id = id;
    }

    // Constructor con campos principales
    public Pedido(Long id, LocalDateTime fechaHora, String mesa, Usuario usuario,
                  Integer total, EstadoPedido estado) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.mesa = mesa;
        this.usuario = usuario;
        this.total = total;
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return id != null && Objects.equals(id, pedido.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 31;
    }

    // Método para calcular el total del pedido con manejo seguro de la colección
    public Integer calcularTotal() {
        // Verificar si items es null para evitar NullPointerException
        if (items == null) {
            return 0;
        }

        return items.stream()
                .map(item -> item.calcularSubtotal() != null ? item.calcularSubtotal() : 0)
                .reduce(0, Integer::sum);
    }

    // Método para actualizar el total
    public void actualizarTotal() {
        this.total = calcularTotal();
    }
}
