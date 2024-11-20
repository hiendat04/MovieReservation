package com.datmai.moviereservation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.datmai.moviereservation.domain.User;
import com.datmai.moviereservation.repository.UserRepository;
import com.datmai.moviereservation.util.dto.response.pagination.ResultPaginationDTO;
import com.datmai.moviereservation.util.dto.response.user.ResCreateUserDTO;
import com.datmai.moviereservation.util.dto.response.user.ResUpdateUserDTO;
import com.datmai.moviereservation.util.dto.response.user.ResponseUserDTO;

import io.micrometer.core.ipc.http.HttpSender.Response;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return this.userRepository.save(user);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        if (user != null) {
            res.setAddress(user.getAddress());
            res.setAge(user.getAge());
            res.setCreatedAt(user.getCreatedAt());
            res.setCreatedBy(user.getCreatedBy());
            res.setEmail(user.getEmail());
            res.setGender(user.getGender());
            res.setId(user.getId());
        }
        return res;
    }

    public boolean isIdExist(long id) {
        return this.userRepository.existsById(id);
    }

    public User updateUser(User user) {
        Optional<User> userOptional = this.userRepository.findById(user.getId());
        User currentUser = userOptional.get();
        if (currentUser != null) {
            currentUser.setAddress(user.getAddress());
            currentUser.setAge(user.getAge());
            currentUser.setGender(user.getGender());
            currentUser.setName(user.getName());

            // Save updated user
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public ResUpdateUserDTO convertUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        if (user != null) {
            res.setAddress(user.getAddress());
            res.setAge(user.getAge());
            res.setGender(user.getGender());
            res.setId(user.getId());
            res.setName(user.getName());
            res.setUpdatedAt(user.getUpdatedAt());
            res.setUpdatedBy(user.getUpdatedBy());
        }
        return res;
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
        System.out.println(">>>>> content" + pageUser.getContent());
        List<ResponseUserDTO> users = pageUser.getContent().stream().map(user -> {
            ResponseUserDTO resUser = new ResponseUserDTO();
            resUser.setAddress(user.getAddress());
            resUser.setAge(user.getAge());
            resUser.setCreatedAt(user.getCreatedAt());
            resUser.setCreatedBy(user.getCreatedBy());
            resUser.setGender(user.getGender());
            resUser.setId(user.getId());
            resUser.setName(user.getName());
            resUser.setUpdatedAt(user.getUpdatedAt());
            resUser.setUpdatedBy(user.getUpdatedBy());

            return resUser;
        }).toList();
        res.setResult(users);
        return res;
    }

    public ResponseUserDTO fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        User currentUser = userOptional.get();
        ResponseUserDTO resUser = new ResponseUserDTO();
        if (currentUser != null) {
            resUser.setAddress(currentUser.getAddress());
            resUser.setAge(currentUser.getAge());
            resUser.setCreatedAt(currentUser.getCreatedAt());
            resUser.setCreatedBy(currentUser.getCreatedBy());
            resUser.setGender(currentUser.getGender());
            resUser.setId(currentUser.getId());
            resUser.setName(currentUser.getName());
            resUser.setUpdatedAt(currentUser.getUpdatedAt());
            resUser.setUpdatedBy(currentUser.getUpdatedBy());
        }
        return resUser;
    }

    public void deleteUser(long id){
        this.userRepository.deleteById(id);
    }

    public User fetchUserByEmail(String email){
        return this.userRepository.findByEmail(email);
    }

    public void updateUserToken(String token, String email){
        // Fetch current user
        User user = this.fetchUserByEmail(email);
        if(user != null){
            user.setRefreshToken(token);
            // Update token
            user = this.createUser(user);
        }
    }

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email){
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }
}
