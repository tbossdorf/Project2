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
import java.util.Comparator;
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

    private List<ClientHandler> clientHandlers;
    private ExecutorService executor;
    private static final int TCP_PORT = 1234;
    private static final int UDP_PORT = 4321;
    private ServerSocket serverSocket;
    private DatagramSocket datagramSocket;
    private boolean running;
    private ArrayList<Integer> nums;
    private Queue waitQueue;
    private boolean gameRunning = false;
    private ArrayList<String> clientScores = new ArrayList<>();
    private int currentQuestion = 1;

    public Server(){
        clientHandlers = Collections.synchronizedList(new ArrayList<>());
        executor = Executors.newCachedThreadPool();
        running = false;
        nums = new ArrayList<>();
        waitQueue = new Queue();

    }

    public void start() {
        for(int i = 1; i < 100; i++){
            nums.add(i);
        }
        Collections.shuffle(nums);


        

        try {
            serverSocket = new ServerSocket(TCP_PORT);
            datagramSocket = new DatagramSocket(UDP_PORT);

            System.out.println("TCP Server listening on port " + TCP_PORT);
            System.out.println("UDP Server listening on port " + UDP_PORT);
            printIP();
            //Start TCP server thread
            executor.execute(this::runTCPServer);

            //Start UDP server
            //runUDPServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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
        Server server = new Server();
        server.start();

        // Scanner scanner = new Scanner(System.in); 
        // if(scanner.nextLine().equals("Start")){
        //     server.running = true;
        // }
        // scanner.close();
        
    }



    // private void runUDPServer(){
    //     try {
    //         System.out.println("UDP Server started");
    //         byte[] packetBuffer = new byte[2024];
    //         while (true){
    //             // DatagramPacket packet = new DatagramPacket(packetBuffer, packetBuffer.length);
    //             // datagramSocket.receive(packet);
    //             // System.out.println("Received UDP packet");
    //             // String receivedPacket = new String(packet.getData()).trim();
    //             // System.out.println(receivedPacket + " from ");
    //         }
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }



    private void runTCPServer(){
        try {
            System.out.println("TCP Server started");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                ClientHandler clientHandler = new ClientHandler(socket, nums.remove(0), waitQueue.getQueue(), datagramSocket);
                clientHandlers.add(clientHandler);
                Scanner scanner = new Scanner(System.in); 
                if(scanner.nextLine().equals("Start")){
                    gameRunning = true;
                }

                if(gameRunning){
                    for(ClientHandler ch : clientHandlers){
                        executor.execute(ch);
                        ch.sendQuestions(currentQuestion);
                        if(ch.questionAnswered()){
                            currentQuestion++;
                        }
                    }
                    // if(scanner.nextLine().equals("Next")){
                    //     currentQuestion++;
                    // }
                       
                }else if(!gameRunning && scanner.nextLine().equals("End")){
                    for(ClientHandler ch : clientHandlers){
                        String names = ch.getClient() + ":" + ch.getScore();
                        clientScores.add(names);
                    }

                    Collections.sort(clientScores, new Comparator<String>() {
                        @Override
                        public int compare(String s1, String s2) {
                            int score1 = Integer.parseInt(s1.split(":")[1]);
                            int score2 = Integer.parseInt(s2.split(":")[1]);
                            return Integer.compare(score1, score2);
                        }
                    });

                    for(String s : clientScores){
                        System.out.println(s);
                    }
                    scanner.close();
                    break;



                }

                

                
                
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    
}
