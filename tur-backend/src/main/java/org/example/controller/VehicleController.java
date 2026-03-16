package org.example.controller;

import org.example.model.Vehicle;
import org.example.repository.VehicleRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles") //if spring get an something called api/tours send this request this class
@CrossOrigin(origins = "*")
public class VehicleController {

    private final VehicleRepository vehicleRepository;

    public VehicleController(VehicleRepository vehicleRepository){
        this.vehicleRepository=vehicleRepository;
    }
    @GetMapping //except this thing api/vehicles become 404
    public List<Vehicle> getAllVehicles(){
        return vehicleRepository.findAll();
    }
}
