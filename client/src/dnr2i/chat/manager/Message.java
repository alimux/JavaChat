
package dnr2i.chat.manager;

//import dnr2i.util.event.ListenableModel;

/**
 * Class which defines Message
 * @author Alexandre DUCREUX & plabadille
 * @since February, 2017
 */
public class Message //extends ListenableModel
{
    
    private String message;
    private String time;
    private String author;
    
    public Message(String message, String author, String time)
    {
    	this.message = message;
    	this.time = time;
    	this.author = author;
    }
    
	public String getMessage() 
	{
		return message;
	}
	public String getTime() 
	{
		return time;
	}
	public String getAuthor() 
	{
		return author;
	}
    public String getStyle(String activeUser)
    {
    	String style;
    	if(this.author == "System") { //system message
    		style = "grey";
    	} else if(this.author == activeUser) { //the author is the active user
    		style = "blue";
    	} else { //other user message
    		style = "black";
    	}
    	return style;
    }
}
