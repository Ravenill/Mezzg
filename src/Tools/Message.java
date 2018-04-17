package Tools;

import java.io.*;

public class Message implements Serializable
{
    public static final int MESSAGE = 0, LOGOUT = 1;
    private int type;
    private String text;

    public Message(int type, String text)
    {
        this.type = type;
        this.text = text;
    }

    public int getType()
    {
        return type;
    }

    public String getText()
    {
        return text;
    }
}

