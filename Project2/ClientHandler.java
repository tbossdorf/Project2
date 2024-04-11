package Project2;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class ClientHandler implements Runnable{
    
    //initializing
    private Socket socket; //represents the tcp socket connection with the client
    private DatagramSocket udpSocket; //represents the udp socket connection with the client
    private ObjectOutputStream outStream; //sends data to client
    private DataInputStream inStream; //receives data from client
    private int correct = -1; //holds the correct answer to the question
    private final int clientID; //identifies the client
    private final BlockingQueue<Poll> queue; //a blocking queue that handles polls
    private boolean pollPressed = false; //indicates if client has pressed the poll
    private int answerResult = 0; //indicates if client has pressed the answer
    private int score;
    private boolean questionAnswered = false;
    private int questionNum = 1;

    
    //takes three parameters
    public ClientHandler(Socket socket, int clientID, BlockingQueue<Poll> queue, DatagramSocket udpSocket) throws IOException
    {
        //initializes
        this.socket = socket;
        this.clientID = clientID;
        this.queue = queue;
        this.udpSocket = udpSocket;
    }

    //sends ack to client 
    private void sendAck(String ack) throws IOException{
        //writes acknowledgment message to output stream
        outStream.writeObject(ack);
        outStream.flush();
        //indicates and acknowledgment has been scent and what the acknowledgment was
        System.out.println("Sent acknowledgment to client " + this.clientID + ":" + ack);
    }

    //sends client id to client
    private void sendID() throws IOException{
        outStream.writeInt(this.clientID);
        outStream.flush();
    }

    //initializies input and output streams
    private void initialize() throws IOException {
        outStream = new ObjectOutputStream(socket.getOutputStream());
        inStream = new DataInputStream(socket.getInputStream());
    }
    
    //sends questions to client over output stream
    public void sendQuestions (int questionNum) throws IOException{
        if(this.questionNum != questionNum){
            pollPressed = false;
        }
        this.questionNum = questionNum;
        //constructs a file path a scanner
        String filePath = "Project2/Project2/Questions/Question" + questionNum + ".txt";
        File file = new File(filePath);
        String[] questions = new String[6];
        //reads questions out of a file using
        try(Scanner scanner = new Scanner(file)){
            //indicates data being sent is a file
            String type = "File";
            //writes the line to the output stream and sends it to client
            outStream.writeObject(type);
            outStream.flush();
            //sends questionNum as an interger 
            int counter = 0;
            while (counter < 5 && scanner.hasNextLine()){
                String line = scanner.nextLine();
                questions[counter] = line;
                counter++;
            }
            if (scanner.hasNextInt()){
                //sets correct answer integer being read to correct variable
                correct = scanner.nextInt();
                questions[5] = ""+correct;
            }
            outStream.writeObject(questions);
            //flushes output stream to ensure all data is sent
            outStream.flush();
        }
    }


    public int questionResult(){
        return answerResult;
    }
    

    //handles client responses
    private void clientResponse() throws IOException {
        //runs in an infinite loop
        while (true){ //always listening to client
            //reads boolean value from input stream
            //pollPressed = inStream.readBoolean();
            //System.out.println("Client " + clientID + " pressed Poll button:" + pollPressed);
            
            //if client has pressed poll button
            if(!pollPressed){
                System.out.println("Waiting for Buzz from client " + clientID);
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(packet);
                String receivedPacket = new String(packet.getData()).trim();
                if(receivedPacket.equals("Buzz"))
                {
                    pollPressed = true;
                    System.out.println("Buzz received from client " + clientID);
                    handlePoll(questionNum);
                }
            }

            int answer = inStream.readInt();
           
            if(answer == 1 || answer == 2 || answer == 3 || answer == 4){
                handleAnswer(questionNum, answer);
            }
           
            
            // if(inStream.readUTF().substring(0, 6) == "Score:"){
            //     System.out.println("Score received from client " + clientID);
            //     this.score = Integer.parseInt(inStream.readUTF().substring(6, 7));
            //     System.out.println("Score: " + score);
            // }


            // if(pollPressed){
            //     //used handlePoll
            //     handlePoll(questionNum);
            // }


            //uses handleAnswer
            
            


            //increments to handle multiple questions
            //questionNum++;
            // outStream.flush();
        }

    }

    //when client presses poll button
    private void handlePoll (int questionNum) throws IOException{
        //creates a new poll object with clientID and questionNum
        queue.add(new Poll(questionNum, this.clientID)); //adds poll object to queue
        System.out.println("Head client in queue: " + queue.peek().getID() + " Question number: " + queue.peek().getquestionNum());
        //if queue is empty and client is at front sends an ack
        if (!queue.isEmpty() && queue.peek().getID() == this.clientID) {
            sendAck("ack");
            System.out.println("Ack to client " + this.clientID);
        } else {
        //if queue is not empty and client is not at front sends an Negative-ack
            sendAck("negative-ack");
            System.out.println("Negative-ack to client " + this.clientID);
        }
    }
    
    //client has submitted an answer
    private void handleAnswer(int questionNum, int clientAnswer) throws IOException{
        //if client is at front of queue and answer is available
        if (!queue.isEmpty() && queue.peek().getID() == this.clientID) {
            int answer = clientAnswer; //read answer
            //prints clients chosen answer and correct answer
            System.out.println("Answer chosen by client " + this.clientID + ": " + answer + ". Correct Answer: " + correct);
            //calculates clients score
            int score = (answer == correct) ? 10 : -10;
            if(answer == correct){
                answerResult = 1;
            }else{
                answerResult = -1;
            }
            outStream.writeObject("Score");
            outStream.writeInt(score);
            

            //flushes output stream to ensure all data is sent
            outStream.flush();
        }
    }
    //returns socket object stored in Socket
    public Socket getSocket()
    {
        return socket;
    }

    //sends the first set of questions to client
    @Override
    public void run()
    {
        try{
            initialize();
            sendID();
            clientResponse();
        } catch (IOException e){
            e.printStackTrace();
        } finally{
            closeStreams();
        }
    }

    //closes the input and output streams
    private void closeStreams(){
        try {
            if (outStream != null) {
                outStream.close();
            }
            if (inStream != null) {
                inStream.close();
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getClient(){ //gets client id
        return this.clientID;
    }

    public void setPressed(boolean pollPressed){
        //updates the state of pollpressed
        this.pollPressed = pollPressed;
    }


    public int getScore(){
        return score;
    }

}
