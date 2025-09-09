package es.diegosr.flightlogapp.pojos;

import java.sql.Time;
import java.util.Date;

public class Flight {
    private int id;
    private LandingField departureSite;
    private Date departureDate;
    private LandingField arrivalSite;
    private Date arrivalDate;
    private Plane plane;
    private Time duration;
    private User pilot;
    private Boolean monoPilot;
    private int dayLandings;
    private int nightLandings;

    public Flight() {
    }
    public Flight(int id, LandingField departureSite, Date departureDate, LandingField arrivalSite, Date arrivalDate, Plane plane, Time duration, User pilot, Boolean monoPilot, int dayLandings, int nightLandings) {
        this.id = id;
        this.departureSite = departureSite;
        this.departureDate = departureDate;
        this.arrivalSite = arrivalSite;
        this.arrivalDate = arrivalDate;
        this.plane = plane;
        this.duration = duration;
        this.pilot = pilot;
        this.monoPilot = monoPilot;
        this.dayLandings = dayLandings;
        this.nightLandings = nightLandings;
    }
    public Flight(LandingField departureSite, Date departureDate, LandingField arrivalSite, Date arrivalDate, Plane plane, Time duration, User pilot, Boolean monoPilot, int dayLandings, int nightLandings) {
        this.departureSite = departureSite;
        this.departureDate = departureDate;
        this.arrivalSite = arrivalSite;
        this.arrivalDate = arrivalDate;
        this.plane = plane;
        this.duration = duration;
        this.pilot = pilot;
        this.monoPilot = monoPilot;
        this.dayLandings = dayLandings;
        this.nightLandings = nightLandings;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public LandingField getDepartureSite() {
        return departureSite;
    }
    public void setDepartureSite(LandingField departureSite) {
        this.departureSite = departureSite;
    }

    public Date getDepartureDate() {
        return departureDate;
    }
    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public LandingField getArrivalSite() {
        return arrivalSite;
    }
    public void setArrivalSite(LandingField arrivalSite) {
        this.arrivalSite = arrivalSite;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }
    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Plane getPlane() {
        return plane;
    }
    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public Time getDuration() {
        return duration;
    }
    public void setDuration(Time duration) {
        this.duration = duration;
    }

    public User getPilot() {
        return pilot;
    }
    public void setPilot(User pilot) {
        this.pilot = pilot;
    }

    public Boolean getMonoPilot() {
        return monoPilot;
    }
    public void setMonoPilot(Boolean monoPilot) {
        this.monoPilot = monoPilot;
    }

    public int getDayLandings() {
        return dayLandings;
    }
    public void setDayLandings(int dayLandings) {
        this.dayLandings = dayLandings;
    }

    public int getNightLandings() {
        return nightLandings;
    }
    public void setNightLandings(int nightLandings) {
        this.nightLandings = nightLandings;
    }
}
