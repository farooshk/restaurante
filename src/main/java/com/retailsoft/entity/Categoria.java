package com.retailsoft.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString(exclude = {"productos"}) // Excluir la colección para evitar problemas de carga lazy
@NoArgsConstructor
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String urlImagen;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto> productos = new ArrayList<>();

    // Constructor con ID para referencias
    public Categoria(Long id) {
        this.id = id;
    }

    // Constructor con campos principales si lo necesitas
    public Categoria(Long id, String nombre, String urlImagen) {
        this.id = id;
        this.nombre = nombre;
        this.urlImagen = urlImagen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return id != null && Objects.equals(id, categoria.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 31;
    }
}
