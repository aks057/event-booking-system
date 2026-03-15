package com.cactro.eventbooking.repository;

import com.cactro.eventbooking.constant.Queries;
import com.cactro.eventbooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = Queries.FIND_BOOKINGS_BY_CUSTOMER_ID, nativeQuery = true)
    List<Booking> findByCustomerId(@Param("customerId") Long customerId);

    @Query(value = Queries.FIND_BOOKINGS_BY_ORGANIZER_ID, nativeQuery = true)
    List<Booking> findByOrganizerId(@Param("organizerId") Long organizerId);

    @Query(value = Queries.FIND_CUSTOMER_EMAILS_BY_EVENT_ID, nativeQuery = true)
    List<String> findCustomerEmailsByEventId(@Param("eventId") Long eventId);
}
