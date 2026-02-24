package parqueadero.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import parqueadero.model.TarifaVehiculo;
import parqueadero.repository.TarifaVehiculoRepository;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tarifas")
public class TarifaVehiculoController {

    @Autowired
    private TarifaVehiculoRepository tarifaRepository;

    @GetMapping
    public List<TarifaVehiculo> listarTarifas() {
        return tarifaRepository.findAll();
    }
}