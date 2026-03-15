package com.cactro.eventbooking.constant;

public final class Queries {

    private Queries() {}

    public static final String FIND_EVENTS_WITH_FILTERS = """
            SELECT e.* FROM events e
            WHERE (:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(e.venue) LIKE LOWER(CONCAT('%', :search, '%')))
            AND (:dateFrom IS NULL OR e.date_time >= :dateFrom)
            """;

    public static final String COUNT_EVENTS_WITH_FILTERS = """
            SELECT COUNT(*) FROM events e
            WHERE (:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(e.venue) LIKE LOWER(CONCAT('%', :search, '%')))
            AND (:dateFrom IS NULL OR e.date_time >= :dateFrom)
            """;

    public static final String FIND_EVENTS_BY_ORGANIZER_ID = """
            SELECT e.* FROM events e
            WHERE e.organizer_id = :organizerId
            """;

    public static final String FIND_BOOKINGS_BY_CUSTOMER_ID = """
            SELECT b.* FROM bookings b
            WHERE b.customer_id = :customerId
            """;

    public static final String FIND_BOOKINGS_BY_ORGANIZER_ID = """
            SELECT b.* FROM bookings b
            INNER JOIN events e ON b.event_id = e.id
            WHERE e.organizer_id = :organizerId
            """;

    public static final String FIND_CUSTOMER_EMAILS_BY_EVENT_ID = """
            SELECT u.email FROM bookings b
            INNER JOIN users u ON b.customer_id = u.id
            WHERE b.event_id = :eventId
            """;
}
