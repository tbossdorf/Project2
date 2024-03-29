package Project2;

import java.io.IOException;
import java.net.Socket;

public class Client {


    public static void main(String[] args)
    {
        //Now, we can pass it our IP from the command line. The IP will be printed on the servers console, which we can copy
        //and enter into the client console to actually connect to our server
        if(args.length == 1)
        {
            String currentIP = args[0];
            try
            {
                Socket socket = new Socket(currentIP, 1234);
                System.out.println("Connected to server");
            }catch(IOException e)
            {
                System.out.println("Issue with connecting to server: " + e.getMessage());
            }
        }else
        {
            System.out.println("Please enter an IP into the command line as an arg to connect to the server.");
        }

    }
    
}
