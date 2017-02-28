package dnr2i.chat.manager;

import dnr2i.chat.gui.socket.Connection;
import dnr2i.chat.user.User;
import dnr2i.util.event.ListenableModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class which manages sending & retrieving message
 *
 * @author Alexandre DUCREUX & plbadille 02/2017
 */
public class ChatManager extends ListenableModel {

	private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    public Thread t1;
    
    private Message message;
    private ArrayList<User> userList;
    private Boolean justConnected;
//    private Connection connection;
    
    private String outComingMessage;
    
    
    private User currentUser;
    private User sendedMessageUser;
    private Thread t2, t3, t4;
    private volatile boolean threadSuspended;
    private boolean listUpdated = true;

    /**
     * constructor
     *
     */
    public ChatManager() {
        this.userList = new ArrayList<User>();
        this.message = new Message();
        this.justConnected = true;
        
        System.out.println("Connection in progress...");
        Connection connection = new Connection();
        this.socket = connection.getSocket();
        
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream());
            System.out.println("Connection to server established");
        } catch(IOException e) {
        	System.out.println("Connection to server failed: " + e.getMessage());
        }
            

        System.out.println("Starting thread for listening server directives...");
        this.t1 = new Thread(new ClientIncomingDirective(this, this.input));
        this.t1.start();
        
//        t2 = new Thread(this);
//        t2.start();
    }
    
    /**
     * Method which is called on login, send login and position to server
     *
     * @param loginName
     * @param x
     * @param y
     */
    public void login(String loginName, int x, int y) {
        if (justConnected) { //TODO check if we can remove it.
            this.currentUser = new User(loginName, x, y);
            this.userList.add(this.currentUser);

            //send login to server
            System.out.println("Sending LOGIN directive to the server...");
            String login = loginName + "," + x + "," + y;
            output.println("LOGIN");
            output.println(login);
            output.flush();
            System.out.println("LOGIN directive send to the server: " + login);
//                test();
            fireChanged();
            justConnected = false;           
        }
    }
    
    /**
     * method wich update the coordinates of the current user
     */
    public void updateUserCoordinate() {
    	System.out.println("Sending SET_COORDINATE directive to the server...");
    	String coordinate = currentUser.getxPosition() + "," + currentUser.getyPosition();
        output.println("SET_COORDINATE");
        output.println(coordinate);
        output.flush();
        System.out.println("New coordinate sent to the server: " + coordinate);
    }

    /**
     * send message method
     *
     * @param ocMessage
     */
    public void sendMessage(String ocMessage) { //TODO check if ocMessage have the attended shape
    	System.out.println("Sending SET_MSG directive to the server...");
        message.setMessageOutComing(ocMessage);
        output.println("SET_MSG");
        output.println(ocMessage);
        output.flush();
        System.out.println("New message sent to the server : " + ocMessage);
        fireChanged();
    }

    /**
     * retrieve message method
     *
     * @param out
     */
    public void retrieveMessage(String incomingMessage) {

        System.out.println("Handling the server response for directive GET_MSG: " + incomingMessage);

        String[] splitMessage = incomingMessage.split("<END/>");
        //retrieve the user who sended message
        for (int i = 0; i < this.userList.size(); i++) { //TODO check if sendedMessageUser is usefull
            if (this.userList.get(i).getUserName().equals(splitMessage[0])) {
                this.sendedMessageUser = this.userList.get(i);
            }
        }
        this.message.setInComingMessage(splitMessage[1]);
        fireChanged();
    }

    /**
     * Method which retrieves the list of the Tchatters
     *
     * @return ArrayList
     */
    public void retrieveUsersList(String incomingUserList) { //TODO see for sending the userlist to front everytime there's a change
    	//TODO remake...

//        //parsing list
//        String[] userSplit = incomingUserList.split(";");
//        for (int i = 0; i < userSplit.length; i++) {
//            String[] user = userSplit[i].split(",");
//            for (int j = 0; j < user.length; j++) {
//                if (user[0] != currentUser.getUserName()) {
//                    userList.add(new User(user[0], Integer.valueOf(user[1]), Integer.valueOf(user[2])));
//                }
//            }
//        }

    }

//    public void test() {
//        User user1 = new User("Pierre", 210, 100);
//        User user2 = new User("Jacquie", 120, 30);
//        User user3 = new User("Michel", 40, 85);
//        userList.add(user1);
//        userList.add(user2);
//        userList.add(user3);
//    }

