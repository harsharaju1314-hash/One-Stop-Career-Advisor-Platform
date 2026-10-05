package com.example.careeradvisor.controller;

import com.example.careeradvisor.dto.CareerAdviceResponse;
import com.example.careeradvisor.service.CareerAdviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/career-advice")
public class CareerAdviceController {

    private final CareerAdviceService careerAdviceService;

    public CareerAdviceController(CareerAdviceService careerAdviceService) {
        this.careerAdviceService = careerAdviceService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CareerAdviceResponse> getCareerAdvice(@PathVariable Long userId) {
        CareerAdviceResponse response = careerAdviceService.generateCareerAdvice(userId);
        return ResponseEntity.ok(response);
    }
}
