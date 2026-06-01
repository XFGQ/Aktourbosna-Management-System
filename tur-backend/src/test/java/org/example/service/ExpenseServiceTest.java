package org.example.service;

import org.example.application.dto.expense.ExpenseCreateDTO;
import org.example.application.dto.expense.ExpenseResponseDTO;
import org.example.application.dto.expense.ExpenseUpdateDTO;
import org.example.application.mapper.ExpenseMapper;
import org.example.model.Expense;
import org.example.model.Tour;
import org.example.repository.ExpenseRepository;
import org.example.repository.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock ExpenseRepository expenseRepository;
    @Mock ExpenseMapper expenseMapper;
    @Mock TourRepository tourRepository;
    @InjectMocks ExpenseService expenseService;

    private Expense expense;
    private ExpenseResponseDTO responseDTO;

    @BeforeEach
    void setup() {
        Tour tour = new Tour();
        tour.setTourId(1L);

        expense = new Expense();
        expense.setExpenseId(1L);
        expense.setCategory("Hotel");
        expense.setAmount(500.0f);
        expense.setDate(LocalDate.of(2024, 6, 1));
        expense.setTour(tour);

        responseDTO = new ExpenseResponseDTO();
        responseDTO.setExpenseId(1L);
        responseDTO.setCategory("Hotel");
        responseDTO.setAmount(500.0f);
        responseDTO.setTourId(1L);
    }

    @Test
    void getExpensesByTour_returnsFilteredList() {
        when(expenseRepository.findByTour_TourId(1L)).thenReturn(List.of(expense));
        when(expenseMapper.toResponse(expense)).thenReturn(responseDTO);

        List<ExpenseResponseDTO> result = expenseService.getExpensesByTour(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("Hotel");
    }

    @Test
    void getExpensesByTour_noExpenses_returnsEmptyList() {
        when(expenseRepository.findByTour_TourId(99L)).thenReturn(List.of());

        List<ExpenseResponseDTO> result = expenseService.getExpensesByTour(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void getExpenseById_found_returnsDTO() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(expenseMapper.toResponse(expense)).thenReturn(responseDTO);

        ExpenseResponseDTO result = expenseService.getExpenseById(1L);

        assertThat(result.getExpenseId()).isEqualTo(1L);
        assertThat(result.getCategory()).isEqualTo("Hotel");
    }

    @Test
    void getExpenseById_notFound_throwsRuntimeException() {
        when(expenseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseService.getExpenseById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createExpense_tourExists_savesAndReturnsDTO() {
        ExpenseCreateDTO dto = new ExpenseCreateDTO();
        dto.setCategory("Transport");
        dto.setAmount(200.0f);

        Tour tour = new Tour();
        tour.setTourId(1L);

        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(expenseMapper.toEntity(dto)).thenReturn(expense);
        when(expenseRepository.save(expense)).thenReturn(expense);
        when(expenseMapper.toResponse(expense)).thenReturn(responseDTO);

        ExpenseResponseDTO result = expenseService.createExpense(1L, dto);

        assertThat(result.getCategory()).isEqualTo("Hotel");
        verify(expenseRepository).save(expense);
    }

    @Test
    void createExpense_tourNotFound_throwsRuntimeException() {
        when(tourRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseService.createExpense(99L, new ExpenseCreateDTO()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateExpense_found_updatesAndReturns() {
        ExpenseUpdateDTO dto = new ExpenseUpdateDTO();
        dto.setCategory("Food");

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(expense)).thenReturn(expense);
        when(expenseMapper.toResponse(expense)).thenReturn(responseDTO);

        ExpenseResponseDTO result = expenseService.updateExpense(1L, dto);

        assertThat(result).isEqualTo(responseDTO);
        verify(expenseMapper).updateEntityFromDto(dto, expense);
    }

    @Test
    void updateExpense_notFound_throwsRuntimeException() {
        when(expenseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseService.updateExpense(99L, new ExpenseUpdateDTO()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteExpense_exists_deletesSuccessfully() {
        when(expenseRepository.existsById(1L)).thenReturn(true);

        expenseService.deleteExpense(1L);

        verify(expenseRepository).deleteById(1L);
    }

    @Test
    void deleteExpense_notFound_throwsRuntimeException() {
        when(expenseRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> expenseService.deleteExpense(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }
}
