package Project2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{
    
    private Socket socket;
    private PrintWriter out;

    public ClientHandler(Socket socket) throws IOException
    {
        this.socket = socket;
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
}
