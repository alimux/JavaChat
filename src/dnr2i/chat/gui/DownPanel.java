
package dnr2i.chat.gui;

/**
 * @author Alexandre DUCREUX 02/2017
 * Class wich defines the Down panel with textfield and send button
 */

import dnr2i.util.event.ModelListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


public class DownPanel extends JPanel implements ActionListener{
    
    private JButton send;
    private JTextField input;
    private String messageOutcoming;

    /**
     * constructor call initPanel Method
     */
    public DownPanel() {
  
        initPanel();
        System.out.println("taille :"+GroupLayout.PREFERRED_SIZE);
    }
    /**
     * method wich initializes the down panel
     */
    private void initPanel(){
        
        input = new JTextField();
        send = new JButton(Constants.BUTTON_LABEL);
        
        //Layout -> Group layout to have a better showing
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(input, GroupLayout.PREFERRED_SIZE, Constants.MESSAGEINPUT_WIDTH, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, Constants.GAP_WIDTH, Short.MAX_VALUE)
                .addComponent(send, GroupLayout.PREFERRED_SIZE, Constants.SENDBUTTON_WIDTH, GroupLayout.PREFERRED_SIZE)
                .addGap(Constants.GAP_WIDTH, Constants.GAP_WIDTH, Constants.GAP_WIDTH))    
        );
        
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addGap(Constants.GAP_WIDTH, Constants.GAP_WIDTH, Constants.GAP_WIDTH)                     
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(send, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 4, Short.MAX_VALUE))
                .addComponent(input, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        
        );
        
        send.addActionListener(this);
      
    }
    /**
     * implements ActionListener, manage send click
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        Object source = e.getSource();
        if(source==send){
            messageOutcoming = input.getText();
            System.out.println("message entré dans la zone de texte : "+messageOutcoming);
            
        }
        
    }
    /**
     * getter MessageOutComing
     * @return String
     */
    public String getMessageOutcoming() {
        return messageOutcoming;
    }
    
    

   
        
}
