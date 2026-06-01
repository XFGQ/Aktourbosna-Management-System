package org.example.controller;

import jakarta.validation.Valid;
import org.example.application.dto.expense.ExpenseCreateDTO;
import org.example.application.dto.expense.ExpenseResponseDTO;
import org.example.application.dto.expense.ExpenseUpdateDTO;
import org.example.service.ExpenseService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tours/{tourId}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByTour(@PathVariable Long tourId) {
        return ResponseEntity.ok(expenseService.getExpensesByTour(tourId));
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponseDTO> getExpenseById(@PathVariable Long tourId,
                                                             @PathVariable Long expenseId) {
        return ResponseEntity.ok(expenseService.getExpenseById(expenseId));
    }

    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> createExpense(@PathVariable Long tourId,
                                                            @Valid @RequestBody ExpenseCreateDTO dto) {
        return ResponseEntity.ok(expenseService.createExpense(tourId, dto));
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(@PathVariable Long tourId,
                                                            @PathVariable Long expenseId,
                                                            @RequestBody ExpenseUpdateDTO dto) {
        return ResponseEntity.ok(expenseService.updateExpense(expenseId, dto));
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long tourId,
                                              @PathVariable Long expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{expenseId}/receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExpenseResponseDTO> uploadReceipt(@PathVariable Long tourId,
                                                            @PathVariable Long expenseId,
                                                            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(expenseService.uploadReceipt(expenseId, file));
    }

    @DeleteMapping("/{expenseId}/receipt")
    public ResponseEntity<ExpenseResponseDTO> deleteReceipt(@PathVariable Long tourId,
                                                            @PathVariable Long expenseId) {
        return ResponseEntity.ok(expenseService.deleteReceipt(expenseId));
    }

    @GetMapping("/{expenseId}/receipt")
    public ResponseEntity<Resource> downloadReceipt(@PathVariable Long tourId,
                                                    @PathVariable Long expenseId) {
        Resource resource = expenseService.loadReceiptAsResource(expenseId);
        String contentType = "application/octet-stream";
        String filename = resource.getFilename();
        if (filename != null) {
            if (filename.toLowerCase().endsWith(".pdf")) contentType = MediaType.APPLICATION_PDF_VALUE;
            else if (filename.toLowerCase().endsWith(".png")) contentType = MediaType.IMAGE_PNG_VALUE;
            else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) contentType = MediaType.IMAGE_JPEG_VALUE;
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}
