
package dnr2i.chat.user;

/**
 * Class which defines a TChatter
 * @author Alexandre DUCREUX 02/2017
 */
public class User {
    
    private String userName;
    private int xPosition;
    private int yPosition;

    public User(String userName, int xPosition, int yPosition) {
        this.userName = userName;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getxPosition() {
        return xPosition;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }
    
    
    
}
