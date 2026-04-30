package org.example.service;

import org.example.model.Booking;
import org.example.model.Tour;

import java.util.List;
import java.util.stream.Collectors;

public class BookingService {

    public List<Booking> getBookingsForTour(Tour tour) {
        return tour.getBookings();
    }

    public List<Booking> getConfirmedBookings(List<Booking> bookings) {
        return bookings.stream()
                .filter(b -> Booking.BookingStatus.CONFIRMED.equals(b.getBookingStatus()))
                .collect(Collectors.toList());
    }

    public List<Booking> getPendingBookings(List<Booking> bookings) {
        return bookings.stream()
                .filter(b -> Booking.BookingStatus.PENDING.equals(b.getBookingStatus()))
                .collect(Collectors.toList());
    }

    public int getTotalGuestCount(List<Booking> bookings) {
        return bookings.stream()
                .mapToInt(b -> b.getGuestCount() != null ? b.getGuestCount() : 0)
                .sum();
    }
}