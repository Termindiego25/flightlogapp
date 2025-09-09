package es.diegosr.flightlogapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import es.diegosr.flightlogapp.pojos.Booking;
import es.diegosr.flightlogapp.pojos.Flight;

public class RecyclerHolder extends RecyclerView.ViewHolder {
    private String recyclerList;
    TextView departureSite, arrivalSite, departureDate, arrivalDate, planePlate, planeModel;
    TextView bookingModel, bookingPlate, bookingDate, bookingDuration;

    RecyclerHolder(View itemView, String recyclerList) {
        super(itemView);
        this.recyclerList = recyclerList;
        if(recyclerList.equals("flights")) {
            departureSite = itemView.findViewById(R.id.departureSite);
            arrivalSite = itemView.findViewById(R.id.arrivalSite);
            departureDate = itemView.findViewById(R.id.departureDate);
            arrivalDate = itemView.findViewById(R.id.arrivalDate);
            planePlate = itemView.findViewById(R.id.planePlate);
            planeModel = itemView.findViewById(R.id.planeModel);
        }
        else if(recyclerList.equals("bookings")) {
            bookingModel = itemView.findViewById(R.id.bookingModel);
            bookingPlate = itemView.findViewById(R.id.bookingPlate);
            bookingDate = itemView.findViewById(R.id.bookingDate);
            bookingDuration = itemView.findViewById(R.id.bookingDuration);
        }
    }
    public void bind(Object data, int pos) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        if(recyclerList.equals("flights")) {
            departureDate.setText(dateTimeFormat.format(((Flight) data).getDepartureDate()) + "h\t\t");
            if (((Flight) data).getDepartureSite().getAcronym().isEmpty()) departureSite.setText(((Flight) data).getDepartureSite().getName());
                else departureSite.setText(((Flight) data).getDepartureSite().getAcronym());
            arrivalDate.setText(dateTimeFormat.format(((Flight) data).getArrivalDate()) + "h\t\t");
            if (((Flight) data).getArrivalSite().getAcronym().isEmpty()) arrivalSite.setText(((Flight) data).getArrivalSite().getName());
                else arrivalSite.setText(((Flight) data).getArrivalSite().getAcronym());
            planePlate.setText(((Flight) data).getPlane().getPlate());
            planeModel.setText(((Flight) data).getPlane().getModel());
        }
        else if(recyclerList.equals("bookings")) {
            bookingModel.setText(((Booking) data).getPlane().getModel());
            bookingPlate.setText(((Booking) data).getPlane().getPlate());
            bookingDate.setText(dateTimeFormat.format(((Booking) data).getDate()));
            bookingDuration.setText(timeFormat.format(((Booking) data).getDuration()) + "h");
        }
    }
}
