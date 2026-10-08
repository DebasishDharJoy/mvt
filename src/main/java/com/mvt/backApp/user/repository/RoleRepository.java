package com.mvt.backApp.user.repository;

import com.mvt.backApp.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Spring translates this to: SELECT * FROM roles WHERE name = ?
    Optional<Role> findByName(String name);

}
