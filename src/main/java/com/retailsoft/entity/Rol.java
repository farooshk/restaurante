package com.retailsoft.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = "usuarios") // Excluye usuarios para evitar referencias circulares
@EqualsAndHashCode(of = "id") // Solo usa id para equals y hashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @ManyToMany(mappedBy = "roles")
    private Set<Usuario> usuarios = new HashSet<>();
}
