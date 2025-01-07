package com.datmai.moviereservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import com.datmai.moviereservation.domain.User;
import com.datmai.moviereservation.service.UserService;
import com.datmai.moviereservation.common.dto.response.pagination.ResultPaginationDTO;
import com.datmai.moviereservation.common.dto.response.user.CreateUserRes;
import com.datmai.moviereservation.common.dto.response.user.UpdateUserRes;
import com.datmai.moviereservation.common.dto.response.user.UserFetchRes;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Controller")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create a new user", description = "Detail description...")
    @PostMapping
    @ApiMessage("Create user successfully")
    public ResponseEntity<CreateUserRes> createUser(@Valid @RequestBody User user) throws ExistingException {
        // Validate user create request
        this.userService.validateUserCreateReq(user);

        // Create new user
        CreateUserRes res = this.userService.createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping
    @ApiMessage("Update user successfully")
    public ResponseEntity<UpdateUserRes> updateUser(@RequestBody User user) throws ExistingException {

        // Check if id exist
        if (!this.userService.isIdExist(user.getId())) {
            throw new ExistingException(
                    List.of("Id " + user.getId() + " does not exist"));
        }

        User updatedUser = this.userService.updateUser(user);
        UpdateUserRes res = this.userService.convertUpdateUserDTO(updatedUser);

        return ResponseEntity.accepted().body(res);
    }

    @GetMapping
    @ApiMessage("Fetch all users successfully")
    public ResponseEntity<ResultPaginationDTO> fetchAllUsers(
            @Filter Specification<User> spec,
            Pageable pageable) {
        ResultPaginationDTO res = this.userService.fetchAllUsers(spec, pageable);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch a user successfully")
    public ResponseEntity<UserFetchRes> fetchAUser(@PathVariable @Min(1) long id) throws ExistingException {

        // Check id user id exist
        if (!this.userService.isIdExist(id)) {
            throw new ExistingException(
                    List.of("User with id " + id + " does not exist"));
        }

        UserFetchRes user = this.userService.fetchUserById(id);
        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete user successfully")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) throws ExistingException {
        // Check id user id exist
        if (!this.userService.isIdExist(id)) {
            throw new ExistingException(List.of("User with id " + id + " does not exist"));
        }

        // Delete user
        this.userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.RESET_CONTENT).build();
    }

    // TODO: Write change/ forgot password API...

}
