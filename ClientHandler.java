package Project2;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable{
    
    private Socket socket;
    private ObjectOutputStream outStream;
    private DataInputStream inStream;
    private int correct = -1;
    private final int clientID;



    public ClientHandler(Socket socket) throws IOException
    {
        this.socket = socket;
        this.queue = queue;
    }

    private void initialize() throws IOException {
        outStream = new ObjectOutputStream(socket.getOutStream());
        inStream = new DataInputStream(socket.getInStream());
    }
    
    void sendQuestions (int questionNum) throws IOException{
        String filePath = "src/Questions/question" + questionNUm + ".txt";
        File file = new File(filePath);
        try(Scanner scanner = new Scanner(file)){
            String type = "File";
            outStream.writeObject(type);
            outStream.writeInt(questionNum);
            int counter = 0;
            while (counter < 5 && scanner.hasNextLine()){
                String line = scanner.nextLine();
                outStream.writeObject(line);
                counter++;
            }
            if (scanner.hasNextInt()){
                correct = scanner.nextInt();
            }
            outStream.flush();
        }
    }

    private void clientResponse() throws IOException {
        while (true){
            int questionNum = x;

            pollPressed = inStream.readBoolean();

            System.out.println("Client " + clientID + " pressed Poll button:" + pollPressed);

            if (pollPressed){

                queue.add(new Poll(this.clientID, questionNum));
                
                if (!queue.isEmpty() && queue.peek().getClientID() == this.clientID) {
                    sendAck("ack");
                    System.out.println("Ack to client " + this.clientID);
                } else {
                    sendAck("negative-ack");
                    System.out.println("Negatuve-ack to client " + this.clientID);
                }
            }
    
            if (!queue.isEmpty() && queue.peek().getClientID() == this.clientID) {
                int answer = inStream.readInt(); //read answer
                System.out.println("Answer chosen by client " + this.clientID + ": " + answer + ". Correct Answer: " + correct);
                int score = (answer == correct) ? 10 : -20;
                // outStream.writeObject("Score");
                // outStream.writeInt(score);
                outStream.flush();
            }
            questionNum++;
            outStream.flush();
        }
    }

    public Socket getSocket()
    {
        return socket;
    }

    @Override
    public void run()
    {
        try{
            sendQuestions(1);
        } catch (IOException e){
            e.printStackTrace();
        } finally{
            closeStreams();
        }
    }

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

    public int getClient(){ //get client id
        return this.clientID;
    }

    public void setPressed(boolean pollPressed){
        this.pollPressed = pollPressed;
    }

}
