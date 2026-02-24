package parqueadero.model;

import jakarta.persistence.*; // Asegúrate de importar esto

@Entity // <--- ESTA ES LA QUE FALTA
@Table(name = "marcas") // Nombre exacto de tu tabla en MySQL
public class Marca {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String nombre;

    // IMPORTANTE: Constructor vacío
    public Marca() {}

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
