package Server;

import sun.plugin2.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientThread extends Thread
{
    ObjectInputStream input;
    ObjectOutputStream output;

    int id;
    String userName;
    String dateOfConnecting;

    Message message;

    ClientThread(Socket socket, int uniqueID)
    {
        id = uniqueID;

        try
        {
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

    
}
