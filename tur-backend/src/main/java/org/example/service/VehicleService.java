package org.example.service;

import org.example.application.dto.VehicleDTO;
import org.example.application.exception.ResourceNotFoundException;
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
        return vehicleRepository.findAll().stream()
                .map(vehicleMapper::toDto)
                .collect(Collectors.toList());
    }

    public VehicleDTO createVehicle(VehicleDTO vehicleDTO) {
        return vehicleMapper.toDto(vehicleRepository.save(vehicleMapper.toEntity(vehicleDTO)));
    }

    public VehicleDTO getVehicleById(Long id) {
        return vehicleMapper.toDto(vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + id)));
    }

    public VehicleDTO updateVehicle(Long id, VehicleDTO vehicleDTO) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + id));
        vehicleMapper.updateEntityFromDto(vehicleDTO, existing);
        return vehicleMapper.toDto(vehicleRepository.save(existing));
    }

    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle not found: " + id);
        }
        vehicleRepository.deleteById(id);
    }
}
