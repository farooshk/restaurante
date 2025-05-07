package com.retailsoft.controller;

import com.retailsoft.dto.ComandaDTO;
import com.retailsoft.dto.PedidoDTO;
import com.retailsoft.dto.ProductoDTO;
import com.retailsoft.entity.Pedido;
import com.retailsoft.service.CategoriaService;
import com.retailsoft.service.PedidoService;
import com.retailsoft.service.ProductoService;
import com.retailsoft.service.UsuarioService;
import com.retailsoft.utils.PrinterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    private PrinterUtil printerUtil;

    @GetMapping
    public String listarPedidosDelDia(Model model) {
        List<PedidoDTO> pedidos = pedidoService.listarPedidosDelDia();
        model.addAttribute("pedidos", pedidos);
        return "pedidos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoPedido(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "pedidos/nuevo";
    }

    @GetMapping("/productos/{categoriaId}")
    @ResponseBody
    public List<ProductoDTO> obtenerProductosPorCategoria(@PathVariable Long categoriaId) {
        return productoService.listarPorCategoria(categoriaId);
    }

    @GetMapping("/{id}")
    public String verPedido(@PathVariable Long id, Model model) {
        pedidoService.buscarPorId(id).ifPresent(pedido -> model.addAttribute("pedido", pedido));
        return "pedidos/detalle";
    }

    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<PedidoDTO> guardarPedido(@RequestBody PedidoDTO pedido, Authentication authentication) {
        pedido.setUsuarioNombre(authentication.getName());
        PedidoDTO nuevoPedido = pedidoService.crearPedido(pedido);
        return ResponseEntity.ok(nuevoPedido);
    }

    @PutMapping("/{id}/actualizar")
    @ResponseBody
    public ResponseEntity<PedidoDTO> actualizarPedido(@PathVariable Long id, @RequestBody PedidoDTO pedido) {
        pedido.setId(id);
        PedidoDTO pedidoActualizado = pedidoService.actualizarPedido(pedido);
        return ResponseEntity.ok(pedidoActualizado);
    }

    @PutMapping("/{id}/cambiarEstado")
    @ResponseBody
    public ResponseEntity<Void> cambiarEstadoPedido(@PathVariable Long id, @RequestParam Pedido.EstadoPedido estado) {
        pedidoService.buscarPorId(id).ifPresent(pedido -> {
            pedido.setEstado(estado);
            pedidoService.actualizarPedido(pedido);
        });
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/anular")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @ResponseBody
    public ResponseEntity<Void> anularPedido(@PathVariable Long id, @RequestParam String motivo) {
        pedidoService.anularPedido(id, motivo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/comanda-html")
    public String verComanda(@PathVariable Long id, Model model) {
        ComandaDTO comanda = pedidoService.generarComanda(id);
        if (comanda == null) {
            return "error/404";
        }

        model.addAttribute("pedido", comanda);
        model.addAttribute("fechaHora", comanda.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        return "comanda"; // plantilla comanda.html en /templates
    }

    @GetMapping("/{id}/comanda-texto")
    @ResponseBody
    public ResponseEntity<String> imprimirTexto(@PathVariable Long id) {
        ComandaDTO comanda = pedidoService.generarComanda(id);
        if (comanda == null) {
            return ResponseEntity.notFound().build();
        }

        String textoComanda = printerUtil.generarTextoComanda(comanda);
        return ResponseEntity.ok(textoComanda);
    }
}
