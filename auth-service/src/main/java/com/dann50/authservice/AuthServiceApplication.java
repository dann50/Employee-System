package com.dann50.authservice;

import com.dann50.authservice.entity.Role;
import com.dann50.authservice.repository.RoleRepository;
import com.dann50.authservice.util.RoleName;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

@SpringBootApplication
public class AuthServiceApplication {

	@Autowired
	private ApplicationContext applicationContext;

	private RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

	@PostConstruct
	public void loadDb()  {
		roleRepository = applicationContext.getBean(RoleRepository.class);

		for (var roleName : RoleName.values()) {
			saveRole(roleName);
		}
	}

	private void saveRole(RoleName name) {
		Optional<Role> role = roleRepository.findByName(name);
		if(role.isEmpty()){
			Role newRole = new Role();
			newRole.setName(name);
			roleRepository.save(newRole);
		}
	}
}
