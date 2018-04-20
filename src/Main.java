import Client.Client;
import Server.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Scanner;

public class Main extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = new Group();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        //launch(args);
        Scanner scan = new Scanner(System.in);

        System.out.println("Run: \n");
        System.out.println("[1] Server: \n");
        System.out.println("[2] Client: \n");

        String opt = scan.nextLine();
        switch (opt)
        {
            case "1":
                clearScreen();
                runServer();
                break;
            case "2":
                System.out.println("Username: \n");
                String name = scan.nextLine();
                clearScreen();
                runClient(name);
                break;
        }

    }

    public static void runServer()
    {
        Server server = new Server();
        server.start();
    }

    public static void clearScreen()
    {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void runClient(String name)
    {
        Client client = new Client(name);
        if (!client.createClient())
            return;

        client.run();
        client.disconnect();
    }
}
