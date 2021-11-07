package zxc347;
import java.io.*;
import java.net.*;

/**
 * @Description: This is a common HTTP 1.1 server using TCP connection.
 * @Author: Zhaokuan Chen zxc347
 * @Date: 10/27/2020
 */

public class HTTPServer {


		
	public static void main(String[] args) throws Exception{
		
		File config = new File("config.txt"); //import the config file.
		FileReader readConfig = new FileReader(config);
		BufferedReader readConfigFile = new BufferedReader(readConfig);
		String wholePortNumberString = readConfigFile.readLine();
		String portNumberString = wholePortNumberString.split(":")[1];
		int portNumber = Integer.parseInt(portNumberString); //get port number from config file
		readConfigFile.close();

		ServerSocket welcomeSocket = new ServerSocket(portNumber); //My port number is 50009.
		
		while(true){
			Socket connectionSocket = welcomeSocket.accept(); //wait at welcomeSocket for incoming connection.
			System.out.println(connectionSocket.hashCode()); //print the hash code of connection socket
			MyThread myThread = new MyThread(connectionSocket, config); //put the socket and the 2 static html file into MyThread.
			Thread thread = new Thread(myThread);
			thread.start(); //run the multi-thread.
		
		}
	}
}
