package dnr2i.chat.gui;

import dnr2i.chat.manager.ChatManager;
import javax.swing.*;
import dnr2i.util.event.ListenerModel;

/**
 * Class which manage the top panel
 *
 * @author Alexandre DUCREUX & plbadille 02/2017
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
    public void modelChanged(Object source) {
        //System.out.println("update !");
        if (chatManager.getJustConnected()) { 
                //System.out.println(chatManager.getCurrentUser().getUserName()+" vient de se connecter au Panel TOP");
                publicArea.setText("[ "+chatManager.getCurrentUser().getUserName()+" ] vient de se connecter au chat...\n");

        }
        
        if(chatManager.getMessage()!=null){
            //System.out.println("message non null");
            if(chatManager.getMessage().getMessageOutComing()!=null){
                System.out.println("message sortant !");
                publicArea.append("[ "+chatManager.getCurrentUser().getUserName()+" ] "+chatManager.getMessage().getMessageOutComing()+"\n");
            }
            if(chatManager.getMessage().getInComingMessage()!=null){
                System.out.println("message entrant !");
                publicArea.append("[ "+chatManager.getSendedMessageUser().getUserName()+" ] "+chatManager.getMessage().getInComingMessage()+"\n");
            }
        }
        if(!chatManager.getUserList().isEmpty() && chatManager.isListUpdated()){
            for(int i=0;i<chatManager.getUserList().size();i++){
                 
                model.addElement(chatManager.getUserList().get(i).getUserName());
                
            }
           
            usersList.setModel(model);
            chatManager.setListUpdated(false);
            
        }
        
        
    }

}
