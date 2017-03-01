
package dnr2i.chat.manager;

import dnr2i.chat.user.User;
import dnr2i.util.event.ListenableModel;
import java.util.ArrayList;

/**
 * Class which defines Message
 * @author Alexandre DUCREUX & plbadille 02/2017
 */
public class Message extends ListenableModel{
    
    private String outComingMessage;
    private String inComingMessage;
    private User user;
    private ArrayList<Message> messages;
    
    public Message(){
        messages = new ArrayList<>();
    }

    public String getMessageOutComing() {
        return outComingMessage;
    }

    public void setMessageOutComing(String message) {
        this.outComingMessage = message;
        this.messages.add(this);
        //System.out.println("Classe Message OC: "+message);
        fireChanged();
    }

    public String getInComingMessage() {       
        return inComingMessage;      
    }

    public void setInComingMessage(String inComingMessage) {
        this.inComingMessage = inComingMessage;
        this.messages.add(this);
        //System.out.println("Classe Message IC: "+inComingMessage);
        fireChanged();
    }
    
    //TODO useless?
    public ArrayList<Message> setMessage(Message message){
        messages.add(message);
        return messages;
    }
    
    
    
    
    
}
