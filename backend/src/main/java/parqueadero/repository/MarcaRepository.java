package parqueadero.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import parqueadero.model.Marca;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Integer> {
    // Ejemplo para MarcaRepository (haz lo mismo en los otros dos con sus nombres)
    Optional <Marca> findByNombre(String nombre);
}
