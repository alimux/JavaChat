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
public class ChatManager extends ListenableModel implements Runnable {

    private PrintWriter output = null;
    private BufferedReader input = null;
    private Connection connection;
    private Socket socket;
    private String outComingMessage;
    private Boolean justConnected = true;
    private ArrayList<User> userList;
    private User currentUser;
    private User sendedMessageUser;
    private Message message;
    private Thread t2, t3, t4;
    private volatile boolean threadSuspended;
    private boolean listUpdated = true;

    /**
     * constructor
     *
     */
    public ChatManager() {
        userList = new ArrayList<>();
        this.message = new Message();

        System.out.println("Démarrage du thread 2");
        t2 = new Thread(this);
        t2.start();

    }

    public void test() {
        User user1 = new User("Pierre", 210, 100);
        User user2 = new User("Jacquie", 120, 30);
        User user3 = new User("Michel", 40, 85);
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
    }

    /**
     * Thread which manages outcoming messages
     */
    public void initOutcomingMessageThread() {

        t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("Thread 3 lancé");
                    if (threadSuspended) {
                        synchronized (this) {
                            while (threadSuspended) {
                                try {
                                    wait();
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }

                    }

                    sendMessage(outComingMessage);
                    this.stop();
                }
            }

            public synchronized void start() {
                if (t3 == null) {
                    t3 = new Thread(this);
                    threadSuspended = false;
                    System.out.println("thread 3 en démarre");
                    this.start();
                } else {
                    if (threadSuspended) {
                        System.out.println("thread graphique en redémarre");
                        threadSuspended = false;
                        notify();
                    }
                }
            }

            public synchronized void stop() {
                System.out.println("thread 3 en pause");
                threadSuspended = true;
            }
        });

    }

    /**
     * Thread which manages incoming messages, set coordinates, and retrieve
     * userslist
     */
    public void initInComingMessageThread() {
        t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println("Thread 4 lancé");
                        System.out.println("Récupération des messages entrants");
                        Thread.sleep(500);
                        if (threadSuspended) {
                            synchronized (this) {
                                while (threadSuspended) {
                                    try {
                                        wait();
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }

                        }

                        retrieveMessage(output);
                        retrieveUsersList();
                        updateUserCoordinate();

                        this.stop();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            public synchronized void start() {
                if (t4 == null) {
                    t4 = new Thread(this);
                    threadSuspended = false;
                    System.out.println("thread 4 en démarre");
                    this.start();
                } else {
                    if (threadSuspended) {
                        System.out.println("thread graphique en redémarre");
                        threadSuspended = false;
                        notify();
                    }
                }
            }

            public synchronized void stop() {
                System.out.println("thread 4 en pause");
                threadSuspended = true;
            }

        });

    }

    /**
     * Method which is called on login, send login and position to server
     *
     * @param loginName
     * @param x
     * @param y
     */
    public void login(String loginName, int x, int y) {
        if (justConnected) {

            //justConnected = false;
            currentUser = new User(loginName, x, y);
            addCurrentUser();

            //send login to server
            if (output != null) {
                String login = loginName + "," + x + "," + y;
                output.println("LOGIN");
                output.println(login);
                output.flush();
                test();
                fireChanged();
                justConnected = false;
                System.out.println(loginName + " vient de se connecter");
            }
        }
    }

    /**
     * method wich update the coordinates of the current user
     */
    public void updateUserCoordinate() {
        System.out.println("update coordonnées : x:" + currentUser.getxPosition() + " y:" + currentUser.getyPosition());
        if (output != null) {
            output.println("SET_COORDINATE");
            output.println(currentUser.getxPosition());
            output.println(currentUser.getyPosition());
            output.flush();
        }

    }

    /**
     * send message method
     *
     * @param ocMessage
     */
    public void sendMessage(String ocMessage) {
        System.out.println("Chat manager -> Envoi du message : " + message);
        outComingMessage = ocMessage;
        message.setMessageOutComing(outComingMessage);
        output.println("SET_MSG");
        output.println(outComingMessage);
        output.flush();

        fireChanged();

    }

    /**
     * retrieve message method
     *
     * @param out
     */
    public void retrieveMessage(PrintWriter out) {

        System.out.println("reception d'un message");
        output.println("GET_MSG");
        output.flush();
        try {
            String incomingMessage = input.readLine();
            String[] splitMessage = incomingMessage.split("<END/>");
            //retrieve the user who sended message
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).getUserName().equals(splitMessage[0])) {
                    sendedMessageUser = userList.get(i);
                }
            }
            message.setInComingMessage(splitMessage[1]);
            fireChanged();

        } catch (IOException ex) {
            Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Method which retrieves the list of the Tchatters
     *
     * @return ArrayList
     */
    public ArrayList<User> retrieveUsersList() {

        System.out.println("reception de la liste des utilisateurs");
        output.println("GET_USERS_LIST");
        output.flush();
        try {
            //retrievelist
            String userListIncoming = input.readLine();
            //parsing list
            String[] userSplit = userListIncoming.split(";");
            for (int i = 0; i < userSplit.length; i++) {
                String[] user = userSplit[i].split(",");
                for (int j = 0; j < user.length; j++) {
                    if (user[0] != currentUser.getUserName()) {
                        userList.add(new User(user[0], Integer.valueOf(user[1]), Integer.valueOf(user[2])));
                    }
                }
            }
            return userList;

        } catch (IOException ex) {
            Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

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

    /**
     * Thread implements
     */
    @Override
    public void run() {
        System.out.println("Connexion en cours...");
        connection = new Connection();
        socket = connection.getSocket();
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream());

            System.out.println("Connexion au serveur réussie");

            //launch thread
            while (true) {
                Thread.sleep(2000);
                if (!justConnected) {
                    initOutcomingMessageThread();
                    t3.start();
                    initInComingMessageThread();
                    t4.start();
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
