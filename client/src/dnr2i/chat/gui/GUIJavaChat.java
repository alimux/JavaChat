package dnr2i.chat.gui;

import dnr2i.chat.manager.ChatManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.Border;


/**
 * JFrame, mainFrame
 * @author Alexandre DUCREUX & plabadille
 * @since February, 2017
 */
public class GUIJavaChat extends JFrame
{
	private static final long serialVersionUID = 1L;
	private final TopPanel topPanel;
    private final DownPanel downPanel;
    private URL imgURL;
    private ImageIcon icon;
    private final ChatManager chatManager;
    private static final int STARTUP_POSITION = 200;

    /**
     * constructor call initFrame
     */
    public GUIJavaChat() 
    {

        super(Constants.JFRAMETITLE);
        chatManager = new ChatManager();
        topPanel = new TopPanel(chatManager);
        downPanel = new DownPanel(chatManager);
        initFrame();
    }

    /**
     * setup the JFrame
     */
    private void initFrame() 
    {

        imgURL = getClass().getResource("chatIco.png");
        icon = new ImageIcon(imgURL);

        //adding panels    
        Box mainPanel = Box.createVerticalBox();

        Border blackLine = BorderFactory.createLineBorder(Color.BLACK);
        mainPanel.setBorder(blackLine);
        mainPanel.add(topPanel);
        mainPanel.add(downPanel);

        this.getContentPane().add(mainPanel);

        //JFrame options
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	        
        this.addWindowListener(new WindowAdapter() {
        	  public void windowClosing(WindowEvent we) {
        		  chatManager.logout();
        		  System.exit(0);
        	  }
    	});
        
        pack();
        setVisible(false);
        this.setIconImage(icon.getImage());
        this.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        this.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        this.setBounds(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        this.setLocation(computeWindowPosition().width,computeWindowPosition().height);

        login();

    }
   

    /**
     * InputDialog to input a login name
     */
    private void login() 
    {
        //Input dialog of login
        String loginBox = (String) JOptionPane.showInputDialog(
                this,
                "Entrez votre login",
                "Login",
                JOptionPane.PLAIN_MESSAGE,
                icon,
                null,
                "");
        //if login isNull exit, else display the main Frame
        if (loginBox == null) {
            System.out.println("Login null");
            System.exit(0);
        } else {
            chatManager.login(loginBox, STARTUP_POSITION, STARTUP_POSITION);
            setVisible(true);
            new GUICircumscribedArea(icon, chatManager);
        }
        System.out.println("Login : " + loginBox);

    }
    /**
     * Method which compute the position on the main JFrame
     * @return 
     */
    public Dimension computeWindowPosition()
    {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        int xPos = ((width - Constants.WINDOW_WIDTH )/6);
        int yPos = ((height - Constants.WINDOW_HEIGHT )/2);
        Dimension d = new Dimension(xPos, yPos);
        return d;
    }

}
