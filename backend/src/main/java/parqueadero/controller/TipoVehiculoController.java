package parqueadero.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import parqueadero.model.TipoVehiculo;
import parqueadero.repository.TipoVehiculoRepository;
import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tipos")
public class TipoVehiculoController {
    @Autowired
    private TipoVehiculoRepository repo;

    @GetMapping
    public List<TipoVehiculo> listar() { return repo.findAll(); }
}
