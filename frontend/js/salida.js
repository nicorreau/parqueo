// Variable global para guardar los datos encontrados temporalmente
let movimientoActual = null;

async function buscarVehiculo() {
    const placa = document.getElementById('buscarPlaca').value.trim().toUpperCase();
    
    if (!placa) {
        alert("⚠️ Por favor, ingresa una placa para buscar.");
        return;
    }

    try {
        // Usamos el endpoint que ya tienes para ver activos
        const respuesta = await fetch(`http://localhost:8080/api/movimientos/activos`);
        const activos = await respuesta.json();

        // Filtramos por la placa ingresada
        movimientoActual = activos.find(m => m.vehiculo.placa === placa);

        if (movimientoActual) {
            mostrarCard(movimientoActual);
        } else {
            alert("❌ No se encontró un vehículo con esa placa dentro del parqueadero.");
            limpiarBusqueda();
        }
    } catch (error) {
        console.error("Error al buscar:", error);
        alert("🚨 Error al conectar con el servidor.");
    }
}

function mostrarCard(mov) {
    const card = document.getElementById('resultado-card');
    card.classList.remove('hidden');

    // Llenar datos básicos del ticket
    document.getElementById('res-placa').innerText = mov.vehiculo.placa;
    document.getElementById('res-desc').innerText = `${mov.vehiculo.marca.nombre} - ${mov.vehiculo.color.nombre}`;
    
    // Formatear fecha de ingreso
    const fechaIngreso = new Date(mov.ingreso);
    document.getElementById('res-ingreso').innerText = fechaIngreso.toLocaleString();

    // Calcular tiempo transcurrido (estimado en frontend para visualización)
    const ahora = new Date();
    const diffMs = ahora - fechaIngreso;
    const diffHrs = Math.ceil(diffMs / (1000 * 60 * 60)); // Horas redondeadas arriba
    
    document.getElementById('res-tiempo').innerText = `${diffHrs} hora(s) (aprox.)`;
    
    // Nota: El total exacto lo dará el backend al procesar, aquí podemos poner un estimado si conoces la tarifa
    document.getElementById('res-total').innerText = "Pendiente de cobro...";
}

async function procesarSalida() {
    if (!movimientoActual) return;

    try {
        // Llamamos a tu método registrarSalidaPorPlaca del controlador
        const respuesta = await fetch(`http://localhost:8080/api/movimientos/salida-por-placa/${movimientoActual.vehiculo.placa}`, {
            method: 'POST'
        });

        if (respuesta.ok) {
            const factura = await respuesta.json();
            
            // Calculamos el total final (Subtotal + IVA) para el mensaje de éxito
            const subtotal = factura.tarifa * factura.horas;
            const totalFinal = subtotal + factura.tax;

            alert(`✅ Salida Procesada!\nTotal: $${totalFinal.toFixed(2)}\nHoras cobradas: ${factura.horas}`);
            window.location.href = 'main.html';
        } else {
            const errorMsg = await respuesta.text();
            alert("❌ Error: " + errorMsg);
        }
    } catch (error) {
        alert("🚨 Error de conexión al procesar el pago.");
    }
}

function limpiarBusqueda() {
    document.getElementById('buscarPlaca').value = "";
    document.getElementById('resultado-card').classList.add('hidden');
    movimientoActual = null;
}