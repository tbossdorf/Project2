package Project2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
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
    private ObjectInputStream inStream; //receives data from clientHandler
    private BufferedReader reader;
    private String response = "";
    private String windowInput;
    private int selectedAnwser;
    
    



    public Client(String currentIP)
    {
        this.currentIP = currentIP;
        try{
            socket = new Socket(currentIP, 1234);
            udpSocket = new DatagramSocket(4321);
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());
            //reader = new BufferedReader(new java.io.InputStreamReader(inStream));
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
            
                //Socket socket = new Socket(currentIP, 1234);
                //DatagramSocket udpSocket = new DatagramSocket(4321);
                
                
                
                System.out.println("Connected to server");
                while(true)
                {

                    if(getWindowInput() == "Buzz")
                    {
                        try{
                            sendBuzz(getUdpSocket(), getCurrentIP());
                        }catch(IOException e)
                        {
                            System.out.println("Error sending buzz to server");
                        }
                    }
                    else if(getWindowInput() != null && getWindowInput().contains("@"))
                    {
                        System.out.println("Answer recieved");
                        try{
                            sendAnswer(getWindowInput(), getOutStream());
                            setWindowInput(null);
                        }catch(IOException e)
                        {
                            System.out.println("Error sending answer to server");
                        }
                    }



                    String serverResponse = "";
                    try {
                        inStream.read();
                        serverResponse = (String) inStream.readObject();
                    } catch (IOException e) {
                        //System.out.println("Error reading from input stream");
                        //e.printStackTrace();
                    } 
                    catch (ClassNotFoundException e) {
                        //System.out.println("Class not found when reading from input stream");
                        //e.printStackTrace();
                    }

                
                    if(serverResponse.equals("ack")){
                        System.out.println("ack recieved");
                        this.response = "ack";
                        break;
                    }else if(serverResponse.equals("nack")){
                        System.out.println("nack recieved");
                        this.response = "nack";
                        break;
                    }





                    // try{
                    //    String response = (String) inStream.readObject();
                    //     if(response == "ack")
                    //     {
                    //         System.out.println("ack recieved");
                    //     }
                    //     else if(response.equals("nack")){
                    //         System.out.println("nack recieved");
                    //     }
                    //     else{
                    //         //System.out.println("Server response: " + getServerResponse());
                    //     }
                    // }catch(IOException e)
                    // {
                    //    // System.out.println("Error reading from input stream");
                    // }catch(ClassNotFoundException e)
                    // {
                    //     //System.out.println("Class not found when reading from input stream");
                    //     //e.printStackTrace();
                    // }
                    // //System.out.println("Server response: " + this.response);

                    
                    
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


    public Socket getSocket(){
        return socket;
    }

    public String getCurrentIP()
    {
        return currentIP;
    }


    public void updateScore(int score)
    {
        try{
            outStream.writeUTF("Score: " + score);
            outStream.flush();
        }catch(IOException e){
            System.out.println("Error updating score");
        }
    }


    public void sendAnswer(String answer, ObjectOutputStream outStream) throws IOException
    {
        outStream.writeUTF(answer);
        outStream.flush();
    }


    public ObjectOutputStream getOutStream()
    {
        return outStream;
    }

    public BufferedReader getReader()
    {
        return reader;
    }

    // public ObjectInputStream getInStream()
    // {
    //     return inStream;
    // }

    public String getServerResponse(){
    
        return response;
    }


    public void setWindowInput(String input)
    {
        this.windowInput = input;
    }

    private String getWindowInput()
    {
        return windowInput;
    }



    
}
