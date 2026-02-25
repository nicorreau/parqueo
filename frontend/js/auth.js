// FUNCION PARA INICIAR SESIÓN

async function login() {
    // 1. Capturar los valores usando TUS IDs actuales
    const userVal = document.getElementById('usuario').value;
    const passVal = document.getElementById('password').value; // Usando tu ID 'password'

    if (!userVal || !passVal) {
        alert("⚠️ Por favor, complete todos los campos.");
        return;
    }

    // 2. Preparar el objeto para tu UsuarioController
    const loginData = {
        usuario: userVal,
        clave: passVal
    };

    try {
        // 3. Petición al endpoint que me mostraste
        const respuesta = await fetch('http://localhost:8080/api/usuarios/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(loginData)
        });

        if (respuesta.ok) {
            const userResponse = await respuesta.json();
            
            // Guardamos la sesión localmente
            localStorage.setItem('usuarioSesion', JSON.stringify(userResponse));
            
            // Redirección a la página principal
            window.location.href = 'main.html';
        } else {
            alert("❌ Usuario o contraseña incorrectos.");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("🚨 No se pudo conectar con el servidor (Spring Boot).");
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