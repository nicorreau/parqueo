package parqueadero.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import parqueadero.model.Usuario;
import parqueadero.repository.UsuarioRepository;
import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repo;

    @GetMapping
    public List<Usuario> listar() {
        return repo.findAll();
    }

    @PostMapping
    public Usuario guardar(@RequestBody Usuario usuario) {
        return repo.save(usuario);
    }

    @PostMapping("/login")
    public Usuario login(@RequestBody Usuario loginData) {
        // 1. Buscamos el usuario por el nombre de cuenta (según tu esquema)
        Usuario user = repo.findByUsuario(loginData.getUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Comparamos la clave (en texto plano por ahora, como en tu esquema
        // original)
        if (user.getClave().equals(loginData.getClave())) {
            return user; // Si coincide, devolvemos el objeto usuario completo
        } else {
            throw new RuntimeException("Credenciales incorrectas");
        }
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        repo.deleteById(id);
    }
}