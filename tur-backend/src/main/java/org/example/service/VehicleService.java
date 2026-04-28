package org.example.service;

import org.example.application.dto.VehicleDTO;
import org.example.application.mapper.VehicleMapper;
import org.example.model.Vehicle;
import org.example.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

     public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

     public List<VehicleDTO> getAllVehicles() {
         List<Vehicle> vehicles = vehicleRepository.findAll();

         return vehicles.stream()
                .map(vehicleMapper::toDto)
                .collect(Collectors.toList());
    }

     public VehicleDTO createVehicle(VehicleDTO vehicleDTO) {
        Vehicle vehicle = vehicleMapper.toEntity(vehicleDTO);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toDto(savedVehicle);
    }
     public VehicleDTO getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Araç bulunamadı. ID: " + id));
        return vehicleMapper.toDto(vehicle);
    }

     public VehicleDTO updateVehicle(Long id, VehicleDTO vehicleDTO) {
         Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Güncellenecek araç bulunamadı. ID: " + id));

         vehicleMapper.updateEntityFromDto(vehicleDTO, existingVehicle);

         Vehicle savedVehicle = vehicleRepository.save(existingVehicle);
        return vehicleMapper.toDto(savedVehicle);
    }

     public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new RuntimeException("Silinecek araç bulunamadı. ID: " + id);
        }
        vehicleRepository.deleteById(id);
    }
}