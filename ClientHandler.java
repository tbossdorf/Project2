
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class ClientHandler implements Runnable{
    
    //initializing
    private Socket socket; //represents the socket connection with the client
    private ObjectOutputStream outStream; //sends data to client
    private DataInputStream inStream; //receives data from client
    private int correct = -1; //holds the correct answer to the question
    private final int clientID; //identifies the client
    private final BlockingQueue<Poll> queue; //a blocking queue that handles polls
    private boolean pollPressed = true; //indicates if client has pressed the poll

    //takes three parameters
    public ClientHandler(Socket socket, int clientID, BlockingQueue<Poll> queue) throws IOException
    {
        //initializes
        this.socket = socket;
        this.clientID = clientID;
        this.queue = queue;
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
    void sendQuestions (int questionNum) throws IOException{
        //constructs a file path a scanner
        String filePath = "src/Questions/question" + questionNum + ".txt";
        File file = new File(filePath);
        //reads questions out of a file using
        try(Scanner scanner = new Scanner(file)){
            //indicates data being sent is a file
            String type = "File";
            //writes the line to the output stream and sends it to client
            outStream.writeObject(type);
            //sends questionNum as an interger 
            outStream.writeInt(questionNum);
            int counter = 0;
            while (counter < 5 && scanner.hasNextLine()){
                String line = scanner.nextLine();
                outStream.writeObject(line);
                counter++;
            }
            if (scanner.hasNextInt()){
                //sets correct answer integer being read to correct variable
                correct = scanner.nextInt();
                outStream.writeInt(correct);
            }
            //flushes output stream to ensure all data is sent
            outStream.flush();
        }
    }

    //handles client responses
    private void clientResponse() throws IOException {
        int questionNum = 1;
        //runs in an infinite loop
        while (true){ //always listening to client
            //reads boolean value from input stream
            pollPressed = inStream.readBoolean();
            System.out.println("Client " + clientID + " pressed Poll button:" + pollPressed);
            
            if(pollPressed){
                //used handlePoll
                handlePoll(questionNum);
            }

            //uses handleAnswer
            handleAnswer(questionNum);

            //increments to handle multiple questions
            questionNum++;
        }

    }

    //when client presses poll button
    private void handlePoll (int questionNum) throws IOException{
        //creates a new poll object with clientID and questionNum
        queue.add(new Poll(this.clientID, questionNum)); //adds poll object to queue

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
    private void handleAnswer(int questionNum) throws IOException{
        //if client is at front of queue and answer is available
        if (!queue.isEmpty() && queue.peek().getID() == this.clientID) {
            int answer = inStream.readInt(); //read answer
            //prints clients chosen answer and correct answer
            System.out.println("Answer chosen by client " + this.clientID + ": " + answer + ". Correct Answer: " + correct);

            //calculates clients score
            int score = (answer == correct) ? 10 : -10;
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
            sendQuestions(1);
            sendID();
            clientResponse();;
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

}
