package parqueadero.model; // IMPORTANTE: Debe coincidir con tu carpeta

import jakarta.persistence.*;

@Entity
@Table(name = "colores") 
public class Color {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String nombre;

    // Constructor vacío obligatorio para Hibernate
    public Color() {}

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}