package org.example.service;

import org.example.application.dto.TollDTO;
import org.example.application.exception.ResourceNotFoundException;
import org.example.application.mapper.TollMapper;
import org.example.model.Toll;
import org.example.repository.TollRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TollServiceTest {

    @Mock private TollRepository tollRepository;
    @Mock private TollMapper tollMapper;

    @InjectMocks private TollService tollService;

    private Toll entity;
    private TollDTO dto;

    @BeforeEach
    void setUp() {
        entity = new Toll();
        dto = new TollDTO(1L, "Bosphorus Bridge", "Istanbul", 30.0f, 45.0f, 60.0f);
    }

    @Test
    @DisplayName("getAllTolls - returns list of toll DTOs")
    void getAllTolls_returnsList() {
        when(tollRepository.findAll()).thenReturn(List.of(entity));
        when(tollMapper.toDto(entity)).thenReturn(dto);

        List<TollDTO> result = tollService.getAllTolls();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Bosphorus Bridge");
        verify(tollRepository).findAll();
    }

    @Test
    @DisplayName("getTollById - existing ID returns DTO")
    void getTollById_existing_returnsDto() {
        when(tollRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(tollMapper.toDto(entity)).thenReturn(dto);

        TollDTO result = tollService.getTollById(1L);

        assertThat(result.getTollId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getTollById - non-existing ID throws ResourceNotFoundException")
    void getTollById_nonExisting_throws() {
        when(tollRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tollService.getTollById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Toll not found");
    }

    @Test
    @DisplayName("createToll - saves and returns DTO")
    void createToll_savesAndReturnsDto() {
        when(tollMapper.toEntity(dto)).thenReturn(entity);
        when(tollRepository.save(entity)).thenReturn(entity);
        when(tollMapper.toDto(entity)).thenReturn(dto);

        TollDTO result = tollService.createToll(dto);

        assertThat(result.getName()).isEqualTo("Bosphorus Bridge");
        verify(tollRepository).save(entity);
    }

    @Test
    @DisplayName("updateToll - non-existing ID throws ResourceNotFoundException")
    void updateToll_nonExisting_throws() {
        when(tollRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tollService.updateToll(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Toll not found");
    }

    @Test
    @DisplayName("deleteToll - non-existing ID throws and does not delete")
    void deleteToll_nonExisting_throws() {
        when(tollRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> tollService.deleteToll(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Toll not found");

        verify(tollRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("deleteToll - existing ID deletes successfully")
    void deleteToll_existing_deletes() {
        when(tollRepository.existsById(1L)).thenReturn(true);

        tollService.deleteToll(1L);

        verify(tollRepository).deleteById(1L);
    }
}
