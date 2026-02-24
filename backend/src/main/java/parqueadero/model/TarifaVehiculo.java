package parqueadero.model;

import jakarta.persistence.*;

@Entity // <--- ESTO LE DICE A SPRING QUE ES UNA TABLA
@Table(name = "tarifasvehiculos") // Nombre exacto de tu tabla en MySQL
public class TarifaVehiculo {

    @Id // <--- LLAVE PRIMARIA
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private int id;

    @ManyToOne // Relación con la tabla tiposvehiculos
    @JoinColumn(name = "id_tipo") // El nombre de la columna FK en tu DB
    private TipoVehiculo tipoVehiculo;

    private float tarifa;
    private float tax;

    // Constructor vacío obligatorio para JPA
    public TarifaVehiculo() {
    }

    // GETTERS Y SETTERS (Igual que los tenías)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public float getTarifa() {
        return tarifa;
    }

    public void setTarifa(float tarifa) {
        this.tarifa = tarifa;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }
}
