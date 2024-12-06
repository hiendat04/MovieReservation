package com.datmai.moviereservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.datmai.moviereservation.domain.Schedule;
import com.datmai.moviereservation.domain.Screen;
import com.datmai.moviereservation.util.constant.ScreenName;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long>, JpaSpecificationExecutor<Screen> {
    boolean existsByName(ScreenName name);

}
