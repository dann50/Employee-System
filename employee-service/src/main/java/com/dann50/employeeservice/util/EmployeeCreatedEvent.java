package com.dann50.employeeservice.util;

import java.util.Set;

/**
 * The message that is sent (to Kafka) when an
 * employee gets registered for the first time. This
 * includes the id, email, default password and the
 * employee's roles.
 */
public class EmployeeCreatedEvent {

    private Long employeeId;
    private String email;
    private String defaultPassword;
    private Set<String> roles;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
