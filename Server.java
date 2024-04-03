package Project2;

import java.net.ServerSocket;
import java.net.DatagramPacket;
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

/*
 * Sources used:
 * https://stackoverflow.com/questions/69948921/run-running-tcp-and-udp-server-at-the-same-time
 * 
 */


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

        // try {
        //     ServerSocket serverSocket = new ServerSocket(1234);
        //     DatagramSocket udpSocket = new DatagramSocket(1235);
        //     System.out.println("Server started");
        //     printIP();
        //     while (true) {
        //         Socket socket = serverSocket.accept();
        //         System.out.println("Client connected");
        //         ClientHandler clientHandler = new ClientHandler(socket);
        //         clientHandlers.add(clientHandler);
        //         byte[] packetBuffer = new byte[2024];
                
        //         if(clientHandlers.size() >= 1){
        //             ExecutorService executor = Executors.newFixedThreadPool(clientHandlers.size());
        //             while(true){
        //                 DatagramPacket packet = new DatagramPacket(packetBuffer, packetBuffer.length);
        //                 System.out.println("waiting for UDP packet...");
        //                 // Blocks until a packet is received
        //                 udpSocket.receive(packet);
        //                 final String receivedPacket = new String(packet.getData()).trim();
        //                 System.out.println(receivedPacket);
                    
        //             }
                    
        //         }
                
        //     }
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }


        ExecutorService executor = Executors.newFixedThreadPool(2); // Create a thread pool

        // TCP Server
        executor.execute(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(1234);
                System.out.println("TCP Server started");
                printIP();
                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("Client connected");
                    ClientHandler clientHandler = new ClientHandler(socket, null);
                    clientHandlers.add(clientHandler);
                    if(clientHandlers.size() >= 1){
                    
                        while(true){
                            byte[] packetBuffer = new byte[2024];
                            DatagramSocket udpSocket = new DatagramSocket(1235);
                            DatagramPacket packet = new DatagramPacket(packetBuffer, packetBuffer.length);
                            System.out.println("waiting for UDP packet...");
                            // Blocks until a packet is received
                            udpSocket.receive(packet);
                            final String receivedPacket = new String(packet.getData()).trim();
                            System.out.println(receivedPacket);
                        }
                    
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // UDP Server
        // executor.execute(() -> {
        //     try {
        //         DatagramSocket udpSocket = new DatagramSocket(1235);
        //         System.out.println("UDP Server started");
        //         byte[] packetBuffer = new byte[2024];
        //         while (true) {
        //             DatagramPacket packet = new DatagramPacket(packetBuffer, packetBuffer.length);
        //             udpSocket.receive(packet);
        //             System.out.println("Received UDP packet");
        //             ClientHandler clientHandler = new ClientHandler(null, packet);
        //             clientHandlers.add(clientHandler);
        //         }
        //     } catch (IOException e) {
        //         e.printStackTrace();
        //     }
        // });
        
    }
    
}
