package Project2;

public class ClientWindowTest
{
	public static void main(String[] args)
	{
		if(args.length != 1)
		{
			System.out.println("Usage: java ClientWindowTest <IP>");
			System.exit(1);
		}else{
			String currentIP = args[0];
			ClientWindow window = new ClientWindow(currentIP);
		}
		
	}
}