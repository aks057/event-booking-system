package com.cactro.eventbooking.entity;

import com.cactro.eventbooking.constant.TableName;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableName.BOOKINGS)
public class Booking implements Serializable {

    @Serial
    private static final long serialVersionUID = 3L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private User customer;

    @Column(columnDefinition = "bigint", name = "customer_id", updatable = false, insertable = false)
    private Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    private Event event;

    @Column(columnDefinition = "bigint", name = "event_id", updatable = false, insertable = false)
    private Long eventId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double totalPrice;

    @Column(nullable = false, updatable = false)
    private LocalDateTime bookedAt;

    @PrePersist
    protected void onCreate() {
        bookedAt = LocalDateTime.now();
    }
}
