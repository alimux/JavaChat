package dnr2i.chat.gui.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Class which manages sending & retrieving message
 * @author Alexandre DUCREUX 02/2017
 */
public class Messages {

    private PrintWriter output;
    private BufferedReader input;
    private Socket socket;
    private String incomingMessage;
    /**
     * constructor
     * @param socket 
     */
    public Messages(Socket socket) {
        this.socket = socket;
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * send message method
     * @param message 
     */
    public void sendMessage(String message) {
        System.out.println("envoi du message : " + message);
        output.println("SET_MSG");
        output.println(message);
        output.flush();
    }
    /**
     * retrieve message method
     * @param out 
     */
    public void retrieveMessage(PrintWriter out) {

        System.out.println("reception d'un message");
        output.println("GET_MSG");
        output.flush();
        try {
            incomingMessage = input.readLine();

        } catch (IOException ex) {
            Logger.getLogger(Messages.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    /**
     * getter incomingMessage
     * @return String
     */
    public String getIncomingMessage() {
        return incomingMessage;
    }
    
    

}
