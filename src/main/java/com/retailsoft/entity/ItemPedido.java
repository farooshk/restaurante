package com.retailsoft.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    // Método para calcular el subtotal del item
    public Integer calcularSubtotal() {
        int cantidadSafe = cantidad != null ? cantidad : 0;

        // Subtotal = precioUnitario * cantidad
        int subtotal = (precioUnitario != null ? precioUnitario : 0) * cantidadSafe;

        // Sumar el precio de los ingredientes adicionales
        int adicionalesPorUnidad = ingredientesAdicionales.stream()
                .map(ingrediente -> ingrediente.getPrecioPorcion() != null ? ingrediente.getPrecioPorcion() : 0)
                .reduce(0, Integer::sum);

        int adicionales = adicionalesPorUnidad * cantidadSafe;

        return subtotal + adicionales;
    }
}
