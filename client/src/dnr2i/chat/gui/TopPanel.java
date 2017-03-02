package dnr2i.chat.gui;

import dnr2i.chat.manager.ChatManager;
import dnr2i.chat.manager.Message;
import dnr2i.chat.user.User;

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.*;
import dnr2i.util.event.ListenerModel;

/**
 * Class which manage the top panel
 * @author Alexandre DUCREUX & plabadille
 * @since February, 2017
 */
public class TopPanel extends JPanel implements ListenerModel {

    private JTextArea publicArea;
    private JList usersList;
    private ChatManager chatManager;
    private final DefaultListModel model;

    /**
     * constructor, call initPanel to initialize panel
     *
     * @param cm
     */
    public TopPanel(ChatManager cm) {
        initPanel();
        this.chatManager = cm;
        chatManager.addModelListener(this);
        model = new DefaultListModel();
    }

    /**
     * initilialize panel, using GroupLayout for a better showing
     */
    private void initPanel() {

        publicArea = new JTextArea(25, 52);
        publicArea.setEditable(false);

        usersList = new JList();
        usersList.setLayoutOrientation(JList.VERTICAL);

        //set Layout
        GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(publicArea, GroupLayout.PREFERRED_SIZE, Constants.PUBLICAREA_WIDTH, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(usersList, GroupLayout.DEFAULT_SIZE, Constants.USERLIST_WIDTH, Short.MAX_VALUE)
                        .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(publicArea, GroupLayout.DEFAULT_SIZE, Constants.PUBLICAREA_HEIGHT, Short.MAX_VALUE)
                                .addComponent(usersList, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
        );

    }

    @Override
    public void modelChanged(Object source) 
    {
    	String eventDirective = this.chatManager.getEventDirective();
    	System.out.println("Event directive received: " + eventDirective + ". Handling directive...");
    	if (eventDirective != null) {
    		//not really necessary anymore but we leave it for some possible customization:
    		switch(eventDirective) {
	    		case "LOGIN":
	    		case "WELCOME":
	    		case "BYE":
	    			this.updateUserList();
	    		case "NEW_IN_MESSAGE":
	    		case "NEW_OUT_MESSAGE":
	    			Message message = this.chatManager.getLastMessage();
	                publicArea.append("[ " + message.getAuthor()+" : "+ message.getTime() + " ] " + message.getMessage() + "\n");
	    			break;
	    		case "ALREADY_CONNECTED":
	    			this.updateUserList();
	    			break;
	    		default:
	    			System.out.println("unknow directive");
	    	}
    	}    
        
    }
    
    private void updateUserList()
    {
    	Set<Entry<String, User>> set = this.chatManager.getUserList().entrySet();
		Iterator<Entry<String, User>> i = set.iterator();
		//we clear the model in order to don't display again older user.
		model.clear();
		while(i.hasNext()) {
			Entry<String, User> me = i.next();
			model.addElement(me.getKey());
		}
       
        usersList.setModel(model);
    }

}
