package com.datmai.moviereservation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permissions")
public class Permission extends AbstractEntity<Long> {

    @NotBlank(message = "Name cannot be empty!")
    private String name;

    @NotBlank(message = "API Path cannot be empty!")
    private String apiPath;

    @NotBlank(message = "Method cannot be empty!")
    private String method;

    @NotBlank(message = "Module cannot be empty!")
    private String module;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Role> roles;
}