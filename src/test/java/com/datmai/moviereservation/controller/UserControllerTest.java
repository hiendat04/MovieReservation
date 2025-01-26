package com.datmai.moviereservation.controller;

import com.datmai.moviereservation.common.constant.Gender;
import com.datmai.moviereservation.common.constant.UserStatus;
import com.datmai.moviereservation.common.constant.UserType;
import com.datmai.moviereservation.common.dto.response.pagination.ResultPaginationDTO;
import com.datmai.moviereservation.common.dto.response.user.UserFetchRes;
import com.datmai.moviereservation.common.dto.response.user.UserResAbstract;
import com.datmai.moviereservation.domain.User;
import com.datmai.moviereservation.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private static User alice;
    private static User bob;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        alice = new User();
        alice.setId(1L);
        alice.setFirstName("Alice");
        alice.setLastName("May");
        alice.setType(UserType.USER);
        alice.setStatus(UserStatus.NONE);
        alice.setGender(Gender.FEMALE);
        alice.setUsername("alice");
        alice.setDateOfBirth(LocalDate.of(2003,4,4));

        bob = new User();
        bob.setId(2L);
        bob.setFirstName("Bob");
        bob.setLastName("Doe");
        bob.setType(UserType.USER);
        bob.setStatus(UserStatus.NONE);
        bob.setGender(Gender.MALE);
        bob.setUsername("bob");
        bob.setDateOfBirth(LocalDate.of(2003,5,4));
    }

    @Test
    @WithMockUser(roles = {"SYS_ADMIN", "ADMIN"})
    void testGetAllUsers() throws Exception {
        // Set up result return
        List<User> userResponses = List.of(alice, bob);

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setCurrentPage(1);
        meta.setPageSize(10);
        meta.setTotalElements(2);
        meta.setTotalPages(1);
        res.setMeta(meta);

        res.setResult(userResponses);

        // Simulate
        Pageable pageable = PageRequest.of(1, 10);
        Specification<User> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        when(this.userService.fetchAllUsers(spec, pageable)).thenReturn(res);

        mockMvc.perform(get("/api/v1/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
