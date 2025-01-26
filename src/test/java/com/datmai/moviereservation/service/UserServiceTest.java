package com.datmai.moviereservation.service;

import com.datmai.moviereservation.common.constant.Gender;
import com.datmai.moviereservation.common.constant.UserStatus;
import com.datmai.moviereservation.common.constant.UserType;
import com.datmai.moviereservation.common.dto.response.pagination.ResultPaginationDTO;
import com.datmai.moviereservation.common.dto.response.user.CreateUserRes;
import com.datmai.moviereservation.common.dto.response.user.UserFetchRes;
import com.datmai.moviereservation.domain.User;
import com.datmai.moviereservation.exception.ResourceNotFoundException;
import com.datmai.moviereservation.repository.RoleRepository;
import com.datmai.moviereservation.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;

    private @Mock UserRepository userRepository;
    private @Mock EmailService emailService;
    private @Mock RoleRepository roleRepository;

    private static User alice;
    private static User bob;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        alice = new User();
        alice.setId(1L);
        alice.setFirstName("Alice");
        alice.setLastName("May");
        alice.setEmail("alice@gmail.com");
        alice.setPassword("alice");
        alice.setType(UserType.USER);
        alice.setStatus(UserStatus.NONE);
        alice.setGender(Gender.FEMALE);
        alice.setUsername("alice");
        alice.setDateOfBirth(LocalDate.of(2003,4,4));

        bob = new User();
        bob.setId(2L);
        bob.setFirstName("Bob");
        bob.setLastName("Doe");
        bob.setEmail("bob@gmail.com");
        bob.setPassword("bob");
        bob.setType(UserType.USER);
        bob.setStatus(UserStatus.NONE);
        bob.setGender(Gender.MALE);
        bob.setUsername("bob");
        bob.setDateOfBirth(LocalDate.of(2003,5,4));
    }

    @BeforeEach
    void setUp() {
        // Initialize User Service
        this.userService = new UserService(userRepository, emailService, roleRepository);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCreateUser_Success() {
        when(this.userRepository.save(any(User.class))).thenReturn(alice);

        // Mock data
        alice = new User();
        alice.setId(1L);
        alice.setFirstName("Alice");
        alice.setLastName("May");
        alice.setEmail("alice@gmail.com");
        alice.setPassword("alice");
        alice.setType(UserType.USER);
        alice.setStatus(UserStatus.NONE);
        alice.setGender(Gender.FEMALE);
        alice.setUsername("alice");
        alice.setDateOfBirth(LocalDate.of(2003,4,4));

        CreateUserRes user = this.userService.createUser(alice);

        assertNotNull(user);
        assertEquals(1L, user.getId());
    }

    @Test
    void validateUserCreateReq() {
    }

    @Test
    void isEmailExist() {
    }

    @Test
    void convertCreateUserDTO() {
    }

    @Test
    void isIdExist() {
    }

    @Test
    void testUpdateUser_Success() {
        User updateResult = new User();
        updateResult.setId(2L);
        updateResult.setFirstName("Jame");
        updateResult.setLastName("Man");

        when(this.userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(this.userRepository.save(any(User.class))).thenReturn(updateResult);

        User updateRequest = new User();
        updateRequest.setId(2L);
        updateRequest.setFirstName("Jame");
        updateRequest.setLastName("Man");

        User updatedUser = this.userService.updateUser(updateRequest);
        assertNotNull(updatedUser);
        assertEquals("Jame", updatedUser.getFirstName());
        assertEquals("Man", updatedUser.getLastName());
    }

    @Test
    void convertUpdateUserDTO() {
    }

    @Test
    void convertUserFetchDTO() {
    }

    @Test
    void testFetchAllUsers_Success() {
        // Simulate method
        Page<User> userPage = new PageImpl<>(List.of(alice, bob));
        Pageable pageable = PageRequest.of(0, 10);

        // Mock a Specification
        @SuppressWarnings("unchecked")
        Specification<User> specification = mock(Specification.class);

        // Resolve ambiguity: Cast to JpaSpecificationExecutor
        JpaSpecificationExecutor<User> specExecutor = (JpaSpecificationExecutor<User>) userRepository;

        // Mock the findAll method with Specification and Pageable
        when(specExecutor.findAll(eq(specification), eq(pageable))).thenReturn(userPage);

        // Call the method being tested
        ResultPaginationDTO resultPaginationDTO = this.userService.fetchAllUsers(specification, pageable);

        // Assert the results
        assertNotNull(resultPaginationDTO);
        assertNotNull(resultPaginationDTO.getMeta());
        assertEquals(2, resultPaginationDTO.getMeta().getTotalElements());
        assertEquals(1, resultPaginationDTO.getMeta().getTotalPages());
    }

    @Test
    void testFetchUserById_Success() {
        // Simulate method
        when(this.userRepository.findById(1L)).thenReturn(Optional.of(alice));

        UserFetchRes userFetchRes = this.userService.fetchUserById(1L);
        assertNotNull(userFetchRes);
        assertEquals(1L, userFetchRes.getId());
    }

    @Test
    void testFetchUserById_Failure() {
        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> this.userService.fetchUserById(1L));
        assertNotNull(resourceNotFoundException);
        assertEquals("User with id " + 1 + " does not exist", resourceNotFoundException.getMessage());

    }

    @Test
    void testDeleteUser_Success() {
        Long userId = 1L;

        when(this.userRepository.findById(userId)).thenReturn(Optional.of(alice));
        this.userService.deleteUser(userId);

        assertEquals(UserStatus.INACTIVE, alice.getStatus());

        // Make sure this function will run only 1 time
        verify(this.userRepository, times(1)).save(alice);
    }

    @Test
    void fetchUserByEmail() {
    }

    @Test
    void updateUserToken() {
    }

    @Test
    void getUserByRefreshTokenAndEmail() {
    }
}