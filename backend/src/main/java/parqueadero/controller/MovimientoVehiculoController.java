package parqueadero.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;
import parqueadero.model.Factura;
import parqueadero.model.MovimientoVehiculo;
import parqueadero.model.TarifaVehiculo;
import parqueadero.model.Vehiculo;
import parqueadero.repository.FacturaRepository;
import parqueadero.repository.MovimientoVehiculoRepository;
import parqueadero.repository.TarifaVehiculoRepository;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/movimientos")
public class MovimientoVehiculoController {

    @Autowired
    private MovimientoVehiculoRepository repo;

    @GetMapping
    public List<MovimientoVehiculo> listar() {
        return repo.findAll();
    }

    @Autowired
    private FacturaRepository facturaRepo;

    @Autowired
    private TarifaVehiculoRepository tarifaRepo;

    @PostMapping
    public MovimientoVehiculo registrarEntrada(@RequestBody MovimientoVehiculo nuevoMovimiento) {
        // Establecemos la hora de ingreso automáticamente
        nuevoMovimiento.setIngreso(LocalDateTime.now());
        // Nos aseguramos que la salida sea nula al entrar
        nuevoMovimiento.setSalida(null);
        return repo.save(nuevoMovimiento);
    }

    @GetMapping("/activos")
    public List<MovimientoVehiculo> listarVehiculosDentro() {
        // Esto devolverá solo los registros donde la columna 'salida' es null
        return repo.findBySalidaIsNull();
    }

    @PutMapping("/salida/{id}")
    public Factura registrarSalida(@PathVariable Integer id) {
        // 1. Buscar el movimiento activo
        MovimientoVehiculo mov = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        // 2. Registrar hora de salida actual
        mov.setSalida(LocalDateTime.now());
        repo.save(mov);

        // 3. Calcular tiempo (Diferencia en horas)
        long minutos = java.time.Duration.between(mov.getIngreso(), mov.getSalida()).toMinutes();
        int horasACobrar = (int) Math.ceil(minutos / 60.0); // Redondea hacia arriba (ej: 1.1 horas = 2 horas)
        if (horasACobrar == 0)
            horasACobrar = 1; // Mínimo cobrar una hora

        // 4. Buscar la tarifa para el tipo de vehículo
        // (Asumimos que tienes una tarifa configurada para el id_tipo del vehículo)
        TarifaVehiculo tarifaObj = tarifaRepo.findAll().stream()
                .filter(t -> t.getTipoVehiculo().getId().equals(mov.getVehiculo().getTipoVehiculo().getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No hay tarifa configurada para este tipo de vehículo"));

        // 5. Crear la Factura
        Factura factura = new Factura();
        factura.setMovimiento(mov);

        // Cambiamos getValor() por getTarifa() que es el nombre real en tu modelo
        factura.setTarifa(tarifaObj.getTarifa());
        factura.setTax(tarifaObj.getTarifa() * 0.19f); // Cálculo del 19% de IVA

        factura.setHoras(horasACobrar);
        factura.setCreada(LocalDateTime.now());

        return facturaRepo.save(factura);
    }

    @Transactional
    @PostMapping("/salida-por-placa/{placa}")
    public Factura registrarSalidaPorPlaca(@PathVariable String placa) {
        // 1. Buscar el movimiento activo usando el nuevo método del repo
        MovimientoVehiculo mov = repo.findByVehiculoPlacaAndSalidaIsNull(placa)
                .orElseThrow(() -> new RuntimeException("No se encontró un ingreso activo para la placa: " + placa));

        // 2. Registrar hora de salida actual
        mov.setSalida(LocalDateTime.now());
        repo.save(mov);

        // 3. Calcular tiempo (Usando tu lógica actual)
        long minutos = java.time.Duration.between(mov.getIngreso(), mov.getSalida()).toMinutes();
        int horasACobrar = (int) Math.ceil(minutos / 60.0);
        if (horasACobrar == 0)
            horasACobrar = 1;

        // 4. Buscar la tarifa (Tu lógica actual)
        TarifaVehiculo tarifaObj = tarifaRepo.findAll().stream()
                .filter(t -> t.getTipoVehiculo().getId().equals(mov.getVehiculo().getTipoVehiculo().getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No hay tarifa configurada"));

        // 5. Crear la Factura
        Factura factura = new Factura();
        factura.setMovimiento(mov);
        factura.setTarifa(tarifaObj.getTarifa());
        factura.setTax(tarifaObj.getTarifa() * 0.19f);
        factura.setHoras(horasACobrar);
        factura.setCreada(LocalDateTime.now());

        return facturaRepo.save(factura);
    }

    @GetMapping("/limpiar-todo")
    @Transactional
    public String limpiarParqueadero() {
        List<MovimientoVehiculo> activos = repo.findBySalidaIsNull();
        LocalDateTime ahora = LocalDateTime.now();

        for (MovimientoVehiculo mov : activos) {
            mov.setSalida(ahora);
            repo.save(mov);
        }
        return "Se han cerrado " + activos.size() + " movimientos. El parqueadero está vacío.";
    }

}
