package com.retailsoft.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FileStorageService {

    // Cambia la ubicación del directorio de subida
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

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
        String nombreArchivo = String.format("img_%s" + extension, fechaYHoraActual());

        // Guardar archivo
        Path rutaCompleta = directorioPath.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

        // Devolver la ruta relativa con el nuevo prefijo
        return "/uploads/" + subdirectorio + "/" + nombreArchivo;
    }

    private String fechaYHoraActual(){
        LocalDateTime myDate = LocalDateTime.now();
        DateTimeFormatter myFormatDate = DateTimeFormatter.ofPattern("ddMMyyyy_HH_mm_ss");
        return myDate.format(myFormatDate);
    }

    public void eliminarArchivo(String rutaRelativa) {
        if (rutaRelativa == null || rutaRelativa.isEmpty()) {
            return;
        }

        try {
            // Convertir la ruta relativa a absoluta
            String rutaAbsoluta = "/uploads/" + rutaRelativa;
            Path archivo = Paths.get(rutaAbsoluta);
            Files.deleteIfExists(archivo);
        } catch (IOException e) {
            // Loguear el error pero no lanzar excepción
            e.printStackTrace();
        }
    }
}
