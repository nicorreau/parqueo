package parqueadero.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import parqueadero.model.Vehiculo;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {
    // Definirlo como Optional permite usar .orElseThrow() y también .orElse(null)
    Optional<Vehiculo> findByPlaca(String placa);
}