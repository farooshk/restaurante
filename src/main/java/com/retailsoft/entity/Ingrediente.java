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
@ToString(exclude = {"productos"}) // Excluir la colección lazy para evitar problemas
@NoArgsConstructor
@Entity
@Table(name = "ingredientes")
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    // Añadido campo descripción usado en el servicio
    private String descripcion;

    @Column(nullable = false)
    private Integer precioPorcion;

    // Cambiado de esAdicional a disponible para coincidir con el servicio
    private boolean disponible = true;

    // Manteniendo el campo original por si se usa en otra parte
    private boolean esAdicional = false;

    // Añadida relación con productos que se usa en convertirADTO
    @ManyToMany(mappedBy = "ingredientes", fetch = FetchType.LAZY)
    private Set<Producto> productos = new HashSet<>();

    // Constructor con ID para referencias
    public Ingrediente(Long id) {
        this.id = id;
    }

    // Constructor completo si lo necesitas
    public Ingrediente(Long id, String nombre, String descripcion, Integer precioPorcion,
                       boolean disponible, boolean esAdicional) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioPorcion = precioPorcion;
        this.disponible = disponible;
        this.esAdicional = esAdicional;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingrediente ingrediente = (Ingrediente) o;
        return id != null && Objects.equals(id, ingrediente.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 31;
    }
}
