document.addEventListener('DOMContentLoaded', function() {
    // Variables globales
    let categoriaSeleccionada = null;
    let productosActuales = [];
    let itemsPedido = [];
    let ingredientesAdicionales = [];
    let ingredientesBase = [];
    let ultimaComandaId = null;

    // Elementos del DOM
    const productosContainer = document.getElementById('productosContainer');
    const productosGrid = document.getElementById('productosGrid');
    const itemsPedidoContainer = document.getElementById('itemsPedido');
    const totalPedidoEl = document.getElementById('totalPedido');
    const emptyCartMsg = document.getElementById('emptyCartMsg');
    const btnGuardarPedido = document.getElementById('btnGuardarPedido');
    const btnCancelarPedido = document.getElementById('btnCancelarPedido');

    // Modal de producto
    const productoModal = new bootstrap.Modal(document.getElementById('productoModal'));
    const comandaModal = new bootstrap.Modal(document.getElementById('comandaModal'));

    // Manejadores de eventos
    setupEventListeners();

    // Verificar si hay un parámetro de categoría en la URL
    const urlParams = new URLSearchParams(window.location.search);
    const categoriaId = urlParams.get('categoria');
    if (categoriaId) {
        const categoriaCard = document.querySelector(`.categoria-card[data-categoria-id="${categoriaId}"]`);
        if (categoriaCard) {
            categoriaCard.click();
        }
    }

    function setupEventListeners() {
        // Evento para seleccionar categoría
        document.querySelectorAll('.categoria-card').forEach(card => {
            card.addEventListener('click', function() {
                const categoriaId = this.getAttribute('data-categoria-id');
                seleccionarCategoria(categoriaId);

                // Quitar selección anterior
                document.querySelectorAll('.categoria-card').forEach(c => {
                    c.classList.remove('border-primary', 'border-3');
                });

                // Marcar esta categoría como seleccionada
                this.classList.add('border-primary', 'border-3');
            });
        });

        // Evento para agregar item al pedido
        document.getElementById('btnAgregarItem').addEventListener('click', agregarItemAlPedido);

        // Evento para guardar pedido
        btnGuardarPedido.addEventListener('click', guardarPedido);

        // Evento para cancelar pedido
        btnCancelarPedido.addEventListener('click', function() {
            if (confirm('¿Está seguro que desea cancelar el pedido?')) {
                window.location.href = '/pedidos';
            }
        });

        // Evento para finalizar pedido y volver a la lista
        document.getElementById('btnFinalizarPedido').addEventListener('click', function() {
            window.location.href = '/pedidos';
        });

        // Comprobamos que los botones existan antes de asignar eventos
        const btnVerComanda = document.getElementById('btn-ver-comanda');
        if (btnVerComanda) {
            btnVerComanda.addEventListener('click', () => {
                if (ultimaComandaId) {
                    window.open(`/pedidos/${ultimaComandaId}/comanda-html`, '_blank');
                }
            });
        }

        document.querySelectorAll('.btn-imprimir-comanda').forEach(btn => {
            btn.addEventListener('click', function () {
                const pedidoId = this.getAttribute('data-pedido-id');

                fetch(`/pedidos/${pedidoId}/comanda-texto`)
                    .then(response => response.text())
                    .then(texto => {
                        const blob = new Blob([texto], { type: 'text/plain' });
                        const url = URL.createObjectURL(blob);

                        const a = document.createElement('a');
                        a.href = url;
                        a.download = `comanda-${pedidoId}.txt`;
                        document.body.appendChild(a);
                        a.click();
                        document.body.removeChild(a);

                        alert('✅ Comanda descargada. Ábrela con ESC POS Bluetooth Print Service para imprimir.');
                    })
                    .catch(error => {
                        console.error('❌ Error:', error);
                        alert('Error al generar la comanda. Intenta nuevamente.');
                    });
            });
        });
    }

    function seleccionarCategoria(categoriaId) {
        categoriaSeleccionada = categoriaId;

        // Mostrar container de productos
        productosContainer.classList.remove('d-none');

        // Cargar productos de la categoría
        fetch(`/pedidos/productos/${categoriaId}`)
            .then(response => response.json())
            .then(data => {
                productosActuales = data;
                mostrarProductos(data);
            })
            .catch(error => {
                console.error('Error al cargar productos:', error);
                alert('Error al cargar productos. Intente nuevamente.');
            });
    }

    function mostrarProductos(productos) {
        productosGrid.innerHTML = '';

        productos.forEach(producto => {
            const productoCard = document.createElement('div');
            productoCard.className = 'col-md-4 mb-3';
            productoCard.innerHTML = `
                <div class="card h-100 producto-card" data-producto-id="${producto.id}">
                    <div class="card-body">
                        <h6 class="card-title">${producto.nombre}</h6>
                        <p class="card-text">
                            <small class="text-muted">$${producto.precio}</small>
                        </p>
                    </div>
                </div>
            `;

            productoCard.querySelector('.producto-card').addEventListener('click', function() {
                const productoId = this.getAttribute('data-producto-id');
                abrirModalProducto(productoId);
            });

            productosGrid.appendChild(productoCard);
        });
    }

    function abrirModalProducto(productoId) {
        // Buscar producto seleccionado
        const producto = productosActuales.find(p => p.id == productoId);
        if (!producto) return;

        // Llenar datos del modal
        document.getElementById('productoId').value = producto.id;
        document.getElementById('productoNombre').value = producto.nombre;
        document.getElementById('productoPrecio').value = `$${producto.precio}`;
        document.getElementById('cantidad').value = 1;
        document.getElementById('observaciones').value = '';

        // Mostrar ingredientes base
        const ingredientesBaseList = document.getElementById('ingredientesBaseList');
        ingredientesBaseList.innerHTML = '';

        // Guardar ingredientes base
        ingredientesBase = producto.ingredientesBase || [];

        if (ingredientesBase.length > 0) {
            ingredientesBase.forEach(ingrediente => {
                const ingredienteItem = document.createElement('div');
                ingredienteItem.className = 'form-check';
                ingredienteItem.innerHTML = `
                    <input class="form-check-input ingrediente-base-check" type="checkbox"
                           id="ingrediente_${ingrediente.id}"
                           data-ingrediente-id="${ingrediente.id}"
                           data-ingrediente-nombre="${ingrediente.nombre}" checked>
                    <label class="form-check-label" for="ingrediente_${ingrediente.id}">
                        ${ingrediente.nombre}
                    </label>
                `;
                ingredientesBaseList.appendChild(ingredienteItem);
            });
        } else {
            ingredientesBaseList.innerHTML = '<p class="text-muted">No hay ingredientes base registrados.</p>';
        }

        // Cargar ingredientes adicionales
        cargarIngredientesAdicionales();

        // Mostrar modal
        productoModal.show();
    }

    function cargarIngredientesAdicionales() {
        // Obtener ingredientes adicionales
        fetch('/api/ingredientes/adicionales')
            .then(response => response.json())
            .then(data => {
                ingredientesAdicionales = data;

                // Mostrar adicionales
                const adicionalesList = document.getElementById('adicionalesList');
                adicionalesList.innerHTML = '';

                if (data.length > 0) {
                    data.forEach(ingrediente => {
                        const adicionalItem = document.createElement('div');
                        adicionalItem.className = 'form-check';
                        adicionalItem.innerHTML = `
                            <input class="form-check-input adicional-check" type="checkbox"
                                   id="adicional_${ingrediente.id}"
                                   data-ingrediente-id="${ingrediente.id}"
                                   data-ingrediente-nombre="${ingrediente.nombre}"
                                   data-ingrediente-precio="${ingrediente.precioPorcion}">
                            <label class="form-check-label" for="adicional_${ingrediente.id}">
                                ${ingrediente.nombre} (+$${ingrediente.precioPorcion})
                            </label>
                        `;
                        adicionalesList.appendChild(adicionalItem);
                    });
                } else {
                    adicionalesList.innerHTML = '<p class="text-muted">No hay ingredientes adicionales disponibles.</p>';
                }
            })
            .catch(error => {
                console.error('Error al cargar ingredientes adicionales:', error);
                document.getElementById('adicionalesList').innerHTML = '<p class="text-danger">Error al cargar adicionales.</p>';
            });
    }

    function agregarItemAlPedido() {
        const productoId = document.getElementById('productoId').value;
        const productoNombre = document.getElementById('productoNombre').value;
        const cantidad = parseInt(document.getElementById('cantidad').value);
        const observaciones = document.getElementById('observaciones').value;

        // Obtener producto
        const producto = productosActuales.find(p => p.id == productoId);
        if (!producto) return;

        // Obtener ingredientes eliminados (los que están en base pero desmarcados)
        const ingredientesEliminados = [];
        document.querySelectorAll('.ingrediente-base-check').forEach(checkbox => {
            if (!checkbox.checked) {
                ingredientesEliminados.push({
                    id: checkbox.getAttribute('data-ingrediente-id'),
                    nombre: checkbox.getAttribute('data-ingrediente-nombre')
                });
            }
        });

        // Obtener ingredientes adicionales (los marcados)
        const adicionales = [];
        let precioAdicionales = 0;

        document.querySelectorAll('.adicional-check').forEach(checkbox => {
            if (checkbox.checked) {
                const id = checkbox.getAttribute('data-ingrediente-id');
                const nombre = checkbox.getAttribute('data-ingrediente-nombre');
                const precio = parseFloat(checkbox.getAttribute('data-ingrediente-precio'));

                adicionales.push({
                    id: id,
                    nombre: nombre,
                    precioPorcion: precio
                });

                precioAdicionales += precio;
            }
        });

        // Crear item
        const item = {
            productoId: productoId,
            productoNombre: productoNombre,
            cantidad: cantidad,
            precioUnitario: parseFloat(producto.precio),
            subtotal: (parseFloat(producto.precio) + precioAdicionales) * cantidad,
            observaciones: observaciones,
            ingredientesEliminados: ingredientesEliminados,
            ingredientesAdicionales: adicionales
        };

        // Agregar al array
        itemsPedido.push(item);

        // Actualizar UI
        actualizarPedidoUI();

        // Cerrar modal
        productoModal.hide();
    }

    function actualizarPedidoUI() {
        // Mostrar items
        itemsPedidoContainer.innerHTML = '';

        if (itemsPedido.length > 0) {
            emptyCartMsg.classList.add('d-none');
            btnGuardarPedido.removeAttribute('disabled');

            itemsPedido.forEach((item, index) => {
                const itemElement = document.createElement('div');
                itemElement.className = 'card mb-2';

                let adicionalesText = '';
                if (item.ingredientesAdicionales.length > 0) {
                    adicionalesText = '+ ' + item.ingredientesAdicionales.map(i => i.nombre).join(', ');
                }

                let eliminadosText = '';
                if (item.ingredientesEliminados.length > 0) {
                    eliminadosText = '- ' + item.ingredientesEliminados.map(i => i.nombre).join(', ');
                }

                itemElement.innerHTML = `
                    <div class="card-body py-2">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <h6 class="mb-0">${item.cantidad} x ${item.productoNombre}</h6>
                                <small class="text-muted">$${item.precioUnitario} c/u</small>
                                ${adicionalesText ? `<div><small class="text-success">${adicionalesText}</small></div>` : ''}
                                ${eliminadosText ? `<div><small class="text-danger">${eliminadosText}</small></div>` : ''}
                                ${item.observaciones ? `<div><small>${item.observaciones}</small></div>` : ''}
                            </div>
                            <div class="text-end">
                                <div class="fw-bold">$${item.subtotal}</div>
                                <button class="btn btn-sm btn-outline-danger"
                                        onclick="eliminarItem(${index})">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                `;

                itemsPedidoContainer.appendChild(itemElement);
            });

            // Calcular total
            const total = itemsPedido.reduce((sum, item) => sum + item.subtotal, 0);
            totalPedidoEl.textContent = `$${total.toFixed(2)}`;
        } else {
            emptyCartMsg.classList.remove('d-none');
            btnGuardarPedido.setAttribute('disabled', 'disabled');
            totalPedidoEl.textContent = '$0';
        }
    }

    // Función para eliminar item (debe estar disponible globalmente)
    window.eliminarItem = function(index) {
        itemsPedido.splice(index, 1);
        actualizarPedidoUI();
    };

    function guardarPedido() {
        // Validar mesa
        const mesa = document.getElementById('mesa').value.trim();
        const usuario = document.getElementById('usuarioNombre')?.value || 'Mesero';

        if (!mesa) {
            alert('Debe ingresar el número de mesa.');
            return;
        }

        // Validar que haya items
        if (itemsPedido.length === 0) {
            alert('Debe agregar al menos un producto al pedido.');
            return;
        }

        // Crear objeto pedido
        const pedido = {
            mesa: mesa,
            items: itemsPedido
        };

        // Enviar petición
        fetch('/pedidos/guardar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(pedido)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al guardar el pedido');
            }
            return response.json();
        })
        .then(data => {
            ultimaComandaId = data.id;
            mostrarNotificacionExito();
            agregarAlLogCocina('ENVIADO', pedido.mesa, usuario);

            fetch(`/pedidos/${data.id}/comanda-texto`)
                .then(res => res.text())
                .then(texto => {
                    const ventana = window.open('', '_blank');
                    ventana.document.write(`
                        <pre style="font-family: monospace; font-size: 10px;">${texto}</pre>
                        <script>
                            location.href = 'intent:#Intent;scheme=rawbt;package=ru.a402d.rawbtprinter;end;';
                        <\/script>
                    `);
                    ventana.document.close();
                    ventana.focus();
                });

            comandaModal.show();
        })
        .catch(error => {
            console.error('❌ Error al guardar el pedido:', error);
            alert('Error al procesar el pedido. Intente nuevamente.');
        });
    }

    function mostrarNotificacionExito() {
        const noti = document.getElementById('notificacion-exito');
        const sonido = document.getElementById('sonido-confirmacion');
        const lottie = document.getElementById('lottie-exito');

        noti.style.display = 'block';
        lottie.style.display = 'block';
        animExito.goToAndPlay(0, true);

        setTimeout(() => {
            noti.style.opacity = 1;
            noti.style.transform = 'scale(1)';
        }, 10);

        if (sonido) sonido.play().catch(() => {});

        setTimeout(() => {
            noti.style.opacity = 0;
            noti.style.transform = 'scale(0.9)';
            setTimeout(() => {
                noti.style.display = 'none';
                lottie.style.display = 'none';
            }, 500);
        }, 3500);
    }

    function agregarAlLogCocina(tipo, mesa, mesero) {
        const log = document.getElementById('log-cocina');
        const fecha = new Date().toLocaleTimeString();
        const mensaje = tipo === 'ENVIADO'
            ? `Enviada [${fecha}] Comanda enviada | Mesa ${mesa} | ${mesero}`
            : `Anulada [${fecha}] Pedido anulado | Mesa ${mesa} | ${mesero}`;

        const entrada = document.createElement('div');
        entrada.textContent = mensaje;
        entrada.style.color = tipo === 'ENVIADO' ? 'green' : 'red';

        log.appendChild(entrada);
        log.scrollTop = log.scrollHeight;

        const historial = JSON.parse(localStorage.getItem('logCocina')) || [];
        historial.push(mensaje);
        localStorage.setItem('logCocina', JSON.stringify(historial));
    }

    function cargarHistorialLog() {
        const log = document.getElementById('log-cocina');
        const historial = JSON.parse(localStorage.getItem('logCocina')) || [];

        historial.forEach(mensaje => {
            const entrada = document.createElement('div');
            entrada.textContent = mensaje;
            entrada.style.color = mensaje.startsWith('Enviada') ? 'green' : 'red';
            log.appendChild(entrada);
        });

        log.scrollTop = log.scrollHeight;
    }

    // Ejecutar al cargar
    cargarHistorialLog();
});
