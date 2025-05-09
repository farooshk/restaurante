package com.retailsoft.config;

import com.retailsoft.entity.Usuario;
import com.retailsoft.entity.Usuario.TipoUsuario;
import com.retailsoft.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.superAdmin.name}")
    private String name;

    @Value("${app.superAdmin.surname}")
    private String surname;

    @Value("${app.superAdmin.email}")
    private String email;

    @Value("${app.superAdmin.usr}")
    private String usr;

    @Value("${app.superAdmin.pass}")
    private String pass;

    @Override
    public void run(String... args) throws Exception {

        String superAdmin = usr;

        if (usuarioRepository.findByUsername(superAdmin).isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername(superAdmin);
            admin.setPassword(passwordEncoder.encode(pass)); // contraseña segura
            admin.setNombre(name);
            admin.setApellido(surname);
            admin.setEmail(email);
            admin.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
            admin.setActivo(true);

            usuarioRepository.save(admin);
            System.out.println("🛡️ Usuario super administrador '" + superAdmin + "' creado con éxito: ");
        } else {
            System.out.println("✅ Usuario super administrador '" + superAdmin + "' ya existe: ");
        }
    }
}
