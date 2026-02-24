package parqueadero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import parqueadero.model.TarifaVehiculo;

@Repository
public interface TarifaVehiculoRepository extends JpaRepository<TarifaVehiculo, Integer> {
}