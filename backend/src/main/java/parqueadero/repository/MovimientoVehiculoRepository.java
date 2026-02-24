package parqueadero.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import parqueadero.model.MovimientoVehiculo;

@Repository
public interface MovimientoVehiculoRepository extends JpaRepository<MovimientoVehiculo, Integer> {

    // Este método busca el registro que NO tiene fecha de salida todavía
    @Query("SELECT m FROM MovimientoVehiculo m WHERE m.vehiculo.placa = :placa AND m.salida IS NULL")
    Optional<MovimientoVehiculo> findByVehiculoPlacaAndSalidaIsNull(String placa);

    // Busca todos los movimientos donde la fecha de salida sea nula (siguen en el
    // parqueadero)
    List<MovimientoVehiculo> findBySalidaIsNull();
}
