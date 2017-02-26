
package dnr2i.chat.manager;

import dnr2i.util.event.ListenableModel;

/**
 * Class which defines Message
 * @author Alexandre DUCREUX 02/2017
 */
public class Message extends ListenableModel{
    
    private String outComingMessage;
    private String inComingMessage;

    public String getMessageOutComing() {
        return outComingMessage;
    }

    public void setMessageOutComing(String message) {
        this.outComingMessage = message;
        System.out.println("Classe Message OC: "+message);
        fireChanged();
    }

    public String getInComingMessage() {
        
        return inComingMessage;      
    }

    public void setInComingMessage(String inComingMessage) {
        this.inComingMessage = inComingMessage;
        System.out.println("Classe Message IC: "+inComingMessage);
        fireChanged();
    }
    
    
    
    
    
}
