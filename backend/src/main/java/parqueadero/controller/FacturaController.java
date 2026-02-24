package parqueadero.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import parqueadero.model.Factura;
import parqueadero.repository.FacturaRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FacturaRepository facturaRepository;

    @GetMapping
    public List<Factura> listarFacturas() {
        return facturaRepository.findAll();
    }

    @GetMapping("/recaudo-total")
    public Map<String, Object> verRecaudo() {
        Double total = facturaRepository.obtenerRecaudoTotal();
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Cierre de caja total");
        respuesta.put("totalRecaudado", total != null ? total : 0.0);
        respuesta.put("cantidadFacturas", facturaRepository.count());
        
        return respuesta;
    }
}