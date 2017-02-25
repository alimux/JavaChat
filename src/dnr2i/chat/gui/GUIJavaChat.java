
package dnr2i.chat.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.border.Border;


public class GUIJavaChat extends JFrame{
    
    private TopPanel gui;
    private DownPanel inputPanel;
    
    private String loginName;
    


    public GUIJavaChat() throws HeadlessException {
        super(Constants.JFRAMETITLE);
        gui = new TopPanel();
        inputPanel = new DownPanel();
        initFrame();
        
    }
    
    public void initFrame(){
        
        //adding panels
        
        Box mainPanel = Box.createVerticalBox();
        
        Border blackLine = BorderFactory.createLineBorder(Color.BLACK);
        mainPanel.setBorder(blackLine);
        mainPanel.add(gui);
        mainPanel.add(inputPanel);
      
        this.getContentPane().add(mainPanel);
        
        //JFrame options
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setVisible(false);
        this.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        this.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        this.setBounds(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        this.setLocationRelativeTo(null);
        
        //login
        login();  
            
    }
    
    private void login(){
        //Input dialog of login
        loginName = (String)JOptionPane.showInputDialog(
                    this,
                    "Entrez votre login",
                    "Login",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "");
        //if login isNull exit, else display the main Frame
        if(loginName==null){
            System.exit(0);
        }
        else{
            setVisible(true);
        }
        
        System.out.println("Login : "+loginName);
        
    }
    
}
