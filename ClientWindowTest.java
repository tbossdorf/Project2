package Project2;

import javax.swing.*;
import java.awt.*;
import Project2.Client;
public class ClientWindowTest
{
	public static void main(String[] args)
	{
		
		String currentIP = "10.111.103.120";
		ClientWindow window = new ClientWindow(new Client(currentIP));
		
		
	}
}