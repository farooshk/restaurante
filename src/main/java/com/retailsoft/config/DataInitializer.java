package com.retailsoft.config;

import com.retailsoft.entity.Usuario;
import com.retailsoft.entity.Usuario.TipoUsuario;
import com.retailsoft.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminUsername = "79915961";

        if (usuarioRepository.findByUsername(adminUsername).isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode("farooshk")); // contraseña segura
            admin.setNombre("Administrador del Sistema");
            admin.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
            admin.setActivo(true);

            usuarioRepository.save(admin);
            System.out.println("🛡️ Usuario administrador creado con éxito: " + adminUsername);
        } else {
            System.out.println("✅ Usuario administrador ya existe: " + adminUsername);
        }
    }
}
