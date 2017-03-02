
package dnr2i.chat.gui;

/**
 * Class for setup the DownPanel
 * @author Alexandre DUCREUX & plabadille 
 * @since February, 2017
 */

import dnr2i.chat.manager.ChatManager;
import dnr2i.util.event.ListenerModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

public class DownPanel extends JPanel implements ActionListener, ListenerModel, KeyListener
{    
	private static final long serialVersionUID = 1L;
	private JButton send;
    private JTextField input;
    private String messageOutComing;
    private ChatManager chatManager;

    /**
     * Build a DownPanel instance
     * @param cm
     */
    public DownPanel(ChatManager cm) 
    {
        initPanel();
        this.chatManager = cm;
        chatManager.addModelListener(this);
    }
    
    /**
     * method used to initializes the down panel
     */
    private void initPanel()
    {    
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
        input.addKeyListener(this);
        
    }
    
    /**
     * implements ActionListener, manage send click
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) 
    {    
        Object source = e.getSource();
        if(source==send){
            sendMessage();
        }  
    }
    /**
     * send message function
     */
    public void sendMessage(){
        messageOutComing = input.getText().toString();
        chatManager.sendMessage(messageOutComing);
        System.out.println("message entré dans la zone de texte : "+messageOutComing);
        input.setText("");
    }

    @Override
    public void modelChanged(Object source) {}

    @Override
    public void keyTyped(KeyEvent ke) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_ENTER){
            sendMessage();
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        
    }

}
