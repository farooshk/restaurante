package com.retailsoft.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String uploadDir = "src/main/resources/static/uploads/";

    public String almacenarArchivo(MultipartFile archivo, String subdirectorio) throws IOException {
        // Crear directorio si no existe
        Path directorioPath = Paths.get(uploadDir + subdirectorio);
        Files.createDirectories(directorioPath);

        // Generar nombre único para el archivo
        String nombreOriginal = StringUtils.cleanPath(archivo.getOriginalFilename());
        String extension = "";
        if (nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        }
        String nombreArchivo = UUID.randomUUID().toString() + extension;

        // Guardar archivo
        Path rutaCompleta = directorioPath.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

        // Devolver la ruta relativa para guardar en la base de datos
        return "/uploads/" + subdirectorio + "/" + nombreArchivo;
    }

    public void eliminarArchivo(String rutaRelativa) {
        if (rutaRelativa == null || rutaRelativa.isEmpty()) {
            return;
        }

        try {
            // Convertir la ruta relativa a absoluta
            String rutaAbsoluta = "src/main/resources/static" + rutaRelativa;
            Path archivo = Paths.get(rutaAbsoluta);
            Files.deleteIfExists(archivo);
        } catch (IOException e) {
            // Loguear el error pero no lanzar excepción
            e.printStackTrace();
        }
    }
}
