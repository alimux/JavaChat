
package dnr2i.chat.gui;

/**
 * @author Alexandre DUCREUX 02/2017
 * Class wich defines the Down panel with textfield and send button
 */

import dnr2i.chat.manager.ChatManager;
import dnr2i.chat.manager.Message;
import dnr2i.util.event.ListenerModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


public class DownPanel extends JPanel implements ActionListener, ListenerModel{
    
    private JButton send;
    private JTextField input;
    private String messageOutComing;
    private ChatManager chatManager;

    /**
     * constructor call initPanel Method
     * @param cm
     */
    public DownPanel(ChatManager cm) {
        initPanel();
        this.chatManager = cm;
        chatManager.addModelListener(this);
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
            messageOutComing = input.getText().toString();
            chatManager.getMessage().setMessageOutComing(messageOutComing);
            chatManager.sendMessage(messageOutComing);
            System.out.println("message entré dans la zone de texte : "+messageOutComing);
            input.setText("");
            
        }
        
    }

    @Override
    public void modelChanged(Object source) {    
        //System.out.println("update :"+source);
    }

   

   
        
}
