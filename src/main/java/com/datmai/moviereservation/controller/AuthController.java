package com.datmai.moviereservation.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datmai.moviereservation.domain.User;
import com.datmai.moviereservation.service.UserService;
import com.datmai.moviereservation.util.dto.request.RequestLoginDTO;
import com.datmai.moviereservation.util.dto.response.login.ResponseLoginDTO;
import com.datmai.moviereservation.util.dto.response.user.ResCreateUserDTO;
import com.datmai.moviereservation.util.error.ExistingException;
import com.datmai.moviereservation.util.format.ApiMessage;
import com.datmai.moviereservation.util.security.SecurityUtil;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${hiendat.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil,
            UserService userService,
            PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/login")
    @ApiMessage("User login")
    public ResponseEntity<ResponseLoginDTO> login(@Valid @RequestBody RequestLoginDTO loginDTO) {
        
        // Send username and password to Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(), loginDTO.getPassword());

        // Authenticate User => Have to write loadUserByUsername function (in
        // UserDetailsCustom.java)
        Authentication authentication = this.authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Save user information in Security Context Holder for further usage
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseLoginDTO responseLoginDTO = new ResponseLoginDTO();

        User currentUser = this.userService.fetchUserByEmail(loginDTO.getEmail());

        if (currentUser != null) {
            ResponseLoginDTO.UserLogin user = new ResponseLoginDTO.UserLogin(
                    currentUser.getId(),
                    currentUser.getEmail(),
                    currentUser.getName());
            responseLoginDTO.setUser(user);
        }

        // Create access token
        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), responseLoginDTO);
        responseLoginDTO.setAccessToken(accessToken);

        // Create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getEmail(), responseLoginDTO);

        // Update user with refresh token
        this.userService.updateUserToken(refreshToken, loginDTO.getEmail());

        // Set cookie
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/") // to allow all APIs in the project to use cookie
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(responseLoginDTO);
    }

    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResponseLoginDTO.UserGetAccount> fetchAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        User currentUser = this.userService.fetchUserByEmail(email);
        ResponseLoginDTO.UserLogin user = new ResponseLoginDTO.UserLogin();
        ResponseLoginDTO.UserGetAccount userGetAccount = new ResponseLoginDTO.UserGetAccount();

        if (currentUser != null) {
            user.setId(currentUser.getId());
            user.setEmail(email);
            user.setName(currentUser.getName());

            userGetAccount.setUser(user);
        }
        return ResponseEntity.ok().body(userGetAccount);
    }

    // The refresh token is saved as long as user login
    @GetMapping("/auth/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResponseLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "unavailable") String refresh_token // Server get
                                                                                                    // refresh token
                                                                                                    // from cookie
    ) throws ExistingException {
        if (refresh_token.equals("unavailable")) {
            throw new ExistingException("Refresh token is unavailable in cookie");
        }

        // Server check if token is valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);

        // Get the object of the token, in this case, it is the email of the user
        String email = decodedToken.getSubject();

        // Check user by token + email (to make the process more secured)
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {
            throw new ExistingException("Refresh token is invalid! ");
        }

        // Issue new token/ set refresh token as cookie
        ResponseLoginDTO responseLoginDTO = new ResponseLoginDTO();
        ResponseLoginDTO.UserLogin user = new ResponseLoginDTO.UserLogin(
                currentUser.getId(),
                currentUser.getEmail(),
                currentUser.getName());

        responseLoginDTO.setUser(user);

        // Create access token
        String accessToken = this.securityUtil.createAccessToken(email, responseLoginDTO);
        responseLoginDTO.setAccessToken(accessToken);

        // Create refresh token
        String newRefreshToken = this.securityUtil.createRefreshToken(email, responseLoginDTO);

        // Update user with refresh token
        this.userService.updateUserToken(newRefreshToken, email);

        // Set cookie
        ResponseCookie cookie = ResponseCookie.from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/") // to allow all APIs in the project to use cookie
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(responseLoginDTO);

    }

    @PostMapping("/auth/logout")
    @ApiMessage("User logout")
    public ResponseEntity<Void> logout() throws ExistingException {
        // Get the email of current user
        String email = SecurityUtil.getCurrentUserLogin().get();

        if (email.equals(null)) {
            throw new ExistingException("Access token is invalid");
        }

        // Set refresh cookie = null
        this.userService.updateUserToken(null, email);

        // Delete refresh token in cookie
        ResponseCookie deleteCookie = ResponseCookie
                .from("refresh_token", null)
                .maxAge(0)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    @PostMapping("/auth/register")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User user) throws ExistingException {

        // Check if email exist
        if (this.userService.isEmailExist(user.getEmail())) {
            throw new ExistingException("Email " + user.getEmail() + " is existing!. Please choose another email!");
        }

        // Hash password
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);

        // Save user
        User registerUser = this.userService.createUser(user);

        // Convert to DTO (not to show password)
        ResCreateUserDTO res = this.userService.convertCreateUserDTO(registerUser);
        return ResponseEntity.ok().body(res);
    }

}
