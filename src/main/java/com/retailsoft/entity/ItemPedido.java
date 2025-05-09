package com.retailsoft.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = {"pedido", "producto", "ingredientesAdicionales", "ingredientesEliminados"}) // Excluir todas las relaciones
@NoArgsConstructor
@Entity
@Table(name = "items_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Integer precioUnitario;

    private String observaciones;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "items_pedido_ingredientes_adicionales",
            joinColumns = @JoinColumn(name = "item_pedido_id"),
            inverseJoinColumns = @JoinColumn(name = "ingrediente_id")
    )
    private Set<Ingrediente> ingredientesAdicionales = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "items_pedido_ingredientes_eliminados",
            joinColumns = @JoinColumn(name = "item_pedido_id"),
            inverseJoinColumns = @JoinColumn(name = "ingrediente_id")
    )
    private Set<Ingrediente> ingredientesEliminados = new HashSet<>();

    // Constructor con ID para referencias
    public ItemPedido(Long id) {
        this.id = id;
    }

    // Constructor con campos principales
    public ItemPedido(Long id, Pedido pedido, Producto producto, Integer cantidad,
                      Integer precioUnitario, String observaciones) {
        this.id = id;
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.observaciones = observaciones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemPedido itemPedido = (ItemPedido) o;
        return id != null && Objects.equals(id, itemPedido.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 31;
    }

    // Método para calcular el subtotal del item
    public Integer calcularSubtotal() {
        int cantidadSafe = cantidad != null ? cantidad : 0;

        // Subtotal = precioUnitario * cantidad
        int subtotal = (precioUnitario != null ? precioUnitario : 0) * cantidadSafe;

        // Sumar el precio de los ingredientes adicionales
        int adicionalesPorUnidad = 0;

        // Modificar para manejar posible LazyInitializationException
        if (ingredientesAdicionales instanceof org.hibernate.collection.spi.PersistentCollection &&
                ((org.hibernate.collection.spi.PersistentCollection) ingredientesAdicionales).wasInitialized()) {
            adicionalesPorUnidad = ingredientesAdicionales.stream()
                    .map(ingrediente -> ingrediente.getPrecioPorcion() != null ? ingrediente.getPrecioPorcion() : 0)
                    .reduce(0, Integer::sum);
        }

        int adicionales = adicionalesPorUnidad * cantidadSafe;

        return subtotal + adicionales;
    }
}
