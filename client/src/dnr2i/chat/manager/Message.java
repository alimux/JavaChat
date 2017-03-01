
package dnr2i.chat.manager;

//TODO Add message history and date
import dnr2i.util.event.ListenableModel;

/**
 * Class which defines Message
 * @author Alexandre DUCREUX & plabadille
 * @since February, 2017
 */
public class Message extends ListenableModel{
    
    private String outComingMessage;
    private String inComingMessage;
    
    public String getMessageOutComing() {
        return outComingMessage;
    }

    public void setMessageOutComing(String message) {
        this.outComingMessage = message;
    }

    public String getInComingMessage() {       
        return inComingMessage;      
    }

    public void setInComingMessage(String inComingMessage) {
        this.inComingMessage = inComingMessage;
    }  
    
}
