package dnr2i.chat.gui;

import dnr2i.chat.manager.ChatManager;
import dnr2i.chat.user.User;
import dnr2i.util.event.ListenerModel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import javax.swing.JPanel;

/**
 * Panel of the circumscribed Area
 * @author Alexandre DUCREUX & plabadille 
 * @since February, 2017
 */
public class UsersPanel extends JPanel implements MouseListener, MouseMotionListener, ListenerModel 
{
	private static final long serialVersionUID = 1L;
	private HashMap<String, User> userList;
    private User currentUser;
    private ChatManager chatManager;
    private int previousXPostion;
    private int previousYPosition;
    private int currentXPosition;
    private int currentYposition;


    /**
     * @param userList
     * @param currentUser
     * @param chatManager
     */
    public UsersPanel(HashMap<String, User> userList, User currentUser, ChatManager chatManager)
    {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.userList = userList;
        this.currentUser = currentUser;
        this.chatManager = chatManager;
        this.previousXPostion = currentUser.getxPosition();
        this.previousYPosition = currentUser.getyPosition();
        this.currentXPosition = currentUser.getxPosition();
        this.currentYposition = currentUser.getyPosition();
        
        init();
    }

    public void init() 
    {
        setBackground(Color.LIGHT_GRAY);
    }


    /* (non-Javadoc)
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g) {
        //draw current user
        super.paint(g);
        Graphics2D mainAvatar = (Graphics2D) g;
        Font font = new Font("Roboto", Font.BOLD, 16);
        Color darkBlue = new Color(15, 78, 126);
        mainAvatar.setFont(font);
        mainAvatar.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        mainAvatar.setColor(Color.darkGray);
        mainAvatar.drawString(currentUser.getUserName(), (currentXPosition - (Constants.avatar)), (currentYposition - (Constants.avatar / 2)));
        mainAvatar.setColor(darkBlue);
        mainAvatar.drawOval(currentXPosition - (Constants.avatarField / 2), currentYposition - (Constants.avatarField / 2), Constants.avatarField, Constants.avatarField);
        mainAvatar.fillOval(currentXPosition, currentYposition, Constants.avatar, Constants.avatar);
        //mainAvatar.dispose();
        
        //draw other users
        Graphics2D avatar = (Graphics2D) g;
        Color darkRed = new Color(192, 41, 41);

        if (userList.size() > 0) {
        	Set<Entry<String, User>> set = this.userList.entrySet();
    		Iterator<Entry<String, User>> i = set.iterator();
    		
    		while(i.hasNext()) {
    			Entry<String, User> me = i.next();
    			if (me.getKey() != currentUser.getUserName()) {
                     mainAvatar.setColor(Color.darkGray);
                     avatar.drawString(me.getKey(), (me.getValue().getxPosition() - (Constants.avatar)), (me.getValue().getyPosition() - (Constants.avatar / 2)));
                     avatar.setColor(darkRed);
                     avatar.fillOval(me.getValue().getxPosition(), me.getValue().getyPosition(), Constants.avatar, Constants.avatar);
                 }
    		}
    		avatar.dispose();
        } else { //Needed for displaying the map when first user (else have to shake the window..)
        	mainAvatar.dispose();
        }
        
    }
    
    //mouse Listeners implements
    @Override
    public void mousePressed(MouseEvent me) 
    {
        previousXPostion = me.getX();
        previousYPosition = me.getY();
    }
    
    @Override
    public void mouseDragged(MouseEvent me) 
    {
        int dx = me.getX() - previousXPostion;
        int dy = me.getY() - previousYPosition;
        previousXPostion = me.getX();
        previousYPosition = me.getY();
        currentXPosition += dx;
        currentYposition += dy;
        repaint();
        
    }
    
    @Override
    public void mouseReleased(MouseEvent me) 
    {
    	System.out.println("mouse released!");
    	chatManager.getCurrentUser().setxPosition(currentXPosition);
        chatManager.getCurrentUser().setyPosition(currentYposition);
        repaint();
        //if coordinate have change we update them to the server.
    	if (currentXPosition != chatManager.getCurrentUser().getyPosition() || currentYposition != chatManager.getCurrentUser().getyPosition()) {
            chatManager.sendCoordinate();
    	}
    }
    
    public void fireGraphicsChange(HashMap<String, User> userList)
    {
    	this.userList = userList;
    	repaint();
    }
    
    @Override
    public void modelChanged(Object source) 
    {
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent me) {}
    @Override
    public void mouseEntered(MouseEvent me) {}
    @Override
    public void mouseExited(MouseEvent me) {}
    @Override
    public void mouseMoved(MouseEvent me) {}
    
}
