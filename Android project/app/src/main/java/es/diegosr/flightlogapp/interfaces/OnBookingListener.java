package es.diegosr.flightlogapp.interfaces;

import es.diegosr.flightlogapp.pojos.Booking;

public interface OnBookingListener {
    void bookingAdded(Booking bookingData);
    void bookingUpdated(Booking bookingData);
    void bookingDeleted();
}
