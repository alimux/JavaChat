package dnr2i.chat.gui;

import dnr2i.chat.manager.ChatManager;
import java.awt.Color;
import java.awt.Dimension;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.Border;

public class GUIJavaChat extends JFrame {

    private final TopPanel topPanel;
    private final DownPanel downPanel;
    private URL imgURL;
    private ImageIcon icon;
    private final ChatManager chatManager;
    private static final int STARTUP_POSITION = 0;

    /**
     * constructor call initFrame
     */
    public GUIJavaChat() {

        super(Constants.JFRAMETITLE);
        chatManager = new ChatManager();
        topPanel = new TopPanel(chatManager);
        downPanel = new DownPanel(chatManager);
        initFrame();
    }

    /**
     * setup the JFrame
     */
    private void initFrame() {

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
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setVisible(false);
        this.setIconImage(icon.getImage());
        this.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        this.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        this.setBounds(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        this.setLocationRelativeTo(null);

        //login
        login();

    }

    /**
     * InputDialog to input a login name
     */
    private void login() {

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
        }
        System.out.println("Login : " + loginBox);

    }

}
