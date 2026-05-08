package org.example.service;

import org.example.application.dto.TollDTO;
import org.example.application.exception.ResourceNotFoundException;
import org.example.application.mapper.TollMapper;
import org.example.model.Toll;
import org.example.repository.TollRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TollService {

    private final TollRepository tollRepository;
    private final TollMapper tollMapper;

    public TollService(TollRepository tollRepository, TollMapper tollMapper) {
        this.tollRepository = tollRepository;
        this.tollMapper = tollMapper;
    }

    public List<TollDTO> getAllTolls() {
        return tollRepository.findAll().stream()
                .map(tollMapper::toDto)
                .collect(Collectors.toList());
    }

    public TollDTO getTollById(Long id) {
        return tollMapper.toDto(tollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Toll not found: " + id)));
    }

    public TollDTO createToll(TollDTO tollDTO) {
        return tollMapper.toDto(tollRepository.save(tollMapper.toEntity(tollDTO)));
    }

    public TollDTO updateToll(Long id, TollDTO tollDTO) {
        Toll toll = tollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Toll not found: " + id));
        tollMapper.updateEntityFromDto(tollDTO, toll);
        return tollMapper.toDto(tollRepository.save(toll));
    }

    public void deleteToll(Long id) {
        if (!tollRepository.existsById(id)) {
            throw new ResourceNotFoundException("Toll not found: " + id);
        }
        tollRepository.deleteById(id);
    }
}
