package com.datmai.moviereservation.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.datmai.moviereservation.common.constant.RoleName;
import com.datmai.moviereservation.common.constant.UserStatus;
import com.datmai.moviereservation.domain.Role;
import com.datmai.moviereservation.exception.ExistingException;
import com.datmai.moviereservation.exception.ResourceNotFoundException;
import com.datmai.moviereservation.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.datmai.moviereservation.domain.User;
import com.datmai.moviereservation.repository.UserRepository;
import com.datmai.moviereservation.common.dto.response.pagination.ResultPaginationDTO;
import com.datmai.moviereservation.common.dto.response.user.CreateUserRes;
import com.datmai.moviereservation.common.dto.response.user.UpdateUserRes;
import com.datmai.moviereservation.common.dto.response.user.UserFetchRes;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RoleRepository roleRepository;

    @Transactional(rollbackFor = Exception.class)
    public CreateUserRes createUser(User user) {
        log.info("Creating user {}", user);
        Role userRole = this.roleRepository.findByName(RoleName.USER).orElse(null);

        // Save user and their address
        log.info("Hashed password {}", user.getPassword());

        user.setRole(userRole);
        User newUser = this.userRepository.save(user);
        log.info("Created user {}", newUser);

        // Send email confirm, if fails, user need to confirm again (transaction rollback)
        try {
            this.emailService.emailVerification(newUser.getEmail(), newUser.getFirstName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this.convertCreateUserDTO(newUser);
    }

    public void validateUserCreateReq(User user) {
        List<String> errors = new ArrayList<>();

        if (userRepository.existsByEmail(user.getEmail())) {
            errors.add("Email already exists");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            errors.add("Username already exists");
        }
        if (userRepository.existsByPhone(user.getPhone())) {
            errors.add("Phone already exists");
        }

        if (!errors.isEmpty()) {
            throw new ExistingException(errors);
        }
    }


    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public CreateUserRes convertCreateUserDTO(User user) {
        return CreateUserRes.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .phone(user.getPhone())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .type(user.getType())
                .status(user.getStatus())
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .createdBy(user.getCreatedBy()).build();
    }

    public boolean isIdExist(long id) {
        return this.userRepository.existsById(id);
    }

    public User updateUser(User user) {
        Optional<User> userOptional = this.userRepository.findById(user.getId());
        User currentUser = userOptional.orElse(null);
        if (currentUser == null) return null;
        else {
            currentUser.setFirstName(user.getFirstName());
            currentUser.setLastName(user.getLastName());
            currentUser.setUsername(user.getUsername());
            currentUser.setPhone(user.getPhone());
            currentUser.setGender(user.getGender());
            currentUser.setDateOfBirth(user.getDateOfBirth());
            currentUser.setType(user.getType());
            currentUser.setStatus(user.getStatus());
            currentUser.setAddress(user.getAddress());
            // Save updated user
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public UpdateUserRes convertUpdateUserDTO(User user) {
        return UpdateUserRes.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .phone(user.getPhone())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .type(user.getType())
                .status(user.getStatus())
                .address(user.getAddress())
                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy()).build();
    }

    public UserFetchRes convertUserFetchDTO(User user) {
        return UserFetchRes.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .phone(user.getPhone())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .type(user.getType())
                .status(user.getStatus())
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .createdBy(user.getCreatedBy())
                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy()).build();
    }

    public ResultPaginationDTO fetchAllUsers(Specification<User> specification, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(specification, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setCurrentPage(pageUser.getNumber() + 1);
        meta.setPageSize(pageUser.getSize());
        meta.setTotalElements(pageUser.getTotalElements());
        meta.setTotalPages(pageUser.getTotalPages());
        res.setMeta(meta);
        List<UserFetchRes> users = pageUser.getContent()
                .stream()
                .map(this::convertUserFetchDTO)
                .toList();
        res.setResult(users);
        return res;
    }

    public UserFetchRes fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        User currentUser = userOptional.orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " does not exist"));
        return this.convertUserFetchDTO(currentUser);
    }

    public void deleteUser(long id) {
        log.info("Deleting user with {} ", id);

        // Get user by id
        User user = this.userRepository.findById(id).orElse(null);
        assert user != null;
        user.setStatus(UserStatus.INACTIVE);

        this.userRepository.save(user);
        log.info("User with id {} has been deleted.", id);
    }

    public User fetchUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public void updateUserToken(String token, String email) {
        // Fetch current user
        User user = this.fetchUserByEmail(email);
        if (user != null) {
            user.setRefreshToken(token);
            // Update token
            this.userRepository.save(user);
        }
    }

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }
}
