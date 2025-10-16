package com.dann50.employeeservice.service;

import com.dann50.employeeservice.dto.request.CreateDepartmentRequest;
import com.dann50.employeeservice.dto.response.DepartmentInfo;
import com.dann50.employeeservice.entity.Department;
import com.dann50.employeeservice.repository.DepartmentRepository;
import com.dann50.employeeservice.repository.EmployeeRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains logic for department management, including
 * creation, updating, deletion. It's only accessible by the admin.
 * To avoid cascading issues, when the admin requests to delete
 * a department, the operation only succeeds if there are no
 * current employees in it.
 */
@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public DepartmentInfo createDepartment(CreateDepartmentRequest dto) {
        Department department = new Department();
        department.setName(dto.getName());
        department.setDescription(dto.getDescription());
        var saved = departmentRepository.save(department);
        int count = employeeRepository.countByDepartment(department);
        return new DepartmentInfo(saved.getId(), dto.getName(), dto.getDescription(), count);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<DepartmentInfo> getAllDepartments() {
        List<Department> depts = departmentRepository.findAll();
        List<DepartmentInfo> departmentInfos = new ArrayList<>();
        for (Department d : depts) {
            if (!d.isActive()) {
                departmentInfos.add(new DepartmentInfo(0, null, null, 0));
                continue;
            }
            int deptCount = employeeRepository.countByDepartment(d);
            departmentInfos.add(new DepartmentInfo(d.getId(), d.getName(), d.getDescription(), deptCount));
        }

        return departmentInfos;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void updateDepartmentDescription(String description, Integer id) {
        Department d = departmentRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Department not found"));
        if (d.isActive()) {
            d.setDescription(description);
            departmentRepository.save(d);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDepartment(Integer id) {
        Department d = departmentRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        if (employeeRepository.countByDepartment(d) > 0) {
            throw new IllegalArgumentException("Cannot delete department because it still " +
                "has member employees. Move them to another department and try again.");
        }

        d.setActive(false);
        departmentRepository.save(d);
    }


}