//    /**
//     * Thread which manages outcoming messages
//     */
//    public void initOutcomingMessageThread() {
//
//        t3 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    System.out.println("Thread 3 lancé");
//                    if (threadSuspended) {
//                        synchronized (this) {
//                            while (threadSuspended) {
//                                try {
//                                    wait();
//                                } catch (InterruptedException ex) {
//                                    Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
//                                }
//                            }
//                        }
//
//                    }
//
//                    sendMessage(outComingMessage);
//                    this.stop();
//                }
//            }
//
//            public synchronized void start() {
//                if (t3 == null) {
//                    t3 = new Thread(this);
//                    threadSuspended = false;
//                    System.out.println("thread 3 en démarre");
//                    this.start();
//                } else {
//                    if (threadSuspended) {
//                        System.out.println("thread graphique en redémarre");
//                        threadSuspended = false;
//                        notify();
//                    }
//                }
//            }
//
//            public synchronized void stop() {
//                System.out.println("thread 3 en pause");
//                threadSuspended = true;
//            }
//        });
//
//    }
//
//    /**
//     * Thread which manages incoming messages, set coordinates, and retrieve
//     * userslist
//     */
//    public void initInComingMessageThread() {
//        t4 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        System.out.println("Thread 4 lancé");
//                        System.out.println("Récupération des messages entrants");
//                        Thread.sleep(500);
//                        if (threadSuspended) {
//                            synchronized (this) {
//                                while (threadSuspended) {
//                                    try {
//                                        wait();
//                                    } catch (InterruptedException ex) {
//                                        Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
//                                    }
//                                }
//                            }
//
//                        }
//
//                        retrieveMessage(output);
//                        retrieveUsersList();
//                        updateUserCoordinate();
//
//                        this.stop();
//                    } catch (InterruptedException ex) {
//                        Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            }
//
//            public synchronized void start() {
//                if (t4 == null) {
//                    t4 = new Thread(this);
//                    threadSuspended = false;
//                    System.out.println("thread 4 en démarre");
//                    this.start();
//                } else {
//                    if (threadSuspended) {
//                        System.out.println("thread graphique en redémarre");
//                        threadSuspended = false;
//                        notify();
//                    }
//                }
//            }
//
//            public synchronized void stop() {
//                System.out.println("thread 4 en pause");
//                threadSuspended = true;
//            }
//
//        });
//
//    }

    /**
     * getterJustConnected
     *
     * @return
     */
    public Boolean getJustConnected() {
        return justConnected;
    }

    /**
     * return the current user
     *
     * @return
     */
    public User getCurrentUser() {
        return currentUser;

    }

    /**
     * getter message instance
     *
     * @return
     */
    public Message getMessage() {
        return message;
    }

    /**
     * add the current user in user list
     */
    public void addCurrentUser() {
        userList.add(this.currentUser);

    }

    /**
     * getter user list
     *
     * @return
     */
    public ArrayList<User> getUserList() {
        return userList;
    }

    /**
     * boolean properties to check if the user list connected is up to date.
     *
     * @return
     */
    public boolean isListUpdated() {
        return listUpdated;
    }

    /**
     * setter list updated
     *
     * @param listUpdated
     */
    public void setListUpdated(boolean listUpdated) {
        this.listUpdated = listUpdated;
    }

    /**
     * getter give the user
     *
     * @return
     */
    public User getSendedMessageUser() {
        return sendedMessageUser;
    }

    /**
     * return isThreadSuspended
     *
     * @return
     */
    public boolean isThreadSuspended() {
        return threadSuspended;
    }

//    /**
//     * Thread implements
//     */
//    @Override
//    public void run() {
//        System.out.println("Connexion en cours...");
//        connection = new Connection();
//        socket = connection.getSocket();
//        try {
//            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            output = new PrintWriter(socket.getOutputStream());
//
//            System.out.println("Connexion au serveur réussie");
//
//            //launch thread
//            while (true) {
//                Thread.sleep(2000);
//                if (!justConnected) {
//                    initOutcomingMessageThread();
//                    t3.start();
//                    initInComingMessageThread();
//                    t4.start();
//                }
//
//            }
//
//        } catch (IOException ex) {
//            Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }

}
