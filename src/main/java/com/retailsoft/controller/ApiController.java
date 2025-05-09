package com.retailsoft.controller;

import com.retailsoft.dto.IngredienteDTO;
import com.retailsoft.service.IngredienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/ingredientes")
public class ApiController {

    @Autowired
    IngredienteService ingredienteService;

    @GetMapping(value = "/adicionales")
    public ResponseEntity<List<IngredienteDTO>> obtenerIngredientesAdicionales(){
        List<IngredienteDTO> adicionales = ingredienteService.listarAdicionales();
        return ResponseEntity.ok(adicionales);
    }
}
