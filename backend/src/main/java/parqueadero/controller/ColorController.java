package parqueadero.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import parqueadero.model.Color;
import parqueadero.repository.ColorRepository;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/colores")
public class ColorController {

    @Autowired
    private ColorRepository colorRepository;

    @GetMapping
    public List<Color> listarTodos() {
        return colorRepository.findAll();
    }
}