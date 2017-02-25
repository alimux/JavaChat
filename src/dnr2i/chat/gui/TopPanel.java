package dnr2i.chat.gui;

import java.awt.*;
import javax.swing.*;

/**
 * Class which manage the top panel
 *
 * @author Alexandre DUCREUX 02/2017
 */
public class TopPanel extends JPanel {

    private JTextArea publicArea;
    private JList usersList;

    /**
     * constructor, call initPanel to initialize panel
     */
    public TopPanel() {
        initPanel();
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
                                .addComponent(publicArea, GroupLayout.DEFAULT_SIZE, Constants.PUBLICAREA_HEIGTH, Short.MAX_VALUE)
                                .addComponent(usersList, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
        );
    }

}
