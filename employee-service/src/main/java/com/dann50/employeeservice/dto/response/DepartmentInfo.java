package com.dann50.employeeservice.dto.response;

public class DepartmentInfo {

    private final Integer id;
    private final String name;
    private final String description;
    private final int numberOfEmployees;

    public DepartmentInfo(Integer id, String name, String description, int numberOfEmployees) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.numberOfEmployees = numberOfEmployees;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getNumberOfEmployees() {
        return numberOfEmployees;
    }
}
