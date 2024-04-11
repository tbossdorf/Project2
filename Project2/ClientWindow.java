package Project2;


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.Timer;
import javax.swing.*;

public class ClientWindow implements ActionListener
{
	private JButton poll;
	private JButton submit;
	private JRadioButton options[];
	private ButtonGroup optionsGroup;
	private String text[];
	private JLabel question;
	private JLabel timer;
	private JLabel score;
	private TimerTask clock;
	private int chosen;
	private int theScore;

	private Socket socket; //represents the socket connection with the client
    private ObjectOutputStream outStream; //sends data to client
    private DataInputStream inStream; //receives data from client
    private int clientID; //identifies the client
	private String questionNum; //identifies the question number
	private boolean canChoose = true; //tracks if client is allowed to choose answer
	private boolean buzzed = true; //tracks if client has buzzed
	private int correct = -1; //holds the correct answer to the question
	private int currentQuestion = 1;

	private String ip;

	private JFrame window;
	private Client client;
	
	private static SecureRandom random = new SecureRandom();
	
	// write setters and getters as you need
	
	public ClientWindow(Client client)
	{
		JOptionPane.showMessageDialog(window, "This is a trivia game");
		window = new JFrame("Trivia");
		question = new JLabel("questions"); // represents the question
		window.add(question);
		question.setBounds(10, 5, 350, 100);;
		

		timer = new JLabel("TIMER");  // represents the countdown shown on the window
		timer.setBounds(250, 250, 100, 20);
		clock = new TimerCode(30);  // represents clocked task that should run after X seconds
		Timer t = new Timer();  // event generator
		t.schedule(clock, 0, 1000); // clock is called every second
		window.add(timer);
		
		
		score = new JLabel("SCORE"); // represents the score
		score.setBounds(50, 250, 100, 20);
		window.add(score);

		poll = new JButton("Poll");  // button that use clicks/ like a buzzer
		poll.setBounds(10, 300, 100, 20);
		poll.addActionListener(this);  // calls actionPerformed of this class
		window.add(poll);
		
		submit = new JButton("Submit");  // button to submit their answer
		submit.setBounds(200, 300, 100, 20);
		submit.addActionListener(this);  // calls actionPerformed of this class
		window.add(submit);
		
		window.setSize(400,400);
		window.setBounds(50, 50, 400, 400);
		window.setLayout(null);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		this.client = client;
		client.run();
		
		options = new JRadioButton[4];
		optionsGroup = new ButtonGroup();
		text = new String[4];
		
		for(int index=0; index<options.length; index++)
		{
			options[index] = new JRadioButton("Option " + (index+1));  // represents an option
			// if a radio button is clicked, the event would be thrown to this class to handle
			options[index].addActionListener(this);
			options[index].setBounds(10, 110+(index*20), 350, 20);
			window.add(options[index]);
			optionsGroup.add(options[index]);
		}
		updateQuestions();
	}
	

	public ClientWindow(Socket socket){
		try {
			this.socket = socket;
			inStream = new DataInputStream(socket.getInputStream());
			correct = inStream.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void updateQuestions(){
		String[] questions = client.getQuestions();
				question.setText(questions[0]);
		for (int i = 0; i < 4; i++){
			options[i].setText(questions[i+1]);
		}
		correct = Integer.parseInt(questions[5]);
	}
		// if (questions != null) {
		// 	// Update the question label and options with the new question data
		// 	question.setText(questions[0]);
		// 	for (int i = 0; i < options.length; i++) {
		// 	  options[i].setText(questions[i + 1]);
		// 	}
		// 	correct = Integer.parseInt(questions[options.length + 1]); // Assuming correct answer index is at the end
		
		// 	// Reset canChoose flag to allow selecting answer for the next question
		// 	canChoose = true;
		// 	buzzed = false; // Reset buzzed flag as well
		
		// 	// Enable submit button again
		// 	submit.setEnabled(true);
		//   } else {
		// 	// Handle scenario where there are no more questions (e.g., show a message)
		// 	System.out.println("No more questions available");
		//   }
		// }

	//handles the submit button using ack and nack
	private void handleAcknowledgment(String acknowledgmentType) {
        if ("ack".equals(acknowledgmentType)) {
            // Enable options and submit button
			setEnabled(true);
        } else if ("negative-ack".equals(acknowledgmentType)) {
            // Disable options and submit button
            setEnabled(false);
            JOptionPane.showMessageDialog(window, "Not quick enough! You cannot pick answer.");
			System.out.println("Not quick enough! You cannot pick answer.");
        }
	}
	//helper method for enabling or disabling the submit button
	private void setEnabled(boolean enabled){ //boolean indicates that it can be true or false
		//sets value of canChoose
		canChoose = enabled;
		//sets poll and submit to the enabled parameter
		poll.setEnabled(enabled);
		submit.setEnabled(enabled);
		//sets enabled states to the vakues of enabled parameters
		for (JRadioButton option : options) {
			option.setEnabled(enabled);
		}
	}

	private void updateScore(){
		score.setText("Score: " + theScore);
	}


	// this method is called when you check/uncheck any radio button
	// this method is called when you press either of the buttons- submit/poll
	@Override
	public void actionPerformed(ActionEvent e)
	{
		//System.out.println("You clicked " + e.getActionCommand());
		// input refers to the radio button you selected or button you clicked
		String input = e.getActionCommand();  

		if(input.equals("Poll")){
			try{
				client.sendBuzz(client.getUdpSocket(), client.getCurrentIP());
				buzzed = true;
				System.out.println(client.getServerResponse());
				handleAcknowledgment(client.getServerResponse());
			} catch (IOException e1){
				e1.printStackTrace();
			}
			
		} else if (input.equals("Submit")){
			if(canChoose && buzzed){
				if (chosen == correct){
					theScore += 10;
				} else if (chosen != correct){
					theScore -= 10;
				} else{
					theScore -= 20;
				}
				updateScore();
				try{
					client.sendAnswer( chosen, client.getOutStream());
				}
				catch(IOException e1){

				}
				client.updateScore(theScore);
				client.setWindowInput("@"+chosen);
				canChoose = false;
				submit.setEnabled(false);
				updateQuestions();
			}
		} else if (input.equals(text[0])){
			chosen = 1;
		}else if (input.equals(text[1])){
			chosen = 2;
		}else if (input.equals(text[2])){
			chosen = 3;
		}else if (input.equals(text[3])){
			chosen = 4;
		}
	}	
	
	// this class is responsible for running the timer on the window
	public class TimerCode extends TimerTask
	{
		private int duration;  // write setters and getters as you need
		public TimerCode(int duration)
		{
			this.duration = duration;
		}
		@Override
		public void run()
		{
			
			if(duration < 0)
			{
				timer.setText("Timer expired");
				window.repaint();
				poll.setEnabled(false);
				submit.setEnabled(false);
				for (JRadioButton option : options) {
					option.setEnabled(false);
				}
				this.cancel();  // cancel the timed task
				return;
			}
			
			if(duration < 6)
				timer.setForeground(Color.red);
			else
				timer.setForeground(Color.black);
			
			timer.setText(duration+"");
			duration--;
			window.repaint();
		}
	}

	// main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
				String currentIP = "10.111.103.120";
                new ClientWindow(new Client(currentIP));
            }
        });
    }
}
