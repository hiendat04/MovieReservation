package com.datmai.moviereservation.repository;

import com.datmai.moviereservation.common.constant.RoleName;
import com.datmai.moviereservation.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
