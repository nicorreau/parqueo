package parqueadero.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import parqueadero.model.TarifaVehiculo;
import parqueadero.model.TipoVehiculo;
import parqueadero.repository.TarifaVehiculoRepository;
import parqueadero.repository.TipoVehiculoRepository; // Necesario para buscar el tipo

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tarifas")
public class TarifaVehiculoController {

    @Autowired
    private TarifaVehiculoRepository tarifaRepository;

    @Autowired
    private TipoVehiculoRepository tipoVehiculoRepository; // Inyectamos esto para la línea 33

    @GetMapping
    public List<TarifaVehiculo> listarTarifas() {
        return tarifaRepository.findAll();
    }

    // NUEVO MÉTODO: Sincroniza con tu formulario web
    @PutMapping("/actualizar-por-tipo/{idTipo}")
    public TarifaVehiculo actualizarPorTipo(@PathVariable Integer idTipo, @RequestBody TarifaVehiculo nuevaTarifa) {
        
        // 1. Buscamos el TipoVehiculo primero para evitar errores de Optional
        TipoVehiculo tipo = tipoVehiculoRepository.findById(idTipo)
                .orElseThrow(() -> new RuntimeException("Tipo de vehículo no encontrado con ID: " + idTipo));

        // 2. Buscamos si ya existe una tarifa para ese tipo o creamos una nueva
        TarifaVehiculo existente = tarifaRepository.findAll().stream()
                .filter(t -> t.getTipoVehiculo() != null && t.getTipoVehiculo().getId().equals(idTipo))
                .findFirst()
                .orElseGet(() -> {
                    TarifaVehiculo nueva = new TarifaVehiculo();
                    nueva.setTipoVehiculo(tipo); // Asignamos el tipo que encontramos arriba
                    return nueva;
                });

        // 3. Asignamos los valores (Precio e IVA)
        existente.setTarifa(nuevaTarifa.getTarifa());
        
        // Si el usuario envía "19", lo convertimos a "0.19"
        float taxValue = nuevaTarifa.getTax() > 1 ? nuevaTarifa.getTax() / 100 : nuevaTarifa.getTax();
        existente.setTax(taxValue);
        
        return tarifaRepository.save(existente);
    }
}