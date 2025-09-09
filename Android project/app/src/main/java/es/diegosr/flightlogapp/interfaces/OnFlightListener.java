package es.diegosr.flightlogapp.interfaces;

import es.diegosr.flightlogapp.pojos.Flight;

public interface OnFlightListener {
    void flightAdded(Flight flightData);
    void flightUpdated(Flight flightData);
    void flightDeleted();
}
