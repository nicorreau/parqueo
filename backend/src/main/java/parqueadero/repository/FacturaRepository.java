package parqueadero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import parqueadero.model.Factura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {

    // Suma el valor de todas las tarifas + todos los impuestos
    @Query("SELECT SUM(f.tarifa * f.horas + f.tax) FROM Factura f")
    Double obtenerRecaudoTotal();
}