import java.io.*;

public class Message implements Serializable
{
    static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2;
    private int type;
    private String text;

    Message(int type, String text)
    {
        this.type = type;
        this.text = text;
    }

    int getType()
    {
        return type;
    }

    String getText()
    {
        return text;
    }
}

