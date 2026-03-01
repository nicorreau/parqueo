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

    // 1. Validación de campos obligatorios
    if (data.get("nombre_marca") == null || data.get("nombre_color") == null || data.get("nombre_tipo") == null || data.get("placa") == null) {
        throw new RuntimeException("ERROR: Placa, Marca, Color y Tipo son obligatorios.");
    }

    // 2. Gestión del Propietario (EVITA EL ERROR DE DUPLICADO)
    Map<String, Object> propData = (Map<String, Object>) data.get("propietario");
    if (propData == null || propData.get("dni") == null) {
        throw new RuntimeException("ERROR: Los datos del propietario (DNI) son obligatorios.");
    }
    
    String dni = propData.get("dni").toString().trim();
    // Buscamos primero para no intentar insertar un DNI que ya existe
    Propietario propietario = propietarioRepository.findByDni(dni);

    if (propietario == null) {
        propietario = new Propietario();
        propietario.setNombre((String) propData.get("nombre"));
        propietario.setDni(dni);
        propietario = propietarioRepository.save(propietario);
    }

    // 3. Gestión del Vehículo (SOLUCIONA TYPE MISMATCH)
    String placa = data.get("placa").toString().toUpperCase().trim();
    Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa).orElse(null);

    if (vehiculo == null) {
        vehiculo = new Vehiculo();
        vehiculo.setPlaca(placa);
    }

    // Actualizamos siempre estas relaciones para asegurar que el vehículo esté completo
    vehiculo.setDescripcion((String) data.get("descripcion"));
    vehiculo.setObservaciones((String) data.get("observaciones"));
    vehiculo.setPropietario(propietario);

    // Lógica para Marca
    String nMarca = data.get("nombre_marca").toString().toUpperCase().trim();
    Marca marca = marcaRepository.findByNombre(nMarca).orElse(null);
    if (marca == null) {
        marca = new Marca();
        marca.setNombre(nMarca);
        marca = marcaRepository.save(marca);
    }
    vehiculo.setMarca(marca);

    // Lógica para Color
    String nColor = data.get("nombre_color").toString().toUpperCase().trim();
    Color color = colorRepository.findByNombre(nColor).orElse(null);
    if (color == null) {
        color = new Color();
        color.setNombre(nColor);
        color = colorRepository.save(color);
    }
    vehiculo.setColor(color);

    // Lógica para Tipo (CRUCIAL PARA LA TARIFA)
    String nTipo = data.get("nombre_tipo").toString().toUpperCase().trim();
    TipoVehiculo tipo = tipoVehiculoRepository.findByNombre(nTipo).orElse(null);
    if (tipo == null) {
        tipo = new TipoVehiculo();
        tipo.setNombre(nTipo);
        tipo = tipoVehiculoRepository.save(tipo);
    }
    vehiculo.setTipoVehiculo(tipo);

    // Guardamos el vehículo (nuevo o actualizado)
    vehiculo = vehiculoRepository.save(vehiculo);

    // 4. Crear el Movimiento de Ingreso
    boolean estaDentro = movimientoVehiculoRepository.findByVehiculoPlacaAndSalidaIsNull(placa).isPresent();
    if (estaDentro) {
        throw new RuntimeException("El vehículo con placa " + placa + " ya se encuentra dentro.");
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