package com.retailsoft.service.impl;

import com.retailsoft.dto.*;
import com.retailsoft.entity.*;
import com.retailsoft.repository.*;
import com.retailsoft.service.PedidoService;
import com.retailsoft.utils.PrinterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final IngredienteRepository ingredienteRepository;
    private final PrinterUtil printerUtil;

    @Autowired
    public PedidoServiceImpl(
            PedidoRepository pedidoRepository,
            ItemPedidoRepository itemPedidoRepository,
            ProductoRepository productoRepository,
            UsuarioRepository usuarioRepository,
            IngredienteRepository ingredienteRepository,
            PrinterUtil printerUtil) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.printerUtil = printerUtil;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDTO> listarPedidosDelDia() {
        LocalDateTime inicio = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime fin = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        return pedidoRepository.findPedidosDelDia(inicio, fin).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PedidoDTO> buscarPorId(Long id) {
        return pedidoRepository.findById(id).map(this::convertirADTO);
    }

    @Override
    @Transactional
    public PedidoDTO crearPedido(PedidoDTO pedidoDTO) {
        Pedido pedido = new Pedido();
        pedido.setFechaHora(LocalDateTime.now());
        pedido.setMesa(pedidoDTO.getMesa());
        pedido.setTotal(0);
        pedido.setEstado(Pedido.EstadoPedido.CREADO);
        pedido.setComandaImpresa(false);
        pedido.setAnulado(false);

        // Buscar el usuario por su nombre
        usuarioRepository.findByUsername(pedidoDTO.getUsuarioNombre())
                .ifPresent(pedido::setUsuario);

        pedido = pedidoRepository.save(pedido);

        // Agregar items al pedido
        for (ItemPedidoDTO itemDTO : pedidoDTO.getItems()) {
            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setCantidad(itemDTO.getCantidad());
            item.setPrecioUnitario(itemDTO.getPrecioUnitario());
            item.setObservaciones(itemDTO.getObservaciones());

            // Buscar el producto
            productoRepository.findById(itemDTO.getProductoId())
                    .ifPresent(item::setProducto);

            // Agregar ingredientes adicionales
            Set<Ingrediente> adicionales = new HashSet<>();
            for (IngredienteDTO ingredienteDTO : itemDTO.getIngredientesAdicionales()) {
                ingredienteRepository.findById(ingredienteDTO.getId())
                        .ifPresent(adicionales::add);
            }
            item.setIngredientesAdicionales(adicionales);

            // Agregar ingredientes eliminados
            Set<Ingrediente> eliminados = new HashSet<>();
            for (IngredienteDTO ingredienteDTO : itemDTO.getIngredientesEliminados()) {
                ingredienteRepository.findById(ingredienteDTO.getId())
                        .ifPresent(eliminados::add);
            }
            item.setIngredientesEliminados(eliminados);

            itemPedidoRepository.save(item);
        }

        // Calcular total del pedido
        pedido.setTotal(pedido.calcularTotal());
        pedido = pedidoRepository.save(pedido);

        return convertirADTO(pedido);
    }

    @Override
    @Transactional
    public PedidoDTO actualizarPedido(PedidoDTO pedidoDTO) {
        return pedidoRepository.findById(pedidoDTO.getId())
                .map(pedido -> {
                    pedido.setEstado(pedidoDTO.getEstado());

                    // Eliminar items actuales
                    List<ItemPedido> itemsActuales = itemPedidoRepository.findByPedido(pedido);
                    itemPedidoRepository.deleteAll(itemsActuales);

                    // Agregar nuevos items
                    for (ItemPedidoDTO itemDTO : pedidoDTO.getItems()) {
                        ItemPedido item = new ItemPedido();
                        item.setPedido(pedido);
                        item.setCantidad(itemDTO.getCantidad());
                        item.setPrecioUnitario(itemDTO.getPrecioUnitario());
                        item.setObservaciones(itemDTO.getObservaciones());

                        // Buscar el producto
                        productoRepository.findById(itemDTO.getProductoId())
                                .ifPresent(item::setProducto);

                        // Agregar ingredientes adicionales
                        Set<Ingrediente> adicionales = new HashSet<>();
                        for (IngredienteDTO ingredienteDTO : itemDTO.getIngredientesAdicionales()) {
                            ingredienteRepository.findById(ingredienteDTO.getId())
                                    .ifPresent(adicionales::add);
                        }
                        item.setIngredientesAdicionales(adicionales);

                        // Agregar ingredientes eliminados
                        Set<Ingrediente> eliminados = new HashSet<>();
                        for (IngredienteDTO ingredienteDTO : itemDTO.getIngredientesEliminados()) {
                            ingredienteRepository.findById(ingredienteDTO.getId())
                                    .ifPresent(eliminados::add);
                        }
                        item.setIngredientesEliminados(eliminados);

                        itemPedidoRepository.save(item);
                    }

                    // Calcular total del pedido
                    pedido.setTotal(pedido.calcularTotal());
                    pedido = pedidoRepository.save(pedido);

                    return convertirADTO(pedido);
                })
                .orElse(null);
    }

    @Override
    @Transactional
    public void anularPedido(Long id, String motivo) {
        pedidoRepository.findById(id).ifPresent(pedido -> {
            pedido.setAnulado(true);
            pedido.setMotivoAnulacion(motivo);
            pedido.setEstado(Pedido.EstadoPedido.ANULADO);
            pedidoRepository.save(pedido);
        });
    }

    @Override
    @Transactional
    public ComandaDTO generarComanda(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .map(pedido -> {
                    ComandaDTO comanda = new ComandaDTO();
                    comanda.setPedidoId(pedido.getId());
                    comanda.setFechaHora(pedido.getFechaHora());
                    comanda.setMesa(pedido.getMesa());
                    comanda.setMesero(pedido.getUsuario().getNombre());

                    List<ItemPedido> items = itemPedidoRepository.findByPedido(pedido);
                    for (ItemPedido item : items) {
                        ComandaDTO.ItemComandaDTO itemComanda = new ComandaDTO.ItemComandaDTO();
                        itemComanda.setCategoria(item.getProducto().getCategoria().getNombre());
                        itemComanda.setProductoNombre(item.getProducto().getNombre());
                        itemComanda.setCantidad(item.getCantidad());
                        itemComanda.setObservaciones(item.getObservaciones());

                        List<String> adicionales = item.getIngredientesAdicionales().stream()
                                .map(Ingrediente::getNombre)
                                .collect(Collectors.toList());
                        itemComanda.setIngredientesAdicionales(adicionales);

                        List<String> eliminados = item.getIngredientesEliminados().stream()
                                .map(Ingrediente::getNombre)
                                .collect(Collectors.toList());
                        itemComanda.setIngredientesEliminados(eliminados);

                        comanda.getItems().add(itemComanda);
                    }

                    return comanda;
                })
                .orElse(null);
    }

    @Override
    @Transactional
    public boolean marcarComandaImpresa(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .map(pedido -> {
                    pedido.setComandaImpresa(true);
                    pedido.setEstado(Pedido.EstadoPedido.EN_PREPARACION);
                    pedidoRepository.save(pedido);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenVentasDTO obtenerResumenVentas(LocalDate fecha) {
        LocalDateTime inicio = LocalDateTime.of(fecha, LocalTime.MIN);
        LocalDateTime fin = LocalDateTime.of(fecha, LocalTime.MAX);

        List<Pedido> pedidos = pedidoRepository.findPedidosDelDia(inicio, fin);
        Double totalVentas = pedidoRepository.obtenerTotalVentasDiarias(inicio, fin);

        ResumenVentasDTO resumen = new ResumenVentasDTO();
        resumen.setFecha(fecha);
        resumen.setTotalPedidos(pedidos.size());
        resumen.setVentasTotal(totalVentas != null ? totalVentas.intValue() : 0);

        // Convertir pedidos a DTO
        resumen.setPedidos(pedidos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList()));

        return resumen;
    }

    private PedidoDTO convertirADTO(Pedido pedido) {
        PedidoDTO dto = PedidoDTO.builder()
                .id(pedido.getId())
                .fechaHora(pedido.getFechaHora())
                .mesa(pedido.getMesa())
                .usuarioNombre(pedido.getUsuario().getNombre())
                .total(pedido.getTotal())
                .estado(pedido.getEstado())
                .comandaImpresa(pedido.isComandaImpresa())
                .anulado(pedido.isAnulado())
                .motivoAnulacion(pedido.getMotivoAnulacion())
                .build();

        // Convertir items
        List<ItemPedido> items = itemPedidoRepository.findByPedido(pedido);
        dto.setItems(items.stream()
                .map(this::convertirAItemDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private ItemPedidoDTO convertirAItemDTO(ItemPedido item) {
        ItemPedidoDTO dto = ItemPedidoDTO.builder()
                .id(item.getId())
                .productoId(item.getProducto().getId())
                .productoNombre(item.getProducto().getNombre())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .subtotal(item.calcularSubtotal())
                .observaciones(item.getObservaciones())
                .build();

        // Convertir ingredientes adicionales
        dto.setIngredientesAdicionales(item.getIngredientesAdicionales().stream()
                .map(this::convertirAIngredienteDTO)
                .collect(Collectors.toList()));

        // Convertir ingredientes eliminados
        dto.setIngredientesEliminados(item.getIngredientesEliminados().stream()
                .map(this::convertirAIngredienteDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private IngredienteDTO convertirAIngredienteDTO(Ingrediente ingrediente) {
        return IngredienteDTO.builder()
                .id(ingrediente.getId())
                .nombre(ingrediente.getNombre())
                .precioPorcion(ingrediente.getPrecioPorcion())
                .esAdicional(ingrediente.isEsAdicional())
                .build();
    }
}
