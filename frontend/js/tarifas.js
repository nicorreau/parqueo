// 1. Cargar tipos de vehículos al abrir la página
async function cargarTiposVehiculo() {
    const select = document.getElementById('id_tipo');
    try {
        const response = await fetch('http://localhost:8080/api/tipos');
        const tipos = await response.json();

        select.innerHTML = '<option value="" disabled selected>Seleccione un tipo...</option>';

        tipos.forEach(tipo => {
            const option = document.createElement('option');
            option.value = tipo.id;
            option.textContent = tipo.nombre;
            select.appendChild(option);
        });
    } catch (error) {
        console.error("Error cargando tipos:", error);
        select.innerHTML = '<option>Error al cargar tipos</option>';
    }
}

// 2. Función para guardar la tarifa con taxes decimales
async function guardarTarifa(event) {
    event.preventDefault();

    const idTipo = document.getElementById('id_tipo').value;
    const precio = document.getElementById('precio').value;
    const ivaInput = document.getElementById('iva').value; // Ejemplo: 16

    // Preparamos el JSON convirtiendo el IVA a decimal (0.16) para tu BD
    const nuevaTarifa = {
        tipoVehiculo: { id: parseInt(idTipo) },
        tarifa: parseFloat(precio),
        tax: parseFloat(ivaInput) / 100 // Esto guarda 0.16 en la columna 'tax'
    };

    try {
        const response = await fetch('http://localhost:8080/api/tarifas', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(nuevaTarifa)
        });

        if (response.ok) {
            alert("¡Tarifa guardada con éxito!");
            document.getElementById('formTarifas').reset(); 
            // Opcional: recargar una tabla de visualización si la tuvieras
        } else {
            alert("Error al guardar la tarifa. Verifique los datos.");
        }
    } catch (error) {
        console.error("Error en la petición:", error);
        alert("No se pudo conectar con el servidor.");
    }
}

// 3. Inicialización de eventos
document.addEventListener('DOMContentLoaded', () => {
    // Carga el selector apenas abre la página
    cargarTiposVehiculo();

    // Escucha el envío del formulario
    const form = document.getElementById('formTarifas');
    if (form) {
        form.addEventListener('submit', guardarTarifa);
    }
});