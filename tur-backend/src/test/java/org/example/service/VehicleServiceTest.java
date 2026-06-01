package org.example.service;

import org.example.application.dto.vehicle.VehicleCreateDTO;
import org.example.application.dto.vehicle.VehicleResponseDTO;
import org.example.application.dto.vehicle.VehicleSummaryDTO;
import org.example.application.dto.vehicle.VehicleUpdateDTO;
import org.example.application.exception.ResourceNotFoundException;
import org.example.application.mapper.VehicleMapper;
import org.example.model.Vehicle;
import org.example.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock VehicleRepository vehicleRepository;
    @Mock VehicleMapper vehicleMapper;
    @InjectMocks VehicleService vehicleService;

    private Vehicle vehicle;
    private VehicleResponseDTO responseDTO;
    private VehicleSummaryDTO summaryDTO;

    @BeforeEach
    void setup() {
        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setBrand("Mercedes");
        vehicle.setModel("Sprinter");
        vehicle.setPlateNumber("34 ABC 123");
        vehicle.setSeatCapacity(20);

        responseDTO = new VehicleResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setBrand("Mercedes");
        responseDTO.setPlateNumber("34 ABC 123");

        summaryDTO = new VehicleSummaryDTO();
        summaryDTO.setId(1L);
        summaryDTO.setBrand("Mercedes");
        summaryDTO.setPlateNumber("34 ABC 123");
    }

    @Test
    void getAllVehicles_returnsMappedSummaryList() {
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));
        when(vehicleMapper.toSummary(vehicle)).thenReturn(summaryDTO);

        List<VehicleSummaryDTO> result = vehicleService.getAllVehicles();

        assertThat(result).hasSize(1).containsExactly(summaryDTO);
        verify(vehicleRepository).findAll();
    }

    @Test
    void getAllVehicles_emptyRepository_returnsEmptyList() {
        when(vehicleRepository.findAll()).thenReturn(List.of());

        List<VehicleSummaryDTO> result = vehicleService.getAllVehicles();

        assertThat(result).isEmpty();
    }

    @Test
    void getVehicleById_found_returnsDTO() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toResponse(vehicle)).thenReturn(responseDTO);

        VehicleResponseDTO result = vehicleService.getVehicleById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBrand()).isEqualTo("Mercedes");
    }

    @Test
    void getVehicleById_notFound_throwsResourceNotFoundException() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.getVehicleById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createVehicle_savesAndReturnsDTO() {
        VehicleCreateDTO dto = new VehicleCreateDTO();
        dto.setBrand("Mercedes");
        dto.setModel("Sprinter");
        dto.setPlateNumber("34 ABC 123");
        dto.setSeatCapacity(20);

        when(vehicleMapper.toEntity(dto)).thenReturn(vehicle);
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toResponse(vehicle)).thenReturn(responseDTO);

        VehicleResponseDTO result = vehicleService.createVehicle(dto);

        assertThat(result.getBrand()).isEqualTo("Mercedes");
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void updateVehicle_found_updatesAndReturns() {
        VehicleUpdateDTO dto = new VehicleUpdateDTO();
        dto.setBrand("Volkswagen");

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.toResponse(vehicle)).thenReturn(responseDTO);

        VehicleResponseDTO result = vehicleService.updateVehicle(1L, dto);

        assertThat(result).isEqualTo(responseDTO);
        verify(vehicleMapper).updateEntityFromDto(dto, vehicle);
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void updateVehicle_notFound_throwsResourceNotFoundException() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.updateVehicle(99L, new VehicleUpdateDTO()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteVehicle_exists_deletesSuccessfully() {
        when(vehicleRepository.existsById(1L)).thenReturn(true);

        vehicleService.deleteVehicle(1L);

        verify(vehicleRepository).deleteById(1L);
    }

    @Test
    void deleteVehicle_notFound_throwsResourceNotFoundException() {
        when(vehicleRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> vehicleService.deleteVehicle(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
