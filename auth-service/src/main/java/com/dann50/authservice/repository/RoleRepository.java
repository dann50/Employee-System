package com.dann50.authservice.repository;

import com.dann50.authservice.entity.Role;
import com.dann50.authservice.util.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleName name);

    List<Role> findAllByNameIn(Collection<RoleName> names);
}
