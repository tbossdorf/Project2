package Project2;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {


    public static int clientId;
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
                DatagramSocket udpSocket = new DatagramSocket(1235);
                System.out.println("Connected to server");
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // Create a PrintWriter
                Scanner scanner = new Scanner(System.in);
                while(true)
                {
                    System.out.println("Enter a message to send to the server:");
                    System.out.println("Enter 'exit' to close the connection");
                    String message = scanner.nextLine();
                    if(message.equals("exit"))
                    {
                        socket.close();
                        udpSocket.close();
                        break;
                    }
                    else if(message.equals(null))
                    {
                        
                        System.out.println("Invalid input, please try again.");
                    }
                    else
                    {
                        byte[] buffer = message.getBytes();
                        InetAddress serverAddress = InetAddress.getByName(currentIP);
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, 1235);
                        udpSocket.send(packet);
                    }
                }
                scanner.close();
            }catch(IOException e)
            {
                System.out.println("Issue with connecting to server: " + e.getMessage());
            }
        }else
        {
            System.out.println("Please enter an IP into the command line as an arg to connect to the server.");
        }

    }


    public int getId(){return clientId;}
    
}
