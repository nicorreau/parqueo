package parqueadero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import parqueadero.model.Usuario;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Buscamos por el campo "usuario" del diagrama
    Optional<Usuario> findByUsuario(String usuario);
}