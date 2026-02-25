package parqueadero.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import parqueadero.model.TipoVehiculo;

@Repository
public interface TipoVehiculoRepository extends JpaRepository<TipoVehiculo, Integer> {

    // Ejemplo para MarcaRepository (haz lo mismo en los otros dos con sus nombres)
    Optional<TipoVehiculo> findByNombre(String nombre);
}
