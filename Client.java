package Project2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
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
    private ObjectOutputStream outStream; //sends data to clientHandler
    private DataInputStream inStream; //receives data from clientHandler
    private BufferedReader reader;



    public Client(String currentIP)
    {
        this.currentIP = currentIP;
        try{
            socket = new Socket(currentIP, 1234);
            udpSocket = new DatagramSocket(4321);
        }
        catch(IOException e)
        {
            System.out.println("Issue with connecting to server: " + e.getMessage());
        }
        
    }

    public void run()
    {
        //Scanner scanner = new Scanner(System.in);
        //ClientWindow window = new ClientWindow();
        
        //Now, we can pass it our IP from the command line. The IP will be printed on the servers console, which we can copy
        //and enter into the client console to actually connect to our server
        if(currentIP != null)
        {
            Scanner scanner = new Scanner(System.in);
            try
            {
                //Socket socket = new Socket(currentIP, 1234);
                //DatagramSocket udpSocket = new DatagramSocket(4321);
                outStream = new ObjectOutputStream(socket.getOutputStream());
                inStream = new DataInputStream(socket.getInputStream());
                reader = new BufferedReader(new InputStreamReader(inStream));
                System.out.println("Connected to server");
                while(true)
                {
                    String ack = reader.readLine();
                    if(ack == "ack"){
                        System.out.println("acknowledgment received");
                    }

                }
            }catch(IOException e)
            {
                System.out.println("Issue with connecting to server: " + e.getMessage());
            }
        }else
        {
            System.out.println("Please enter an IP into the command line as an arg to connect to the server.");
        }

    }



    public void sendBuzz(DatagramSocket udpSocket, String currentIP) throws IOException
    {
        String response = "Buzz";
        byte[] buffer = response.getBytes();
        InetAddress serverAddress = InetAddress.getByName(currentIP);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, 4321);
        udpSocket.send(packet);
    }

    public DatagramSocket getUdpSocket()
    {
        return udpSocket;
    }

    public String getCurrentIP()
    {
        return currentIP;
    }

    public void sendAnswer(int answer, ObjectOutputStream outStream) throws IOException
    {
        outStream.writeInt(answer);
    }


    public ObjectOutputStream getOutStream()
    {
        return outStream;
    }

    public BufferedReader getReader()
    {
        return reader;
    }



    
}
