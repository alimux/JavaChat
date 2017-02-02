
package dnr2i.chat.gui;

import java.awt.*;
import javax.swing.*;

public class GUIJavaChat extends JPanel{
    
    private JTextArea displayPublicMessage;
    private JList usersList;
    private JSplitPane splitPane;
    private static final int LOCATION=100;
    

    public GUIJavaChat() {    
        initPanel();
    }
    
    public void initPanel(){
      System.out.println("affichage du fond");
      this.setBackground(Color.DARK_GRAY);
     
      //set Layout
      this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
      //defining the public message area
      displayPublicMessage = new JTextArea(25,52);
      JScrollPane scrollPane = new JScrollPane(displayPublicMessage);
      displayPublicMessage.setEditable(false);
      
      //defining the users list
      usersList= new JList();
      usersList.setLayoutOrientation(JList.VERTICAL);
      
      //initialize JsplitaPane
      splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, usersList);
      splitPane.setDividerLocation(this.getWidth()-LOCATION);
      this.add(splitPane);
    }
    

    
    
    
    
    
    
    
    
}
