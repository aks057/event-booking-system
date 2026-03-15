package com.cactro.eventbooking.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    @Async("notificationExecutor")
    @EventListener
    public void handleBookingConfirmed(BookingConfirmedEvent event) {
        System.out.println("========================================");
        System.out.println("[BACKGROUND TASK - BOOKING CONFIRMATION]");
        System.out.println("========================================");
        System.out.printf("[EMAIL] Sending booking confirmation to %s (%s)%n",
                event.getCustomerName(), event.getCustomerEmail());
        System.out.printf("[EMAIL] You have successfully booked %d ticket(s) for \"%s\".%n",
                event.getQuantity(), event.getEventTitle());
        System.out.printf("[EMAIL] Total amount: $%.2f%n", event.getTotalPrice());
        System.out.println("[EMAIL] Confirmation email sent successfully!");
        System.out.println("========================================");
    }

    @Async("notificationExecutor")
    @EventListener
    public void handleEventUpdated(EventUpdatedEvent event) {
        System.out.println("==========================================");
        System.out.println("[BACKGROUND TASK - EVENT UPDATE NOTIFICATION]");
        System.out.println("==========================================");
        System.out.printf("[NOTIFICATION] Event \"%s\" (ID: %d) has been updated.%n",
                event.getEventTitle(), event.getEventId());
        System.out.printf("[NOTIFICATION] Notifying %d booked customer(s)...%n",
                event.getBookedCustomerEmails().size());

        for (String email : event.getBookedCustomerEmails()) {
            System.out.printf("[EMAIL] Notification sent to %s: Event \"%s\" has been updated. Please check the latest details.%n",
                    email, event.getEventTitle());
        }

        System.out.println("[NOTIFICATION] All customers notified successfully!");
        System.out.println("==========================================");
    }
}
