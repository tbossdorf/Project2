

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ClientWindowTest
{
	public static void main(String[] args)
	{
		String currentIP = "10.111.121.233";
		Client client = new Client(currentIP);
		ClientWindow window = new ClientWindow(client);
	}
}