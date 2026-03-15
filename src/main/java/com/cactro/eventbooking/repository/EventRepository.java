package com.cactro.eventbooking.repository;

import com.cactro.eventbooking.constant.Queries;
import com.cactro.eventbooking.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = Queries.FIND_EVENTS_WITH_FILTERS, countQuery = Queries.COUNT_EVENTS_WITH_FILTERS, nativeQuery = true)
    Page<Event> findEventsWithFilters(
            @Param("search") String search,
            @Param("dateFrom") LocalDateTime dateFrom,
            Pageable pageable);

    @Query(value = Queries.FIND_EVENTS_BY_ORGANIZER_ID, nativeQuery = true)
    Page<Event> findByOrganizerId(@Param("organizerId") Long organizerId, Pageable pageable);
}
