package com.dann50.employeeservice.repository;

import com.dann50.employeeservice.entity.Department;
import com.dann50.employeeservice.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    int countByDepartment(Department department);
}
