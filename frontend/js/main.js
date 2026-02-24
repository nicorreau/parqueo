
function login() {
  const user = document.getElementById('usuario').value;
  const pass = document.getElementById('password').value;

  if (user === 'demo' && pass === '1234') {
    window.location.href = 'main.html';
  } else {
    alert('Usuario o contraseña incorrectos');
  }
}

function logout() {
  if (confirm('¿Deseas cerrar sesión?')) {
    window.location.href = 'login.html';
  }
  
}


document.addEventListener("DOMContentLoaded", () => {
    // Recuperar valores guardados
    const usuarioGuardado = localStorage.getItem("usuario");
    const passwordGuardado = localStorage.getItem("password");

    if (usuarioGuardado) document.getElementById("usuario").value = usuarioGuardado;
    if (passwordGuardado) document.getElementById("password").value = passwordGuardado;

    // Guardar valores al hacer clic en el botón
    document.querySelector("button").addEventListener("click", () => {
      localStorage.setItem("usuario", document.getElementById("usuario").value);
      localStorage.setItem("password", document.getElementById("password").value);
      alert("Datos guardados localmente ✅");
    });
  });

/* ... ... */

/* ==========================================
   LÓGICA VISUAL PARA SALIDA (Simulación)
   ========================================== */

function buscarVehiculo() {
    const placa = document.getElementById("buscarPlaca").value;
    
    // Validación simple
    if(placa.length < 3) {
        alert("Por favor ingrese una placa válida");
        return;
    }

    // SIMULACIÓN: Mostrar la tarjeta oculta
    const tarjeta = document.getElementById("resultado-card");
    tarjeta.classList.remove("hidden");
    
    // Llenar con datos falsos para probar el diseño
    document.getElementById("res-placa").innerText = placa.toUpperCase();
    document.getElementById("res-desc").innerText = "Chevrolet Spark GT (Ejemplo)";
    document.getElementById("res-ingreso").innerText = "08:00 AM";
    document.getElementById("res-tiempo").innerText = "3 Horas 15 Min";
    document.getElementById("res-total").innerText = "$ 12.500";
}

function limpiarBusqueda() {
    // Ocultar tarjeta y limpiar input
    document.getElementById("resultado-card").classList.add("hidden");
    document.getElementById("buscarPlaca").value = "";
}

function procesarSalida() {
    alert("¡Pago simulado con éxito! \nEl vehículo ha salido.");
    limpiarBusqueda();
}

/* ... ... */

/* ==========================================
   LÓGICA VISUAL PARA REPORTES (Simulación)
   ========================================== */

function cargarReportesSimulados() {
    // 1. Simular Datos de los KPI
    document.getElementById("total-dinero").innerText = "$ 14,280.00";
    document.getElementById("total-vehiculos").innerText = "4";
    document.getElementById("ocupacion-actual").innerText = "25%";
    document.getElementById("bar-width").style.width = "25%";

    // 2. Simular Datos de la Tabla
    const body = document.getElementById("tabla-reporte-body");
    const datos = [
        {placa: "ABC123", entrada: "08:00 AM", salida: "09:00 AM", total: "$3,570", estado: "Pagado"},
        {placa: "XYZ789", entrada: "10:30 AM", salida: "11:30 AM", total: "$3,570", estado: "Pagado"},
        {placa: "EXITO123", entrada: "19:06 PM", salida: "19:08 PM", total: "$3,570", estado: "Pagado"}
    ];

    body.innerHTML = datos.map(fila => `
        <tr>
            <td><strong>${fila.placa}</strong></td>
            <td>${fila.entrada}</td>
            <td>${fila.salida}</td>
            <td>${fila.total}</td>
            <td><span class="status-badge status-paid">${fila.estado}</span></td>
        </tr>
    `).join('');
}