package com.retailsoft.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = "roles") // Excluir roles para evitar referencias circulares
@EqualsAndHashCode(of = "id") // Usar solo el id para equals y hashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean activo = true;

    // Mantenemos tipoUsuario por compatibilidad, pero usaremos roles para autorización
    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;

    // Relación con Rol
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "usuarios_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();

    public enum TipoUsuario {
        ADMINISTRADOR, MESERO
    }
}
