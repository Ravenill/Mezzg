package Tools;

public class ParserSQL
{
    public String createQuery(String user, String time, String msg) {
        String query = "INSERT INTO MSG VALUES (NULL, '" + user + "', '" + time + "', '" + msg +"')";
        return query;
    }
}
