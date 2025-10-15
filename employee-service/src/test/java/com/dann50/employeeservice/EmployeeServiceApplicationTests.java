package com.dann50.employeeservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
class EmployeeServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DBSetup dbSetup;

    private boolean dbSetupDone = false;

    @BeforeEach
    void beforeAll() {
        if (!dbSetupDone) {
            dbSetupDone = true;
            dbSetup.loadDb();
        }
    }

    @Test
    @WithUserDetails("user123@email.com")
    void test_callViewEmployeeWithRegularUser() throws Exception {
        mockMvc.perform(get("/employees/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin123@email.com")
    void test_callViewEmployeeWithAdminUser() throws Exception {
        mockMvc.perform(get("/employees/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("user123@email.com")
    void test_callUpdateDepartmentWithRegularUser() throws Exception {
        mockMvc.perform(put("/employees/7/department").param("newDept", "hr"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin123@email.com")
    void test_callUpdateDepartmentWithAdminUser() throws Exception {
        mockMvc.perform(put("/employees/7/department").param("newDept", "hr"))
            .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("user123@email.com")
    void test_callUpdateSalaryWithRegularUser() throws Exception {
        mockMvc.perform(put("/employees/7/salary").param("newSalary", "200000"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin123@email.com")
    void test_callUpdateSalaryWithAdminUser() throws Exception {
        mockMvc.perform(put("/employees/7/salary").param("newSalary", "200000"))
            .andExpect(status().isOk());
    }

//    @Test
//    @WithUserDetails("admin123@email.com")
//    void test_callAdminEndpointWithAdminUser() throws Exception {
//        mockMvc.perform(get("/api/admin/user/daniel"))
//            .andExpect(status().isOk());
//    }

}
