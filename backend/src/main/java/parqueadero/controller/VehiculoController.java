package parqueadero.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;

import parqueadero.model.MovimientoVehiculo;
import parqueadero.model.Propietario;
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

    @Transactional // Agregamos esto para que si algo falla, no guarde a medias
    @PostMapping("/registro-completo")
    public MovimientoVehiculo registroCompleto(@RequestBody Map<String, Object> data) {

        // --- ESCUDO 1: Validación de Datos Obligatorios ---
        // Si faltan estos IDs, el método se detiene aquí y no crea basura
        if (data.get("id_marca") == null || data.get("id_color") == null || data.get("id_tipo") == null) {
            throw new RuntimeException("ERROR: id_marca, id_color e id_tipo son obligatorios para el registro.");
        }

        // 1. Validar y Obtener Propietario
        Map<String, Object> propData = (Map<String, Object>) data.get("propietario");
        if (propData == null || propData.get("dni") == null) {
            throw new RuntimeException("ERROR: Los datos del propietario (DNI) son obligatorios.");
        }
        String dni = propData.get("dni").toString();

        // Buscamos si ya existe el propietario por DNI
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

            // --- ESCUDO 2: Asignación Forzada de Relaciones ---
            // Usamos .orElseThrow para asegurar que los IDs existan en la base de datos
            vehiculo.setMarca(marcaRepository.findById(Integer.parseInt(data.get("id_marca").toString()))
                    .orElseThrow(
                            () -> new RuntimeException("La Marca con ID " + data.get("id_marca") + " no existe.")));

            vehiculo.setColor(colorRepository.findById(Integer.parseInt(data.get("id_color").toString()))
                    .orElseThrow(
                            () -> new RuntimeException("El Color con ID " + data.get("id_color") + " no existe.")));

            vehiculo.setTipoVehiculo(tipoVehiculoRepository.findById(Integer.parseInt(data.get("id_tipo").toString()))
                    .orElseThrow(() -> new RuntimeException(
                            "El Tipo de Vehículo con ID " + data.get("id_tipo") + " no existe.")));

            vehiculo = vehiculoRepository.save(vehiculo);
        }

        // 3. Crear el Ingreso Automático
        // --- ESCUDO 3: Evitar doble ingreso ---
        // Verificamos si el vehículo ya tiene un movimiento activo (sin salida)
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