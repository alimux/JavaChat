
package dnr2i.chat.manager;

/**
 * Class which defines Message
 * @author Alexandre DUCREUX & plabadille
 * @since February, 2017
 */
public class Message
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
	
	//unused yet.
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
