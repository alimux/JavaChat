package dnr2i.chat.user;

import dnr2i.util.event.ListenableModel;

/**
 * A class for representing an user.
 * @author Alexandre DUCREUX & plabadille
 * @since February, 2017
 */
public class User extends ListenableModel 
{
    private String userName;
    private int xPosition;
    private int yPosition;

    /**
     * Build an user instance
     * @param userName
     * @param xPosition
     * @param yPosition
     */
    public User(String userName, int xPosition, int yPosition)
    {
        this.userName = userName;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    /**
     * getter user name
     *
     * @return
     */
    public String getUserName() 
    {
        return userName;
    }

    /**
     * setter username
     *
     * @param userName
     */
    public void setUserName(String userName)
    {
        this.userName = userName;

    }

    /**
     * getter x position of the user
     *
     * @return
     */
    public int getxPosition()
    {
        return xPosition;
    }

    /**
     * setter x position of the user
     *
     * @param xPosition
     */
    public void setxPosition(int xPosition)
    {
        this.xPosition = xPosition;
        fireChanged();
    }

    /**
     * getter y position of the user
     *
     * @return
     */
    public int getyPosition()
    {
        return yPosition;
    }

    /**
     * setter y position of the user
     *
     * @param yPosition
     */
    public void setyPosition(int yPosition)
    {
        this.yPosition = yPosition;
        fireChanged();
    }

}
