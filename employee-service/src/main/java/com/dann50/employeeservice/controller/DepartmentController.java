package com.dann50.employeeservice.controller;

import com.dann50.employeeservice.dto.request.CreateDepartmentRequest;
import com.dann50.employeeservice.dto.response.DepartmentInfo;
import com.dann50.employeeservice.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/departments")
@Tag(name = "Department management",
    description = "Endpoints that handle department management, including creation," +
        " deletion and updates. Most of it only accessible by admin"
)
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping("")
    @Operation(summary = "Create a new department", description = "Creates a new department and" +
        " requires the department name and description.")
    public ResponseEntity<DepartmentInfo> createDepartment(@RequestBody @Valid CreateDepartmentRequest request) {
        return ResponseEntity.accepted().body(departmentService.createDepartment(request));
    }

    @GetMapping("")
    @Operation(summary = "Get all departments", description = "Retrieves all info on the departments " +
        "including name, description and number of employees in each.")
    public ResponseEntity<List<DepartmentInfo>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update department", description = "Update the info on a department, namely " +
        "the department's description")
    public ResponseEntity<HttpStatus> updateDepartmentDesc(@PathVariable Integer id, String newDesc) {
        departmentService.updateDepartmentDescription(newDesc, id);
        return ResponseEntity.accepted().body(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete department", description = "Remove department from database")
    public ResponseEntity<HttpStatus> deleteDepartment(@PathVariable Integer id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.accepted().body(HttpStatus.OK);
    }


}
