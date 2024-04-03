package Project2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

public class ClientHandler implements Runnable{
    
    private Socket socket;
    private DatagramPacket packet;
    private PrintWriter out;

    public ClientHandler(Socket socket, DatagramPacket packet) throws IOException
    {
        this.socket = socket;
        this.packet = packet;
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public Socket getSocket()
    {
        return socket;
    }

    @Override
    public void run()
    {

    }

    public String readResponse() throws IOException
    {
        String response = "empty";
        BufferedReader input = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream())); 
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
