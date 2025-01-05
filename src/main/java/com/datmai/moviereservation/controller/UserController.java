package com.datmai.moviereservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import com.datmai.moviereservation.domain.User;
import com.datmai.moviereservation.service.UserService;
import com.datmai.moviereservation.common.dto.response.pagination.ResultPaginationDTO;
import com.datmai.moviereservation.common.dto.response.user.ResCreateUserDTO;
import com.datmai.moviereservation.common.dto.response.user.ResUpdateUserDTO;
import com.datmai.moviereservation.common.dto.response.user.ResponseUserDTO;
import com.datmai.moviereservation.exception.ExistingException;
import com.datmai.moviereservation.common.format.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "User Controller")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create a new user", description = "Detail description...")
    @PostMapping("/users")
    @ApiMessage("Create user successfully")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User user) throws ExistingException {

        // Check if email exist
        if (this.userService.isEmailExist(user.getEmail())) {
            throw new ExistingException("Email " + user.getEmail() + " already exist");
        }
        User newUser = this.userService.createUser(user);
        ResCreateUserDTO res = this.userService.convertCreateUserDTO(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/users")
    @ApiMessage("Update user successfully")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws ExistingException {

        // Check if id exist
        if (!this.userService.isIdExist(user.getId())) {
            throw new ExistingException("Id " + user.getId() + " does not exist");
        }

        User updatedUser = this.userService.updateUser(user);
        ResUpdateUserDTO res = this.userService.convertUpdateUserDTO(updatedUser);

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users successfully")
    public ResponseEntity<ResultPaginationDTO> fetchAllUsers(
            @Filter Specification<User> spec,
            Pageable pageable) {
        ResultPaginationDTO res = this.userService.fetchAllUsers(spec, pageable);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch a user successfully")
    public ResponseEntity<ResponseUserDTO> fetchAUser(@PathVariable long id) throws ExistingException {

        // Check id user id exist
        if (!this.userService.isIdExist(id)) {
            throw new ExistingException("User with id " + id + " does not exist");
        }

        ResponseUserDTO user = this.userService.fetchUserById(id);
        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete user successfully")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) throws ExistingException {
        // Check id user id exist
        if (!this.userService.isIdExist(id)) {
            throw new ExistingException("User with id " + id + " does not exist");
        }

        // Delete user
        this.userService.deleteUser(id);
        return ResponseEntity.ok(null);
    }

}
