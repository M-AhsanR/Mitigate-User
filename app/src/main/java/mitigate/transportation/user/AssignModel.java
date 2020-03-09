package mitigate.transportation.user;

public class AssignModel {
    private String driver_uid;
    private String user_uid;
    private String id;
    private String start_location;
    private String end_location;
    private String car_model;
    private String car_plate;
    private String car_color;
    private String user_name;
    private String user_email;
    private String status;
    private Double start_Lat;
    private Double start_Lng;
    private Double end_Lat;
    private Double end_Lng;

    public AssignModel(String driver_uid, String user_uid, String id, String start_location, String end_location, String car_model, String car_plate, String car_color, String user_name, String user_email, String status, Double start_Lat, Double start_Lng, Double end_Lat, Double end_Lng) {
        this.driver_uid = driver_uid;
        this.user_uid = user_uid;
        this.id = id;
        this.start_location = start_location;
        this.end_location = end_location;
        this.car_model = car_model;
        this.car_plate = car_plate;
        this.car_color = car_color;
        this.user_name = user_name;
        this.user_email = user_email;
        this.status = status;
        this.start_Lat = start_Lat;
        this.start_Lng = start_Lng;
        this.end_Lat = end_Lat;
        this.end_Lng = end_Lng;
    }

    public AssignModel() {
    }

    public String getDriver_uid() {
        return driver_uid;
    }

    public void setDriver_uid(String driver_uid) {
        this.driver_uid = driver_uid;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStart_location() {
        return start_location;
    }

    public void setStart_location(String start_location) {
        this.start_location = start_location;
    }

    public String getEnd_location() {
        return end_location;
    }

    public void setEnd_location(String end_location) {
        this.end_location = end_location;
    }

    public String getCar_model() {
        return car_model;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public String getCar_plate() {
        return car_plate;
    }

    public void setCar_plate(String car_plate) {
        this.car_plate = car_plate;
    }

    public String getCar_color() {
        return car_color;
    }

    public void setCar_color(String car_color) {
        this.car_color = car_color;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getStart_Lat() {
        return start_Lat;
    }

    public void setStart_Lat(Double start_Lat) {
        this.start_Lat = start_Lat;
    }

    public Double getStart_Lng() {
        return start_Lng;
    }

    public void setStart_Lng(Double start_Lng) {
        this.start_Lng = start_Lng;
    }

    public Double getEnd_Lat() {
        return end_Lat;
    }

    public void setEnd_Lat(Double end_Lat) {
        this.end_Lat = end_Lat;
    }

    public Double getEnd_Lng() {
        return end_Lng;
    }

    public void setEnd_Lng(Double end_Lng) {
        this.end_Lng = end_Lng;
    }
}
