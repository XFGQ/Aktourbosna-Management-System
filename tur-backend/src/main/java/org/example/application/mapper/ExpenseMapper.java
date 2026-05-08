package org.example.application.mapper;

import org.example.application.dto.ExpenseDTO;
import org.example.model.Expense;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExpenseMapper {

    @Mapping(source = "tour.tourId", target = "tourId")
    ExpenseDTO toDto(Expense expense);

    @Mapping(source = "tourId", target = "tour.tourId")
    Expense toEntity(ExpenseDTO dto);

    @Mapping(target = "tour", ignore = true)
    @Mapping(target = "expenseId", ignore = true)
    void updateEntityFromDto(ExpenseDTO dto, @MappingTarget Expense entity);
}
