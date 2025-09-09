package es.diegosr.flightlogapp.pojos;

import java.util.Date;
import java.sql.Time;

public class Booking {
    private int id;
    private User pilot;
    private Plane plane;
    private Date date;
    private Time duration;

    public Booking() {
    }
    public Booking(int id, User pilot, Plane plane, Date date, Time duration) {
        this.id = id;
        this.pilot = pilot;
        this.plane = plane;
        this.date = date;
        this.duration = duration;
    }
    public Booking(User pilot, Plane plane, Date date, Time duration) {
        this.pilot = pilot;
        this.plane = plane;
        this.date = date;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public User getPilot() {
        return pilot;
    }
    public void setPilot(User pilot) {
        this.pilot = pilot;
    }

    public Plane getPlane() {
        return plane;
    }
    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public Time getDuration() {
        return duration;
    }
    public void setDuration(Time duration) {
        this.duration = duration;
    }
}
