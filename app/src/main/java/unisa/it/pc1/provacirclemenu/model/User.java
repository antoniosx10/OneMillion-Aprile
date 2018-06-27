package unisa.it.pc1.provacirclemenu.model;

public class User {

    private String userId;
    private String displayName;
    private String number;
    private String image;
    private String thumb_image;
    private String deviceToken;

    public User() {
    }

    public User(String displayName, String number, String image,String thumb_image,String deviceToken) {
        this.displayName = displayName;
        this.number = number;
        this.image = image;
        this.thumb_image = thumb_image;
        this.deviceToken = deviceToken;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
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

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
