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
@ToString(exclude = {"categoria", "ingredientes"}) // Excluir relaciones para evitar problemas de carga lazy
@NoArgsConstructor
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Integer precio;

    private String descripcion;

    private boolean disponible = true;

    private String urlImagen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "productos_ingredientes",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "ingrediente_id")
    )
    private Set<Ingrediente> ingredientes = new HashSet<>();

    public Producto(Long id) {
        this.id = id;
    }

    // Constructor con todos los campos si es necesario
    public Producto(Long id, String nombre, Integer precio, String descripcion,
                    boolean disponible, String urlImagen, Categoria categoria) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.disponible = disponible;
        this.urlImagen = urlImagen;
        this.categoria = categoria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return id != null && Objects.equals(id, producto.id);
    }

    @Override
    public int hashCode() {
        // Usar un valor constante para evitar problemas con entidades no persistidas
        return id != null ? Objects.hash(id) : 31;
    }
}
