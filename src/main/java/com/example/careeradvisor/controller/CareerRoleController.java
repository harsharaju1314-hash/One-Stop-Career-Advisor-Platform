package com.example.careeradvisor.controller;

import com.example.careeradvisor.dto.CareerRoleResponse;
import com.example.careeradvisor.service.CareerRoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class CareerRoleController {

    private final CareerRoleService careerRoleService;

    public CareerRoleController(CareerRoleService careerRoleService) {
        this.careerRoleService = careerRoleService;
    }

    @GetMapping
    public ResponseEntity<List<CareerRoleResponse>> getAllRoles() {
        List<CareerRoleResponse> roles = careerRoleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CareerRoleResponse> getRoleById(@PathVariable Long id) {
        CareerRoleResponse response = careerRoleService.getRoleById(id);
        return ResponseEntity.ok(response);
    }
}
