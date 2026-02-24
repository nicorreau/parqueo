package parqueadero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import parqueadero.model.Marca;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Integer> {
   
}
