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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 * Panel of the circumscribed Area
 *
 * @author Alexandre DUCREUX & plbadille 02/2017
 */
public class UsersPanel extends JPanel implements MouseListener, MouseMotionListener, ListenerModel, Runnable {

    private ArrayList<User> userList;
    private User currentUser;
    private ChatManager chatManager;
    private int previousXPostion;
    private int previousYPosition;
    private int currentXPosition;
    private int currentYposition;
    private Thread t1;

    public UsersPanel(ArrayList<User> userList, User currentUser, ChatManager chatManager) {

        init();
        t1 = new Thread(this);
        t1.start();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.userList = userList;
        this.currentUser = currentUser;
        this.chatManager = chatManager;
        this.previousXPostion = currentUser.getxPosition();
        this.previousYPosition = currentUser.getyPosition();
        this.currentXPosition = currentUser.getxPosition();
        this.currentYposition = currentUser.getyPosition();
    }

    public void init() {
        setBackground(Color.LIGHT_GRAY);
    }

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
        mainAvatar.dispose();

        //draw other users
        Graphics2D avatar = (Graphics2D) g;
        Color darkRed = new Color(192, 41, 41);
        if (userList.size() > 0) {
            for (int i=0;i<userList.size();i++) {
                avatar.drawString(userList.get(i).getUserName(), (currentXPosition - (Constants.avatar)), (currentYposition - (Constants.avatar / 2)));
                avatar.setColor(darkRed);
                avatar.fillOval(userList.get(i).getxPosition(), userList.get(i).getyPosition(), Constants.avatar, Constants.avatar);
                avatar.dispose();
            }
        }

    }

    @Override
    public void mouseClicked(MouseEvent me) {

    }

    @Override
    public void mousePressed(MouseEvent me) {
        System.out.println("Mouse Pressed x:" + me.getX() + " y:" + me.getY());
        previousXPostion = me.getX();
        previousYPosition = me.getY();
    }

    @Override
    public void mouseReleased(MouseEvent me) {

    }

    @Override
    public void mouseEntered(MouseEvent me) {

    }

    @Override
    public void mouseExited(MouseEvent me) {

    }

    @Override
    public void mouseDragged(MouseEvent me) {
        System.out.println("Mouse Dragged xM:" + currentXPosition + " yM:" + currentYposition
                + "Mouse Dragged xA:" + previousXPostion + " yA:" + previousYPosition);
        int dx = me.getX() - previousXPostion;
        int dy = me.getY() - previousYPosition;
        System.out.println("Mouse Dragged x:" + currentXPosition + " y:" + currentYposition);
        previousXPostion = me.getX();
        previousYPosition = me.getY();
        currentXPosition += dx;
        currentYposition += dy;
        chatManager.getCurrentUser().setxPosition(currentXPosition);
        chatManager.getCurrentUser().setyPosition(currentYposition);

    }

    @Override
    public void mouseMoved(MouseEvent me) {

    }

    @Override
    public void modelChanged(Object source) {
        repaint();
    }

    @Override
    public void run() {
        System.out.println("Lancement thread graphique");
        while (true) {
            repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(UsersPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
