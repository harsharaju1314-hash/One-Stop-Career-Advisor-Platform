package com.example.careeradvisor.dto;

public class CareerRoleSummaryDto {

    private Long id;
    private String title;

    public CareerRoleSummaryDto() {
    }

    public CareerRoleSummaryDto(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
