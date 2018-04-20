package Client;

import Tools.Message;

import java.net.*;
import java.io.*;
import java.util.*;

public class Client
{
    private static final int PORT = 4021;
    private static final String SERVER_NAME = "localhost";
    private static final String USERNAME = "Anon";

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket socket;
    private ListenFromServer listener;

    private String server;
    private String username;
    private int port;

    public Client()
    {
        this.server = SERVER_NAME;
        this.port = PORT;
        this.username = USERNAME;
    }

    public Client(String username)
    {
        this.server = SERVER_NAME;
        this.port = PORT;
        this.username = username;
    }

    public boolean createClient()
    {
        if (!connectToServer())
            return false;

        if (!openStreams())
            return false;

        createServerListener();

        if (!sendUsernameToServer())
            return false;

        return true;
    }

    private boolean connectToServer()
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

        String info = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        System.out.println(info);
        return true;
    }

    private boolean openStreams()
    {
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

        return true;
    }

    private void createServerListener()
    {
        listener = new ListenFromServer();
        listener.start();
    }

    private boolean sendUsernameToServer()
    {
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

        return true;
    }

    private void sendMessage(Message msg)
    {
        try
        {
            output.writeObject(msg);
        }
        catch (IOException error)
        {
            System.out.println("Exception writing to server: " + error);
        }
    }

    public void run()
    {
        Scanner scan = new Scanner(System.in);
        while (true)
        {
            System.out.print("> ");
            String msg = scan.nextLine();
            if (msg.equalsIgnoreCase("LOGOUT"))
            {
                sendMessage(new Message(Message.LOGOUT, ""));
                break;
            }
            else
            {
                sendMessage(new Message(Message.MESSAGE, msg));
            }
        }
    }

    public void disconnect()
    {
        try
        {
            if (input != null) input.close();
        }
        catch (Exception error)
        {
            System.out.println("Exception while closing input: " + error);
        }
        try
        {
            if (output != null) output.close();
        }
        catch (Exception error)
        {
            System.out.println("Exception while closing output: " + error);
        }
        try
        {
            if (socket != null) socket.close();
        }
        catch (Exception error)
        {
            System.out.println("Exception while closing socket: " + error);
        }
    }

    class ListenFromServer extends Thread
    {
        @Override
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
                catch (IOException error)
                {
                    System.out.println("Server has close the connection: " + error);
                    break;
                }
                catch (ClassNotFoundException error)
                {
                    System.out.println("Cannot read the message: " + error);
                }
            }
        }
    }
}
