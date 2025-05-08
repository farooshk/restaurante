$(document).ready(function() {
    // Manejar clic en botón eliminar
    $('.eliminar-categoria').on('click', function() {
        const id = $(this).data('id');
        const nombre = $(this).data('nombre');
        const cantidadProductos = $(this).data('productos');

        // Verificar si tiene productos asociados
        if (cantidadProductos > 0) {
            Swal.fire({
                title: 'No se puede eliminar',
                text: `La categoría "${nombre}" tiene ${cantidadProductos} producto(s) asociado(s). Debe reasignar o eliminar estos productos primero.`,
                icon: 'warning',
                confirmButtonText: 'Entendido'
            });
            return;
        }

        // Mostrar confirmación
        Swal.fire({
            title: '¿Estás seguro?',
            text: `¿Deseas eliminar la categoría "${nombre}"?`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                // Enviar solicitud DELETE
                $.ajax({
                    url: `/admin/categorias/${id}`,
                    type: 'DELETE',
                    success: function() {
                        Swal.fire({
                            title: 'Eliminado',
                            text: 'La categoría ha sido eliminada correctamente',
                            icon: 'success'
                        }).then(() => {
                            location.reload();
                        });
                    },
                    error: function(xhr) {
                        let mensaje = 'Ha ocurrido un error al eliminar la categoría';
                        if (xhr.responseJSON && xhr.responseJSON.error) {
                            mensaje = xhr.responseJSON.error;
                        }
                        Swal.fire({
                            title: 'Error',
                            text: mensaje,
                            icon: 'error'
                        });
                    }
                });
            }
        });
    });
});
