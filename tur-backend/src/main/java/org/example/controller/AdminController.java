package org.example.controller;

import org.example.application.dto.AdminDTO;
import org.example.application.dto.AdminRequest;
import org.example.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public List<AdminDTO> getAll() {
        return adminService.getAll();
    }

    @GetMapping("/{id}")
    public AdminDTO getById(@PathVariable Long id) {
        return adminService.getById(id);
    }

    // No POST - admin profile is auto-created when user is created with role=ADMIN
    // Only PUT to update admin details (e.g., department)

    @PutMapping("/{id}")
    public AdminDTO update(@PathVariable Long id, @RequestBody AdminRequest request) {
        return adminService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        adminService.delete(id);
    }
}