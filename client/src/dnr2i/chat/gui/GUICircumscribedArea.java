package dnr2i.chat.gui;

import dnr2i.chat.manager.ChatManager;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 * JFrame of the circumscribed Area where we can move your own "avatar"
 * @author Alexandre DUCREUX & plbadille 02/2017
 */
public class GUICircumscribedArea extends JFrame implements Runnable{

    private ImageIcon icon;
    private JFrame main;
    private ChatManager chatManager;

    public GUICircumscribedArea(ImageIcon ii, ChatManager chatManager) {
        this.icon = ii;
        this.chatManager = chatManager;
        init();
        UsersPanel up = new UsersPanel(chatManager.getUserList(), chatManager.getCurrentUser(), chatManager);
        //adding panels    
        Box mainPanel = Box.createVerticalBox();
        mainPanel.add(up);
        this.getContentPane().add(mainPanel);
    }
    /**
     * init JFrame
     */
    public void init() {
        //JFrame options 
        this.setIconImage(icon.getImage());
        this.setPreferredSize(new Dimension(Constants.WINDOW_C_AREA_WIDTH, Constants.WINDOW_C_AREA_HEIGHT));
        this.setSize(Constants.WINDOW_C_AREA_WIDTH, Constants.WINDOW_C_AREA_HEIGHT);
        this.setLocation(computeWindowPosition().width, computeWindowPosition().height);
        pack();
        setVisible(true);
    }
    /**
     * method which compute the position of the secondary JFrame
     * @return 
     */
    public Dimension computeWindowPosition() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        int xPos = ((width - Constants.WINDOW_WIDTH) / 2) + (Constants.WINDOW_WIDTH - Constants.WINDOW_C_AREA_WIDTH / 2);
        int yPos = (height - Constants.WINDOW_C_AREA_HEIGHT) / 2;
        Dimension d = new Dimension(xPos, yPos);
        return d;
    }

    @Override
    public void run() {
        
    }

}
