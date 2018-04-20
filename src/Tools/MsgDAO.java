package Tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MsgDAO
{
    private final static String DBURL = "jdbc:oracle:thin:@127.0.0.1:1521:xe";
    private final static String DBUSER = "JAVA";
    private final static String DBPASS = "123";
    private final static String DBDRIVER = "oracle.jdbc.OracleDriver";

    private Connection connection;
    private Statement statement;
    private String query;

    private ParserSQL parserSQL;

    public MsgDAO()
    {
        parserSQL = new ParserSQL();
    }

    public void save(String user, String time, String msg)
    {
        query = parserSQL.createQuery(user, time, msg);

        try
        {
            Class.forName(DBDRIVER).newInstance();
            connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
            statement = connection.createStatement();
            statement.executeUpdate(query);

            statement.close();
            connection.close();
        }
        catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException | SQLException error)
        {
            error.printStackTrace();
        }
    }
}
