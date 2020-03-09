package mitigate.transportation.user;

public class DriverModel {
    private String uid;
    private  String name;
    private   String email;
    private   String car_model;
    private   String car_number;
    private   String car_color;
    private   int count;
    private int rating;
    private String comment;

    public DriverModel() {
    }

    public DriverModel(String uid, String name, String email, String car_model, String car_number, String car_color, int count, int rating, String comment) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.car_model = car_model;
        this.car_number = car_number;
        this.car_color = car_color;
        this.count = count;
        this.rating = rating;
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCar_model() {
        return car_model;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public String getCar_number() {
        return car_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public String getCar_color() {
        return car_color;
    }

    public void setCar_color(String car_color) {
        this.car_color = car_color;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
