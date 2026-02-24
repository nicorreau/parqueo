package parqueadero.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Relación con el movimiento que originó la factura
    @OneToOne
    @JoinColumn(name = "id_mov_vehiculo")
    private MovimientoVehiculo movimiento;

    private float tarifa;
    private float tax;
    private int horas;

    @Column(name = "creada")
    private LocalDateTime creada;

    public Factura() {
    }

    // Getters y Setters (Estilo NetBeans)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MovimientoVehiculo getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoVehiculo movimiento) {
        this.movimiento = movimiento;
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

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public LocalDateTime getCreada() {
        return creada;
    }

    public void setCreada(LocalDateTime creada) {
        this.creada = creada;
    }
}