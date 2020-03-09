package mitigate.transportation.user;

public class UserModel {
    String email;
    String name;
    String userID;
    int rating;
    String comment;

    public UserModel(String email, String name, String userID, int rating, String comment) {
        this.email = email;
        this.name = name;
        this.userID = userID;
        this.rating = rating;
        this.comment = comment;
    }

    public UserModel() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
