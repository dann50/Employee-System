package com.dann50.employeeservice;

import com.dann50.employeeservice.dto.request.CreateEmployeeRequest;
import com.dann50.employeeservice.entity.Department;
import com.dann50.employeeservice.repository.DepartmentRepository;
import com.dann50.employeeservice.service.EmployeeService;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@SpringBootApplication
@EnableConfigurationProperties
public class EmployeeServiceApplication {

    @Autowired
    private ApplicationContext applicationContext;
    private DepartmentRepository departmentRepository;

    public static void main(String[] args) {
        SpringApplication.run(EmployeeServiceApplication.class, args);
    }

    @PostConstruct
    public void loadDb()  {
        departmentRepository = applicationContext.getBean(DepartmentRepository.class);

        List<String> names = List.of("hr", "engineering", "devops", "customer service");
        List<String> descs = List.of("This department is responsible for employee management",
            "This department is responsible for development and testing work",
            "This department is responsible for platform reliability and scaling",
            "This department is responsible for end user communication");

        for (int i = 0; i < names.size(); i++) {
            saveDept(names.get(i), descs.get(i));
        }

        var auth = new UsernamePasswordAuthenticationToken("admin123@email.com",null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")) // authorities
            );
        SecurityContextHolder.getContext().setAuthentication(auth);

        EmployeeService employeeService = applicationContext.getBean(EmployeeService.class);

        String[] roles = { "admin", "user", "manager" };
        Faker faker = new Faker();

        Random rand = new Random();
        for (int i = 0; i < 150; i++) {
            CreateEmployeeRequest request;
            int r = rand.nextInt(150);
            if (i < 3)
                request = getRequest(faker, Set.of(roles[i]), names.get(r % 4), roles[i] + "123@email.com");
            else {
                int ind = (r < 135) ? 1 : (r < 145) ? 2 : 0;
                request = getRequest(faker, Set.of(roles[ind]), names.get(r % 4), null);
            }
            employeeService.createEmployee(request);
        }

        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);

    }

    private CreateEmployeeRequest getRequest(Faker faker, Set<String> roles, String departmentName, String customEmail) {

        CreateEmployeeRequest req = new CreateEmployeeRequest();
        req.setFirstName(faker.name().firstName());
        req.setLastName(faker.name().lastName());
        req.setEmail((customEmail != null)? customEmail : faker.internet().emailAddress());
        req.setPhone(faker.phoneNumber().phoneNumber());
        req.setSalary(faker.numerify("1#####"));
        var bd = faker.date().between(new Date(1985, Calendar.JANUARY, 1), new Date(1999, Calendar.DECEMBER, 31));
        req.setBirthDate(LocalDate.ofInstant(bd.toInstant(), ZoneOffset.UTC));
        var hd = faker.date().between(new Date(2019, Calendar.AUGUST, 7), new Date(2024, Calendar.NOVEMBER, 10));
        req.setHireDate(LocalDate.ofInstant(hd.toInstant(), ZoneOffset.UTC));
        req.setDepartment(departmentName);
        req.setRoles(roles);

        return req;
    }

    private void saveDept(String name, String description) {
        Optional<Department> dept = departmentRepository.findByName(name);
        if (dept.isEmpty()) {
            Department d = new Department();
            d.setName(name);
            d.setDescription(description);
            d.setActive(true);
            departmentRepository.save(d);
        }
    }

}
