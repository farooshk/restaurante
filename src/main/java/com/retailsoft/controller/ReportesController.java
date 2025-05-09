package com.retailsoft.controller;

import com.retailsoft.service.PedidoService;
import com.retailsoft.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/reportes")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ReportesController {

    @Autowired
    PedidoService pedidoService;

    @Autowired
    UsuarioService usuarioService;

    @GetMapping
    public String mostrarReportes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long meseroId,
            Model model) {

        if (fechaInicio == null) fechaInicio = LocalDate.now();
        if (fechaFin == null) fechaFin = LocalDate.now();

        // Obtener datos desde el servicio
        var resumen = pedidoService.obtenerResumenVentasEntreFechas(fechaInicio, fechaFin, meseroId);
        var categorias = pedidoService.obtenerVentasPorCategoria(fechaInicio, fechaFin, meseroId);
        var dias = pedidoService.obtenerVentasPorDia(fechaInicio, fechaFin, meseroId);
        var meseros = usuarioService.listarUsuariosQueTomanPedidos();

        // Agregar al modelo
        model.addAttribute("totalVentas", resumen.getVentasTotal());
        model.addAttribute("cantidadPedidos", resumen.getTotalPedidos());
        model.addAttribute("pedidos", resumen.getPedidos());

        model.addAttribute("ventasPorCategoria", categorias);
        model.addAttribute("ventasPorDia", dias);

        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("meseros", meseros);
        model.addAttribute("meseroId", meseroId);

        return "admin/reportes";
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarReporteExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long meseroId) {

        byte[] archivo = pedidoService.exportarReporte(fechaInicio, fechaFin, meseroId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_de_ventas.xlsx");

        return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
    }
}
