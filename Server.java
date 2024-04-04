import java.net.ServerSocket;
import java.net.DatagramSocket;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import java.util.Iterator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.crypto.Data;

import java.net.InetSocketAddress;
public class Server {




    public static void printIP() throws IOException
    {
        Socket s = new Socket();
        s.connect(new InetSocketAddress("google.com", 80));
        String ip = s.getLocalAddress().getHostAddress();
        s.close();
        System.out.println("Current IP is " + ip);
    }


    public static void main(String[] args) {
        //ServerWindow window = new ServerWindow();

        List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());

        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            DatagramSocket datagramSocket = new DatagramSocket(1234);
            System.out.println("Server started");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");

                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        
    }
    
}
