
package dnr2i.chat.gui;

import javax.swing.*;


public class InputPanel extends JPanel{
    
    private JButton send;
    private JTextField input;
    private static final String BUTTON_LABEL = "Envoyer";

    public InputPanel() {
        
        initPanel();
    }
    
    public void initPanel(){
        
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        input = new JTextField();
        send = new JButton(BUTTON_LABEL);
        this.add(input);
        this.add(send);
    }
    
    
    
}
