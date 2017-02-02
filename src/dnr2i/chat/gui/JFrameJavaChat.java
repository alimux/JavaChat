
package dnr2i.chat.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.HeadlessException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.border.Border;


public class JFrameJavaChat extends JFrame{
    
    private GUIJavaChat gui;
    private InputPanel inputPanel;
    
    //Definition of the size of the window
    private final static int WIDTH = 850;
    private final static int HEIGHT = 600;
    //title of the window
    private final static String TITLE = "Java chat";

    public JFrameJavaChat() throws HeadlessException {
        super(TITLE);
        gui = new GUIJavaChat();
        inputPanel = new InputPanel();
        initialize();
        
    }
    
    public void initialize(){
        
        //adding panels
        
        Box mainPanel = Box.createVerticalBox();
        
        Border blackLine = BorderFactory.createLineBorder(Color.BLACK);
        mainPanel.setBorder(blackLine);
        mainPanel.add(gui);
        mainPanel.add(inputPanel);
      
        this.getContentPane().add(mainPanel);
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setVisible(false);
        this.setSize(WIDTH, HEIGHT);
        this.setLocationRelativeTo(null);
        
        String login = (String)JOptionPane.showInputDialog(
                    this,
                    "Entrez votre login",
                    "Login",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "");
        //if login isNull exit, else display the main Frame
        if(login==null){
            System.exit(0);
        }
        else{
            setVisible(true);
        }
        
        System.out.println("Login : "+login);
        
        
            
    }
    
    
  
    
}
