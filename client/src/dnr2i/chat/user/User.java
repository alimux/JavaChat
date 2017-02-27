
package dnr2i.chat.user;

import dnr2i.util.event.ListenableModel;

/**
 * Class which defines a TChatter
 * @author Alexandre DUCREUX & plbadille 02/2017
 */
public class User extends ListenableModel{
    
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
        //System.out.println("update user x:" + xPosition);
        this.xPosition = xPosition;
        fireChanged();
    }

    public int getyPosition() {
        return yPosition;
    }

    public void setyPosition(int yPosition) {
        //System.out.println("update user y:" + yPosition);
        this.yPosition = yPosition;
        fireChanged();
    }
    
    
    
}
