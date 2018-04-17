package Client;

import Tools.Message;
import java.net.*;
import java.io.*;
import java.util.*;

public class Client
{
    private static final int PORT = 4021;
    private static final String SERVERNAME = "localhost";
    private static final String USERNAME = "Anon";

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket socket;

    private String server;
    private String username;
    private int port;

    Client ()
    {
        this.server = SERVERNAME;
        this.port = PORT;
        this.username = USERNAME;
    }

    public boolean start()
    {
        try
        {
            socket = new Socket(server, port);
        }
        catch (Exception error)
        {
            System.out.println("Error connectiong to server:" + error);
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        System.out.println(msg);

        /* Creating both Data Stream */
        try
        {
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException error)
        {
            System.out.println("Exception creating new Input/output Streams: " + error);
            return false;
        }

        new ListenFromServer().start();

        try
        {
            output.writeObject(username);
        }
        catch (IOException error)
        {
            System.out.println("Exception doing login : " + error);
            disconnect();
            return false;
        }
        // success we inform the caller that it worked
        return true;
    }

    /*
     * To send a message to the server
     */
    void sendMessage(Message msg)
    {
        try
        {
            output.writeObject(msg);
        }
        catch (IOException e)
        {
            System.out.println("Exception writing to server: " + e);
        }
    }

    /*
     * When something goes wrong
     * Close the Input/Output streams and disconnect not much to do in the catch clause
     */
    private void disconnect()
    {
        try
        {
            if (input != null) input.close();
        }
        catch (Exception e)
        {
        } // not much else I can do
        try
        {
            if (output != null) output.close();
        }
        catch (Exception e)
        {
        } // not much else I can do
        try
        {
            if (socket != null) socket.close();
        }
        catch (Exception e)
        {
        } // not much else I can do
    }

    public static void main(String[] args)
    {

        Client client = new Client();
        if (!client.start())
            return;

        Scanner scan = new Scanner(System.in);
        while (true)
        {
            System.out.print("> ");
            String msg = scan.nextLine();
            if (msg.equalsIgnoreCase("LOGOUT"))
            {
                client.sendMessage(new Message(Message.LOGOUT, ""));
                break;
            }
            else
            {
                client.sendMessage(new Message(Message.MESSAGE, msg));
            }
        }
        client.disconnect();
    }

    class ListenFromServer extends Thread
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    String msg = (String) input.readObject();
                    System.out.println(msg);
                    System.out.print("> ");
                }
                catch (IOException e)
                {
                    System.out.println("Server has close the connection: " + e);
                    break;
                }
                catch (ClassNotFoundException e2)
                {
                }
            }
        }
    }
}
