package com.datmai.moviereservation.common.dto.response.login;

import com.datmai.moviereservation.common.constant.RoleName;
import com.datmai.moviereservation.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ResponseLoginDTO {
    private UserLogin user;
    private String accessToken;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLogin {
        private long id;
        private String email;
        private String username;
        private RoleName role;
    }

    // Change the data is covered by the prefix "user" in response body
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserGetAccount {
        private UserLogin user;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInsideToken {
        private long id;
        private String email;
        private String username;
    }

}
