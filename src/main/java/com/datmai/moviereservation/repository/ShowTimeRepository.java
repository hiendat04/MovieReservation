package com.datmai.moviereservation.repository;

import java.time.LocalTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.datmai.moviereservation.domain.Schedule;
import com.datmai.moviereservation.domain.ShowTime;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, Long>, JpaSpecificationExecutor<ShowTime> {
    boolean existsByTimeAndSchedule(LocalTime time, Schedule schedule);

    // Find the nearest lower bound show time to validate new show time
    @Query("SELECT st FROM ShowTime st WHERE st.time <= :newTime AND st.schedule.id = :scheduleId " +
            "AND (:showTimeId IS NULL OR st.id <> :showTimeId) " +
            "ORDER BY st.time DESC LIMIT 1")
    Optional<ShowTime> findNearestLowerBound(@Param("newTime") LocalTime newTime,
            @Param("scheduleId") Long scheduleId,
            @Param("showTimeId") Long showTimeId);

    // Find the nearest upper bound show time to validate new show time
    @Query("SELECT st FROM ShowTime st WHERE st.time >= :newTime AND st.schedule.id = :scheduleId " +
            "AND (:showTimeId IS NULL OR st.id <> :showTimeId) " +
            "ORDER BY st.time ASC LIMIT 1")
    Optional<ShowTime> findNearestUpperBound(@Param("newTime") LocalTime newTime,
            @Param("scheduleId") Long scheduleId,
            @Param("showTimeId") Long showTimeId);

}
