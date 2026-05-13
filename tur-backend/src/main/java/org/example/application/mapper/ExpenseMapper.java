package org.example.application.mapper;

import org.example.application.dto.expense.ExpenseCreateDTO;
import org.example.application.dto.expense.ExpenseResponseDTO;
import org.example.application.dto.expense.ExpenseUpdateDTO;
import org.example.model.Expense;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExpenseMapper {

    @Mapping(source = "tour.tourId", target = "tourId")
    ExpenseResponseDTO toResponse(Expense expense);

    @Mapping(target = "tour", ignore = true)
    @Mapping(target = "expenseId", ignore = true)
    Expense toEntity(ExpenseCreateDTO dto);

    @Mapping(target = "tour", ignore = true)
    @Mapping(target = "expenseId", ignore = true)
    void updateEntityFromDto(ExpenseUpdateDTO dto, @MappingTarget Expense entity);
}
