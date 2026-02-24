package parqueadero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import parqueadero.model.Color;

@Repository
public interface ColorRepository extends JpaRepository<Color, Integer> {
    // Aquí ya tienes heredados métodos como:
    // .findAll() -> SELECT * FROM colores
    // .findById() -> SELECT * FROM colores WHERE id = ?
    // .save() -> INSERT o UPDATE
}