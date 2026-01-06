package com.example.bookmyshow.repository;

import com.example.bookmyshow.models.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
    Role findByRoleName(String roleName);
    Role findById(int id);
}
