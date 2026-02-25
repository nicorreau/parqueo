package parqueadero.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;
import parqueadero.model.Color;
import parqueadero.model.Marca;
import parqueadero.model.MovimientoVehiculo;
import parqueadero.model.Propietario;
import parqueadero.model.TipoVehiculo;
import parqueadero.model.Vehiculo;
// Asegúrate de importar todos los repositorios necesarios
import parqueadero.repository.PropietarioRepository;
import parqueadero.repository.TipoVehiculoRepository;
import parqueadero.repository.VehiculoRepository;
import parqueadero.repository.ColorRepository;
import parqueadero.repository.MarcaRepository;
import parqueadero.repository.MovimientoVehiculoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    // INYECCIONES (Estas variables son las que usaremos abajo en minúsculas)
    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private PropietarioRepository propietarioRepository;

    @Autowired
    private MovimientoVehiculoRepository movimientoVehiculoRepository;

    @Autowired
    private MarcaRepository marcaRepository;
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private TipoVehiculoRepository tipoVehiculoRepository;

    @GetMapping
    public List<Vehiculo> listarVehiculos() {
        return vehiculoRepository.findAll();
    }

    @Transactional 
@PostMapping("/registro-completo")
public MovimientoVehiculo registroCompleto(@RequestBody Map<String, Object> data) {

    // --- NUEVO ESCUDO 1: Validación de Nombres Obligatorios ---
    // Ahora validamos que el texto no llegue nulo desde el JS
    if (data.get("nombre_marca") == null || data.get("nombre_color") == null || data.get("nombre_tipo") == null) {
        throw new RuntimeException("ERROR: Marca, Color y Tipo son campos obligatorios.");
    }

    // 1. Validar y Obtener Propietario (Se mantiene igual que tu código original)
    Map<String, Object> propData = (Map<String, Object>) data.get("propietario");
    if (propData == null || propData.get("dni") == null) {
        throw new RuntimeException("ERROR: Los datos del propietario (DNI) son obligatorios.");
    }
    String dni = propData.get("dni").toString();
    Propietario propietario = propietarioRepository.findByDni(dni);

    if (propietario == null) {
        propietario = new Propietario();
        propietario.setNombre((String) propData.get("nombre"));
        propietario.setDni(dni);
        propietario = propietarioRepository.save(propietario);
    }

    // 2. Validar y Obtener Vehículo
    String placa = (String) data.get("placa");
    if (placa == null || placa.trim().isEmpty()) {
        throw new RuntimeException("ERROR: La placa es obligatoria.");
    }

    Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa).orElse(null);

    if (vehiculo == null) {
        vehiculo = new Vehiculo();
        vehiculo.setPlaca(placa);
        vehiculo.setDescripcion((String) data.get("descripcion"));
        vehiculo.setObservaciones((String) data.get("observaciones"));
        vehiculo.setPropietario(propietario);

        // --- NUEVA LÓGICA: Busca por nombre o Crea si no existe ---
        // Esto evita que el sistema se rompa si el usuario escribe algo nuevo
        
        String nMarca = data.get("nombre_marca").toString().toUpperCase().trim();
        vehiculo.setMarca(marcaRepository.findByNombre(nMarca).orElseGet(() -> {
            Marca nueva = new Marca();
            nueva.setNombre(nMarca);
            return marcaRepository.save(nueva);
        }));

        String nColor = data.get("nombre_color").toString().toUpperCase().trim();
        vehiculo.setColor(colorRepository.findByNombre(nColor).orElseGet(() -> {
            Color nuevo = new Color();
            nuevo.setNombre(nColor);
            return colorRepository.save(nuevo);
        }));

        String nTipo = data.get("nombre_tipo").toString().toUpperCase().trim();
        vehiculo.setTipoVehiculo(tipoVehiculoRepository.findByNombre(nTipo).orElseGet(() -> {
            TipoVehiculo nuevo = new TipoVehiculo();
            nuevo.setNombre(nTipo);
            return tipoVehiculoRepository.save(nuevo);
        }));

        vehiculo = vehiculoRepository.save(vehiculo);
    }

    // 3. Crear el Ingreso Automático (Se mantiene igual que tu código original)
    boolean estaDentro = movimientoVehiculoRepository.findByVehiculoPlacaAndSalidaIsNull(placa).isPresent();
    if (estaDentro) {
        throw new RuntimeException("El vehículo con placa " + placa + " ya se encuentra dentro del parqueadero.");
    }

    MovimientoVehiculo movimiento = new MovimientoVehiculo();
    movimiento.setVehiculo(vehiculo);
    movimiento.setIngreso(LocalDateTime.now());

    return movimientoVehiculoRepository.save(movimiento);
}

    @PutMapping("/actualizar-datos/{placa}")
    public Vehiculo actualizarVehiculo(@PathVariable String placa, @RequestBody Vehiculo datosNuevos) {
        // 1. Buscamos el vehículo existente por placa
        Vehiculo vehiculoExistente = vehiculoRepository.findByPlaca(placa)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con placa: " + placa));

        // 2. Actualizamos las relaciones según tu modelo ER
        if (datosNuevos.getTipoVehiculo() != null) {
            vehiculoExistente.setTipoVehiculo(datosNuevos.getTipoVehiculo());
        }
        if (datosNuevos.getMarca() != null) {
            vehiculoExistente.setMarca(datosNuevos.getMarca());
        }
        if (datosNuevos.getColor() != null) {
            vehiculoExistente.setColor(datosNuevos.getColor());
        }
        if (datosNuevos.getPropietario() != null) {
            vehiculoExistente.setPropietario(datosNuevos.getPropietario());
        }

        // 3. Guardamos los cambios
        return vehiculoRepository.save(vehiculoExistente);
    }

}