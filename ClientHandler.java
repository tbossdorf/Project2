package Project2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

import javax.xml.crypto.Data;

public class ClientHandler implements Runnable{
    
    private Socket tcpSocket;
    private DatagramSocket udpSocket;
    private int clientID;
    

    public ClientHandler(Socket tcpSocket, DatagramSocket udpSocket, int clientID) throws IOException
    {
        this.tcpSocket = tcpSocket;
        this.clientID = clientID;
        this.udpSocket = udpSocket;
        OutputStream output = tcpSocket.getOutputStream();

    }

    public Socket getTCPSocket()
    {
        return tcpSocket;
    }

    public DatagramSocket getUDPSocket()
    {
        return udpSocket;
    }

    public int getClientID()
    {
        return clientID;
    }

    @Override
    public void run()
    {

    }

    public String readResponse() throws IOException
    {
        String response = "empty";
        BufferedReader input = new BufferedReader(new java.io.InputStreamReader(tcpSocket.getInputStream())); 
        //listens for a response for client

        //we know a response will be coming, so this works here
        while((response = input.readLine()) != null)
        {
            if(!response.equals("empty"))
            {
                return response;
                //when we notice a response, we return it
            }
        }

        return response;
    }

}
