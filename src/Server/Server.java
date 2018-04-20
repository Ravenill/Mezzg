package Server;

import Tools.Message;
import Tools.MsgDAO;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server
{
    private static final int PORT = 4021;
    private int uniqueIDForClient;

    private ArrayList<ClientThread> clientThreadList;
    private SimpleDateFormat date;
    private int port;

    private MsgDAO msgDAO;

    private boolean run;

    public Server()
    {
        clientThreadList = new ArrayList<ClientThread>();
        date = new SimpleDateFormat("HH:mm:ss");
        port = PORT;
        run = false;
        msgDAO = new MsgDAO();
    }

    public void start()
    {
        run = true;

        try
        {
            ServerSocket server_socket = new ServerSocket(port);

            while (run)
            {
                System.out.println("Waiting for connetction on port: " + port + ".\n");
                Socket socket = server_socket.accept();

                if (!run)
                    break;

                addClientThread(socket);
            }

            stop(server_socket);
        }
        catch (IOException error)
        {
            System.out.println(date.format(new Date()) + " Problem with new serverSocket: " + error + "\n");
        }
    }

    private void addClientThread(Socket socket)
    {
        ClientThread new_thread = new ClientThread(socket);
        clientThreadList.add(new_thread);
        new_thread.start();
    }

    private void stop(ServerSocket serverSocket)
    {
        try
        {
            serverSocket.close();

            for (ClientThread client : clientThreadList)
            {
                try
                {
                    client.input.close();
                    client.output.close();
                    client.socket.close();
                }
                catch (IOException error)
                {
                    System.out.println("Cannot close streams: " + error);
                }
            }
        }
        catch (Exception error)
        {
            System.out.println("Problem with closing server and clients: " + error);
        }
    }

    private synchronized void sendToAllClients(String user, String msg)
    {
        String time = date.format(new Date());
        String msg_to_send = "[" + time + "] " + user + ": " + msg + "\n";

        System.out.println(msg_to_send);

        msgDAO.save(user, time, msg);

        //reverse, coz someone can DC
        for (int i = clientThreadList.size() - 1; i >= 0; i--)
        {
            ClientThread client = clientThreadList.get(i);
            if (client.isActive())
                client.sendMessage(msg_to_send);
            else
                remove(client.id);

        }
    }

    synchronized void remove(int id)
    {
        for (ClientThread client : clientThreadList)
        {
            if (client.id == id)
            {
                clientThreadList.remove(client);
                return;
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class ClientThread extends Thread
    {
        private Socket socket;
        private ObjectInputStream input;
        private ObjectOutputStream output;

        int id;
        private String userName;

        private Message message;
        boolean loop;

        ClientThread(Socket socket)
        {
            id = uniqueIDForClient++;

            try
            {
                this.socket = socket;
                output = new ObjectOutputStream(socket.getOutputStream());
                input = new ObjectInputStream(socket.getInputStream());
                userName = (String) input.readObject();
                System.out.println(userName + " connected.\n");
            }
            catch (IOException error)
            {
                System.out.println("Problem with user's connecting. Cannot create new input/output stream: " + error);
            }
            catch (ClassNotFoundException error)
            {
                System.out.println("Problem with user's name. Cannot read username: " + error);
            }
        }

        public void run()
        {
            loop = true;

            while (loop)
            {
                try
                {
                    message = (Message) input.readObject();
                }
                catch (IOException error)
                {
                    System.out.println("User: " + userName + ". Problem with reading stream: " + error);
                }
                catch (ClassNotFoundException error)
                {
                    System.out.println("User: " + userName + ". Problem with reading message: " + error);
                }

                handleMessage();
            }

            close();
        }

        private void handleMessage()
        {
            String text = message.getText();

            switch (message.getType())
            {
                case Message.MESSAGE:
                    sendToAllClients(userName, text);
                    break;
                case Message.LOGOUT:
                    System.out.println(userName + " disconnected.\n");
                    loop = false;
                    remove(id);
                    break;
            }
        }

        private void close()
        {
            try
            {
                if (output != null)
                    output.close();
            }
            catch (Exception error)
            {
                System.out.println(userName + ": problem with closing output stream\n");
            }
            try
            {
                if (input != null)
                    input.close();
            }
            catch (Exception error)
            {
                System.out.println(userName + ": problem with closing input stream\n");
            }
            try
            {
                if (socket != null)
                    socket.close();
            }
            catch (Exception error)
            {
                System.out.println(userName + ": problem with closing socket\n");
            }
        }

        private boolean isActive()
        {
            if (!socket.isConnected())
            {
                close();
                return false;
            }
            return true;
        }

        private void sendMessage(String msg)
        {
            try
            {
                output.writeObject(msg);
            }
            catch (IOException error)
            {
                System.out.println("Error sending message to " + userName + "\n");
            }
        }
    }
}
