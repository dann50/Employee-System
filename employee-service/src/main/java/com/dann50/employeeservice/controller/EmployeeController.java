package com.dann50.employeeservice.controller;

import com.dann50.employeeservice.dto.request.CreateEmployeeRequest;
import com.dann50.employeeservice.dto.response.CreateEmployeeResponse;
import com.dann50.employeeservice.dto.response.EmployeeResponse;
import com.dann50.employeeservice.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/employees")
@Tag(name = "Employees",
    description = "Endpoints that handle employee management, updating, deletion etc."
)
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("")
    @Operation(summary = "Create a new employee", description = "Creates a new employee. Only" +
        "accessible by admin and most of the JSON data is required.")
    public ResponseEntity<CreateEmployeeResponse> createEmployee(@RequestBody @Valid CreateEmployeeRequest request) {
        return ResponseEntity
            .accepted()
            .body(employeeService.createEmployee(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve an employee's details", description = "Fetches the info on a" +
        " single employee using their id. An admin can view info for all employees, a manager" +
        " can view info for employees in their department. An employee can view only their info")
    public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable Long id, Authentication a) {
        return ResponseEntity.ok(employeeService.getEmployeeDetails(id, a));
    }

    @PutMapping("/{id}/salary")
    @Operation(summary = "Update employee salary", description = "Allows to update an employee's" +
        " salary. Admin-only operation")
    public ResponseEntity<HttpStatus> updateEmployeeSalary(@PathVariable Long id, @RequestParam String newSalary) {
        employeeService.updateEmployeeSalary(id, newSalary);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/department")
    @Operation(summary = "Update employee's department", description = "Allows to update an employee's" +
        " department. Admin-only operation")
    public ResponseEntity<HttpStatus> updateEmployeeDepartment(@PathVariable Long id, @RequestParam String newDept) {
        employeeService.updateEmployeeDepartment(id, newDept);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employee", description = "Deletes an employee. Admin-only operation")
    public ResponseEntity<HttpStatus> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-department")
    @Operation(summary = "View employees in department", description = "Allows to view all employees" +
        " in the viewer's department. Not accessible to regular employees.")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesInDepartment(Authentication a) {
        return ResponseEntity.ok(employeeService.getAllEmployeesInMyDepartment(a));
    }




}
