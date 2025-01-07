package com.datmai.moviereservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.datmai.moviereservation.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    User findByEmail(String email);

    User findByRefreshTokenAndEmail(String refreshToken, String email);
}
