package com.datmai.moviereservation.common.seeder;

import com.datmai.moviereservation.common.constant.RoleName;
import com.datmai.moviereservation.domain.Role;
import com.datmai.moviereservation.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadRoles();
    }

    private void loadRoles() {
        RoleName[] roleNames = new RoleName[] { RoleName.USER, RoleName.ADMIN, RoleName.SYS_ADMIN };
        Map<RoleName, String> roleDescriptionMap = Map.of(
                RoleName.USER, "Default user role",
                RoleName.ADMIN, "Administrator role",
                RoleName.SYS_ADMIN, "System Administrator role"
        );

        Arrays.stream(roleNames).forEach((roleName) -> {
            Optional<Role> optionalRole = roleRepository.findByName(roleName);

            optionalRole.ifPresentOrElse(System.out::println, () -> {
                Role roleToCreate = new Role();

                roleToCreate.setName(roleName);
                roleToCreate.setDescription(roleDescriptionMap.get(roleName));

                this.roleRepository.save(roleToCreate);
            });
        });
    }
}