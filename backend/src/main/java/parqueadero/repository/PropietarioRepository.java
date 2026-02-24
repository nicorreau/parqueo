package parqueadero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import parqueadero.model.Propietario;

@Repository
public interface PropietarioRepository extends JpaRepository<Propietario, Integer> {

    Propietario findByDni(String dni);

 }