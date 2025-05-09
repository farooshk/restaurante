document.addEventListener('DOMContentLoaded', function() {
    // Modal de anular
    const anularPedidoModal = new bootstrap.Modal(document.getElementById('anularPedidoModal'));

    // Evento para abrir modal de anulación
    document.querySelectorAll('.btn-anular-pedido').forEach(btn => {
        btn.addEventListener('click', function() {
            const pedidoId = this.getAttribute('data-pedido-id');
            document.getElementById('pedidoIdAnular').value = pedidoId;
            anularPedidoModal.show();
        });
    });

    // Evento para confirmar anulación
    document.getElementById('btnConfirmarAnular').addEventListener('click', function() {
        const pedidoId = document.getElementById('pedidoIdAnular').value;
        const motivo = document.getElementById('motivoAnulacion').value.trim();

        if (!motivo) {
            alert('Debe ingresar el motivo de anulación');
            return;
        }

        // Enviar petición de anulación
        fetch(`/pedidos/${pedidoId}/anular`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `motivo=${encodeURIComponent(motivo)}`
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al anular el pedido');
            }
            // Recargar página
            location.reload();
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error al anular el pedido. Intente nuevamente.');
        });
    });

    // Evento para imprimir comanda
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

                    alert('✅ Comanda descargada. Ábrela con RawBT para imprimir.');
                })
                .catch(error => {
                    console.error('❌ Error:', error);
                    alert('Error al generar la comanda. Intenta nuevamente.');
                });
        });
    });
});
