package com.dann50.employeeservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateDepartmentRequest {

    @NotBlank(message = "Department name is mandatory")
    private String name;
    @NotBlank(message = "Department description is mandatory")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
