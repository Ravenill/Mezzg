package Server;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server
{
    static final int PORT = 4021;
    int uniqueIDForClient;

    private ArrayList<ClientThread> clientThreadList;
    private SimpleDateFormat date;
    private int port;

    private boolean run;

    public Server()
    {
        clientThreadList = new ArrayList<ClientThread>();
        date = new SimpleDateFormat("HH:mm:ss");
        port = PORT;
        run = false;
    }

    public void start()
    {
        run = true;

        try
        {
            ServerSocket server_socket = new ServerSocket(port);

            while (run)
            {

            }
        }
        catch (IOException error)
        {

        }
    }

    public void addClientThread()
    {

    }

}
