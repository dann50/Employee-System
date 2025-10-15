package com.dann50.employeeservice.service;

import com.dann50.employeeservice.dto.request.CreateEmployeeRequest;
import com.dann50.employeeservice.dto.response.CreateEmployeeResponse;
import com.dann50.employeeservice.dto.response.DepartmentInfo;
import com.dann50.employeeservice.dto.response.EmployeeResponse;
import com.dann50.employeeservice.entity.Department;
import com.dann50.employeeservice.entity.Employee;
import com.dann50.employeeservice.exception.EmployeeCreationException;
import com.dann50.employeeservice.repository.DepartmentRepository;
import com.dann50.employeeservice.repository.EmployeeRepository;
import com.dann50.employeeservice.util.EmployeeCreatedEvent;
import com.dann50.employeeservice.util.RoleName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This class contains logic for employee management, including
 * creation, updating, deletion. Some are only accessible by the
 * admin.
 */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeEventProducer eventProducer;

    @Value("${application.security.default-password}")
    private String defaultPassword;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository,
                           EmployeeEventProducer eventProducer) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.eventProducer = eventProducer;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CreateEmployeeResponse createEmployee(CreateEmployeeRequest request) {
        List<RoleName> roleNames = new ArrayList<>();

        var sanitized = request.getDepartment().trim().toLowerCase();
        if (sanitized.endsWith(" department")) {
            sanitized = sanitized.replace(" department", "");
        }

        try {
            for (var role : request.getRoles()) {
                roleNames.add(RoleName.valueOf("ROLE_" + role.toUpperCase()));
            }

            if (roleNames.isEmpty())
                throw new EmployeeCreationException("Roles of the employee cannot be empty");

            return saveEmployee(request, sanitized);
        } catch (IllegalArgumentException e) {
            throw new EmployeeCreationException("Invalid role name");
        }
    }

    private CreateEmployeeResponse saveEmployee(CreateEmployeeRequest request, String deptName) {

        if (employeeRepository.findByEmail(request.getEmail()).isPresent())
            throw new EmployeeCreationException("Email already exists");

        Department department = departmentRepository
            .findByName(deptName)
            .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        Employee e = new Employee();
        e.setFirstName(request.getFirstName());
        e.setLastName(request.getLastName());
        e.setEmail(request.getEmail());
        e.setPhone(request.getPhone());
        e.setActive(true);
        e.setSalary(request.getSalary());
        e.setBirthDate(request.getBirthDate());
        e.setHireDate(request.getHireDate());
        e.setDepartment(department);

        var saved = employeeRepository.save(e);

        var event = new EmployeeCreatedEvent();
        event.setEmployeeId(saved.getId());
        event.setEmail(request.getEmail());
        event.setRoles(request.getRoles());
        event.setDefaultPassword(defaultPassword);

        eventProducer.publishEmployeeCreatedEvent(event);

        var response = new CreateEmployeeResponse();
        response.setMessage("The e has been registered successfully. The e should " +
            "login using the following details so they can change their password:\n" +
            "E-mail: " + request.getEmail() + "\nPassword: " + defaultPassword);

        return response;
    }

    public EmployeeResponse getEmployeeDetails(Long id, Authentication authentication) {
        String email = authentication.getName();
        Employee viewer = employeeRepository
            .findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        Employee emp = employeeRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        if (
            viewer.getId().equals(emp.getId()) ||
            authentication.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.name()))
        ) {
            return mapToEmployeeResponse(Collections.singletonList(emp)).getFirst();
        } else if (
            authentication.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.name())) &&
            viewer.getDepartment().getId().equals(emp.getDepartment().getId())
        ) {
            return mapToEmployeeResponse(Collections.singletonList(emp)).getFirst();
        }

        throw new AccessDeniedException("You are not authorized to view this employee");
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void updateEmployeeSalary(Long id, String newSalary) {
        Employee emp = employeeRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        emp.setSalary(newSalary);
        employeeRepository.save(emp);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void updateEmployeeDepartment(Long id, String deptName) {
        Employee emp = employeeRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        Department department = departmentRepository
            .findByName(deptName)
            .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        if (!department.isActive())
            throw new IllegalArgumentException("Cannot update the employee department");

        emp.setDepartment(department);
        employeeRepository.save(emp);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<EmployeeResponse> getAllEmployeesInMyDepartment(Authentication authentication) {
        String email = authentication.getName();
        Employee viewer = employeeRepository
            .findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        List<Employee> emps = viewer.getDepartment().getEmployees();
        return mapToEmployeeResponse(emps);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEmployee(Long id) {
        Employee emp = employeeRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        emp.setActive(false);
        employeeRepository.save(emp);
        eventProducer.publishEmployeeDeletedEvent(emp.getEmail() + "::" + emp.getId());
    }

    private List<EmployeeResponse> mapToEmployeeResponse(List<Employee> employees) {
        List<EmployeeResponse> responses = new ArrayList<>();
        HashMap<Integer, Integer> deptCountMap = new HashMap<>();
        for (Employee emp : employees) {
            if (!emp.isActive()) {
                responses.add(new EmployeeResponse());
                continue;
            }
            var e = new EmployeeResponse();
            emp.setId(emp.getId());
            e.setName(emp.getFirstName() + " " + emp.getLastName());
            e.setEmail(emp.getEmail());
            e.setPhone(emp.getPhone());
            e.setSalary(emp.getSalary());
            e.setBirthDate(emp.getBirthDate());
            e.setHireDate(emp.getHireDate());

            int deptCount = deptCountMap.computeIfAbsent(
                emp.getDepartment().getId(),
                k -> employeeRepository.countByDepartment(emp.getDepartment()));

            DepartmentInfo dept = new DepartmentInfo(
                emp.getDepartment().getId(),
                emp.getDepartment().getName(),
                emp.getDepartment().getDescription(),
                deptCount
            );

            e.setDepartment(dept);
            responses.add(e);
        }

        return responses;
    }
}
