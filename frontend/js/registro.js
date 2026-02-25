
// Función para llenar cualquier selector con datos de la API
// 1. Función genérica para llenar cualquier select
async function cargarSelect(endpoint, elementId) {
    try {
        const response = await fetch(`http://localhost:8080/api/${endpoint}`);
        const datos = await response.json();
        const select = document.getElementById(elementId);

        // Limpiamos las opciones actuales (las de simulación)
        select.innerHTML = '<option value="">Seleccionar...</option>';

        // Llenamos con datos reales de la base de datos
        datos.forEach(item => {
            const option = document.createElement('option');
            option.value = item.id; // El ID numérico para el Backend
            option.textContent = item.nombre; // El texto para el Usuario
            select.appendChild(option);
        });
    } catch (error) {
        console.error(`Error al cargar ${endpoint}:`, error);
    }
}

// 2. Ejecutar la carga cuando el HTML esté listo
document.addEventListener('DOMContentLoaded', () => {
    // Verificamos si estamos en la página que tiene estos selects
    if (document.getElementById('reg-marca')) {
        cargarSelect('marcas', 'reg-marca');
        cargarSelect('colores', 'reg-color');
        cargarSelect('tipos', 'reg-tipo');
    }
});

// FUNCION PARA REGISTRAR UN VEHÍCULO
async function registrarVehiculo() {
    // 1. Capturamos los elementos para facilitar la lectura
    const inputMarca = document.getElementById('reg-marca');
    const inputColor = document.getElementById('reg-color');
    const inputTipo = document.getElementById('reg-tipo');
    const inputPlaca = document.getElementById('reg-placa');
    const inputDni = document.getElementById('reg-dni');
    const inputNombre = document.getElementById('reg-nombre');

    // 2. ESCUDO FRONTEND: Validación de campos de texto obligatorios
    // Verificamos que no envíen strings vacíos al Backend
    if (!inputMarca.value.trim() || !inputColor.value.trim() || !inputTipo.value.trim() || !inputPlaca.value.trim() || !inputDni.value.trim()) {
        alert("⚠️ Por favor, completa todos los campos obligatorios (Placa, Marca, Color, Tipo y DNI).");
        return;
    }

    // 3. Construcción del objeto EXACTO para el Map de Java
    // Usamos las llaves 'nombre_marca', 'nombre_color' y 'nombre_tipo' que definimos en el Controller
    const objetoParaEnviar = {
        placa: inputPlaca.value.toUpperCase().trim(),
        descripcion: document.getElementById('reg-descripcion').value.trim(),
        observaciones: document.getElementById('reg-observacion').value.trim(),
        
        // Enviamos el texto directamente para que el Backend decida si crearlo o no
        nombre_marca: inputMarca.value.trim(),
        nombre_color: inputColor.value.trim(),
        nombre_tipo: inputTipo.value.trim(),

        propietario: {
            dni: inputDni.value.trim(),
            nombre: inputNombre.value.trim()
        }
    };

    // DEBUG: Útil para verificar en la consola (F12) antes del envío
    console.log("Enviando estos datos al servidor:", objetoParaEnviar);

    try {
        const respuesta = await fetch('http://localhost:8080/api/vehiculos/registro-completo', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(objetoParaEnviar)
        });

        if (respuesta.ok) {
            alert("✅ ¡Vehículo e Ingreso registrados correctamente!");
            window.location.href = 'main.html';
        } else {
            // Capturamos el mensaje de error de la RuntimeException de Java
            const errorMsg = await respuesta.text();
            alert("❌ Error del Servidor: " + errorMsg);
        }
    } catch (err) {
        console.error("Error de red:", err);
        alert("🚨 Error de conexión: Asegúrate de que el servidor Spring Boot esté corriendo.");
    }
}



