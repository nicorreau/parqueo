package parqueadero.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;
import parqueadero.model.Factura;
import parqueadero.model.MovimientoVehiculo;
import parqueadero.model.TarifaVehiculo;
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

    @Transactional
    @PutMapping("/salida/{id}")
    public Factura registrarSalida(@PathVariable Integer id) {
        // 1. Buscar movimiento
        MovimientoVehiculo mov = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));

        // 2. Registrar salida
        mov.setSalida(LocalDateTime.now());
        repo.save(mov);

        // 3. Calcular tiempo
        long minutos = java.time.Duration.between(mov.getIngreso(), mov.getSalida()).toMinutes();
        int horasACobrar = (int) Math.ceil(minutos / 60.0);
        if (horasACobrar <= 0)
            horasACobrar = 1;

        // 4. Búsqueda de tarifa con respaldo (EVITA EL ERROR "No hay tarifa")
        TarifaVehiculo tarifaObj = tarifaRepo.findAll().stream()
                .filter(t -> t.getTipoVehiculo() != null &&
                        t.getTipoVehiculo().getId().equals(mov.getVehiculo().getTipoVehiculo().getId()))
                .findFirst()
                .orElseGet(() -> {
                    // Si no hay tarifa en DB, creamos una temporal para que no falle el proceso
                    TarifaVehiculo base = new TarifaVehiculo();
                    base.setTarifa(2000.0f);
                    base.setTax(0.19f);
                    return base;
                });

        // 5. Crear Factura
        Factura factura = new Factura();
        factura.setMovimiento(mov);
        factura.setTarifa(tarifaObj.getTarifa());

        // Calculamos el IVA basándonos en el tax de la tarifa (0.19 = 19%)
        factura.setTax(tarifaObj.getTarifa() * tarifaObj.getTax());
        factura.setHoras(horasACobrar);
        factura.setCreada(LocalDateTime.now());

        // 6. GUARDAR Y RETORNAR (IMPORTANTE)
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
        // Busca la tarifa, pero si no la encuentra, usa una de emergencia
        TarifaVehiculo tarifaObj = tarifaRepo.findAll().stream()
                .filter(t -> t.getTipoVehiculo() != null &&
                        t.getTipoVehiculo().getId().equals(mov.getVehiculo().getTipoVehiculo().getId()))
                .findFirst()
                .orElseGet(() -> {
                    // En lugar de orElseThrow, usamos orElseGet para dar una respuesta de
                    // emergencia
                    TarifaVehiculo defaultTarifa = new TarifaVehiculo();
                    defaultTarifa.setTarifa(2000.0f); // Valor por defecto para no romper el proceso
                    defaultTarifa.setTax(0.19f);
                    return defaultTarifa;
                });
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