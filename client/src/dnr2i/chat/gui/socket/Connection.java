package dnr2i.chat.gui.socket;

import java.io.IOException;
import java.net.Socket;
import dnr2i.chat.gui.Constants;

/**
 * A class for establishing connection to the server (socket)
 * You can setup SERVER & PORT in constants file
 * @author Alexandre DUCREUX & plabadille
 * @since February, 2017
 */
public class Connection 
{
    private Socket socket;

    /**
     * constructor call initConnection to connect client to serve.
     */
    public Connection() {
        initConnection();
    }

    /**
     * launch connection to the server
     */
    private void initConnection() {
        try {
            System.out.println("Connexion au serveur :"+Constants.SERVER+":"+Constants.PORT);
            socket = new Socket(Constants.SERVER, Constants.PORT);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * getter socket
     * @return socket
     */
    public Socket getSocket() {
        return socket;
    }

}
