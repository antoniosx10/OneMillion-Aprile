package unisa.it.pc1.provacirclemenu.model;

public class User {

    private String userId;
    private String displayName;
    private String number;
    private String image;

    public User() {
    }

    public User(String userId, String displayName, String number) {
        this.userId = userId;
        this.displayName = displayName;
        this.number = number;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number= number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
