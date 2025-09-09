package es.diegosr.flightlogapp.pojos;

public class User {
    private int id;
    private String username;
    private String password;
    private String userType;
    private LandingField landingField;
    private String name;
    private String lastName;
    private String image;

    public User() {
    }
    public User(int id, String username, String password, String userType, LandingField landingField, String name, String lastName, String image) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.landingField = landingField;
        this.name = name;
        this.lastName = lastName;
        this.image = image;
    }
    public User(int id, String userType, LandingField landingField, String name, String lastName, String image) {
        this.id = id;
        this.userType = userType;
        this.landingField = landingField;
        this.name = name;
        this.lastName = lastName;
        this.image = image;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }
    public void setUserType(String userType) {
        this.userType = userType;
    }

    public LandingField getLandingField() {
        return landingField;
    }
    public void setLandingField(LandingField landingField) {
        this.landingField = landingField;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
}
