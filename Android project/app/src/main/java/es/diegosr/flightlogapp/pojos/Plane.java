package es.diegosr.flightlogapp.pojos;

public class Plane {
    private int id;
    private String plate;
    private User owner;
    private String model;

    public Plane() {
    }
    public Plane(int id, String plate, User owner, String model) {
        this.id = id;
        this.plate = plate;
        this.owner = owner;
        this.model = model;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }
    public void setPlate(String plate) {
        this.plate = plate;
    }

    public User getOwner() {
        return owner;
    }
    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
}
