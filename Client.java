package Project2;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {


    private String currentIP;
    private Socket socket;
    private DatagramSocket udpSocket;


    public static void main(String[] args)
    {
        String currentIP = args[0];
        //Scanner scanner = new Scanner(System.in);
        //ClientWindow window = new ClientWindow();
        
        //Now, we can pass it our IP from the command line. The IP will be printed on the servers console, which we can copy
        //and enter into the client console to actually connect to our server
        if(currentIP != null)
        {
            Scanner scanner = new Scanner(System.in);
            try
            {
                Socket socket = new Socket(currentIP, 1234);
                DatagramSocket udpSocket = new DatagramSocket(4321);
                System.out.println("Connected to server");
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // Create a PrintWriter
                while(true)
                {
                    
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
                        if(message.equals("Buzz"))
                        {
                            sendBuzz(udpSocket, currentIP);
                        }
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




    private void run(String currentIP){
        
    }


    public static void sendBuzz(DatagramSocket udpSocket, String currentIP) throws IOException
    {
        String response = "Buzz";
        byte[] buffer = response.getBytes();
        InetAddress serverAddress = InetAddress.getByName(currentIP);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, 4321);
        udpSocket.send(packet);
    }
    
}
