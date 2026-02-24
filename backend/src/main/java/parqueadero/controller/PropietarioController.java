package parqueadero.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import parqueadero.model.Propietario;
import parqueadero.repository.PropietarioRepository;
import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/propietarios")
public class PropietarioController {
    @Autowired
    private PropietarioRepository repo;

    @GetMapping
    public List<Propietario> listar() { return repo.findAll(); }
}