package zxc347;

import java.io.*;
import java.net.*;
import java.lang.Integer;

/**
 * @Description: This is a common HTTP 1.1 server using TCP connection.
 * @Author: Zhaokuan Chen zxc347
 * @Date: 10/27/2020
 */

public class MyThread implements Runnable {
	
	private Socket connectionSocket; //the connection socket
	int visitTimes = 0; // Initiate visitTimes.
	String visitTimesString = "0"; // Initiate visiTimeString. visitTimeString is simply the String type of visitTimes.
	boolean persistentConnection = false; //implement persistent connection or not
	int idleTime = 0; //the idle time of persistent connection
	File test1; // test1.html file
	File test2; // test2.html file
	File config;// config.txt file


	public MyThread(Socket connectionSocket, File config) { //constructor
		this.connectionSocket = connectionSocket;
		this.config = config;
	}

	public void run() {
		try {
			FileReader readConfig = new FileReader(config); //extract content from config.txt
			BufferedReader readConfigFile = new BufferedReader(readConfig);
			String wholePortNumberString = readConfigFile.readLine(); //read the first line, this line is not used.
			String wholeTest1DirectoryString = readConfigFile.readLine();//read the second line, this line contains directory of test1.html
			String test1DirectoryString = wholeTest1DirectoryString.split(":")[1]; //directory of test1.html
			String wholeTest2DirectoryString = readConfigFile.readLine();//third line, containing the directory of test2.html
			String test2DirectoryString = wholeTest2DirectoryString.split(":")[1]; //directory of test2.html
			String persistentConnectionString = readConfigFile.readLine(); //fourth line, containing the choice of persistent connection
			persistentConnection = Boolean.parseBoolean(persistentConnectionString.split(":")[1]);//whether to implement persistent connection
			String idleTimeString = readConfigFile.readLine();//fifth line, containing the idle time of persistent connection
			idleTime = Integer.parseInt(idleTimeString.split(":")[1]); //idle time of persistent connection
			readConfigFile.close();
			test1 = new File(test1DirectoryString); // import test1.html
			test2 = new File(test2DirectoryString); // import test2.html
		} catch (Exception e6) {
			System.out.println(e6);
		}

		try {
			if (persistentConnection)
				connectionSocket.setSoTimeout(idleTime * 1000);//the config file starts with 60s
		} catch (IOException e5) {
			System.out.print(e5);
		}
		
		
		do {
			try {
				BufferedReader inFromClient = // Create input stream from socket
						new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				DataOutputStream outToClient = // Create output stream to socket
						new DataOutputStream(connectionSocket.getOutputStream());
				StringBuilder requestBuilder = new StringBuilder();
				String lines;
				
				while ((lines = inFromClient.readLine()) != null) {// import the request message into the String lines
					//System.out.println(lines);
					requestBuilder.append(lines + "\r\n");
					if (requestBuilder.toString().endsWith("\r\n\r\n")) {
						break;
					}
				}
				if (lines == null) { //if there is nothing in the buffer, then close socket.
					connectionSocket.shutdownInput();

					connectionSocket.shutdownOutput();

					connectionSocket.close();
					break;
				}

				String request = requestBuilder.toString(); // convert StringBuilder to String.
				String[] requestLines = request.split("\r\n"); // chop up the whole request into lines.
				String[] eachRequestLine = requestLines[0].split(" ");// chop up the first lines and get the individual information. 
				System.out.println(request); // For testing purpose.
				String httpMethod = eachRequestLine[0]; // get HTTP method, not used
				String path = eachRequestLine[1]; // get the request path
				String version = eachRequestLine[2]; // get the version of HTTP, which should be 1.1, not used
				// String host = requestLines[1].split(" ")[1]; //the host should be the second
				// line, so we chop up the second line as well. Not used
				// String connection = requestLines[2].split(" ")[1]; //get connection, not
				// used.
				// String userAgent = requestLines[5].split(" ")[1]; //get user agent, not used.
				String cookie = null; //initialize cookie string 
				if (request.toString().contains("Cookie")) { //search for "Cookie" in the request
					String restCookieLine = request.substring(request.indexOf("Cookie"));
					String cookieLine = restCookieLine.split("\r\n")[0];
					System.out.println("rest cookie line" + restCookieLine);
					System.out.println("cookie line" + cookieLine);
					cookie = cookieLine.split(" ")[1]; //get the cookie string "name=value"
				}

				String[] parsePath = new String[3];
				parsePath = path.split("/", 3); // Split the path line into 2 part. The student ID like"zxc347" and the name of the request html file.
				String name = parsePath[parsePath.length - 2]; // For tidiness, I also changed 1 to parsePath.length-2
				String requestHtml = parsePath[parsePath.length - 1]; // I changed 2 to parsePath.length-1, because java throw me an exception when I use parsePath[2].
				if (name.equals("zxc347") & requestHtml.equals("test1.html")) { // Check if the HTTP request is aiming for "zxc347" and html file "test1.html".
					try {
						FileReader reader = new FileReader(test1);
						outToClient.write("HTTP/1.1 200 OK\r\n".getBytes());

						if (cookie != null) { //this chunk of code deal with cookies
							if (cookie.split("=")[0].equals("visit")) { //check if the HTTP request come with a cookie header.
								String time = cookie.split("=")[1];
								visitTimes = Integer.parseInt(time.split(";")[0]) + 1; // If the request come with a header, then add 1 to the cookie value
								visitTimesString = Integer.toString(visitTimes);
								outToClient.write(("Set-Cookie: visit =" + visitTimesString
										+ "\r\n Path = /zxc347\r\n").getBytes());// Send back the cookie header

							}
						} else
							outToClient.write("Set-Cookie: visit = 1\r\n Path = /zxc347\r\n".getBytes());
							// If the request does not come with a cookie header, then just issue the client a new cookie with value 1,
						
						outToClient.write("Connection: keep-alive \r\n".getBytes()); //keep live connection
						outToClient.write(("Content-Length: " + test1.length() + "\r\n").getBytes()); //content length of test1.html
						outToClient.write("\r\n".getBytes());
						BufferedReader httpReader = new BufferedReader(reader);
						String httpLines;
						while ((httpLines = httpReader.readLine()) != null) { // Read test1.html
							outToClient.write(httpLines.getBytes()); // Send test1.html to the client.
							outToClient.write("\r\n".getBytes());
						}
						httpReader.close(); // Close reader

					} catch (Exception e) {
						System.out.println(e);
						outToClient.write("HTTP/1.1 404 Not Found\r\n".getBytes());
						outToClient.write("<b>404 NOT FOUND</b>".getBytes());
					}
				} 
				else if (name.equals("zxc347") & requestHtml.equals("test2.html")) { // Check if the HTTP request is aiming for "zxc347" and html file "test1.html".
					try {
						FileReader reader = new FileReader(test2);
						outToClient.write("HTTP/1.1 200 OK\r\n".getBytes());

						if (cookie != null) {
							if (cookie.split("=")[0].equals("visit")) { // This if statement is to check if the HTTP request  come with a cookie header.
								String time = cookie.split("=")[1];
								visitTimes = Integer.parseInt(time.split(";")[0]) + 1; // If the request come with a cookie header, 
								//then add 1 to the value of the cookie, showing that the browser visited my server 1 time.
								visitTimesString = Integer.toString(visitTimes);
								outToClient.write(("Set-Cookie: visit =" + visitTimesString
										+ "\r\n Path = /zxc347\r\n").getBytes());// Send back the cookie header.
							}
						} else
							outToClient.write("Set-Cookie: visit = 1\r\n Path = /zxc347\r\n".getBytes());
							// If the request does not come with a cookie header,
							// then just issue the client a new cookie with value 1, showing that the browser visited my server 1 time. 

						outToClient.write("Connection: keep-alive \r\n".getBytes()); //keep alive connection.
						outToClient.write(("Content-Length: " + test2.length() + "\r\n").getBytes()); //content length of test2.html
						outToClient.write("\r\n".getBytes());
						BufferedReader httpReader = new BufferedReader(reader);
						String httpLines;
						while ((httpLines = httpReader.readLine()) != null) { // Read test1.html
							outToClient.write(httpLines.getBytes()); // Send test1.html to the client.
							outToClient.write("\r\n".getBytes());
						}
						httpReader.close(); // Close reader
					} catch (Exception e) {
						System.out.println(e);
						outToClient.write("HTTP/1.1 404 Not Found\r\n".getBytes());
						outToClient.write("<b>404 NOT FOUND</b>".getBytes());
					}
				} 
				else if (name.equals("zxc347") & requestHtml.equals("visit.html")) { // Check if the HTTP request is aiming for "zxc347" and the web page visit.html
					try {
						if (cookie != null) {
							if (cookie.split("=")[0].equals("visit")) { // This if statement is to check if the HTTP request come with a cookie header.
								String time = cookie.split("=")[1];
								visitTimes = Integer.parseInt(time.split(";")[0]) + 1;// If the request come with a cookie header, 
								//then add 1 to the value of the cookie, showing that the browser visited my server 1 time.
								visitTimesString = Integer.toString(visitTimes);
								outToClient.write("HTTP/1.1 200 OK\r\n".getBytes());
								outToClient.write(("Set-Cookie: visit =" + visitTimesString
										+ "\r\n Path = /zxc347\r\n").getBytes());//send the cookie with num of visit that the browser made to client
								outToClient.write(
										("Content-Length: " + ("<b>Your browser visited various URLs on this site ("
												+ visitTimesString + ") times.</b>\r\n").length() + "\r\n").getBytes()); //length of html page
								outToClient.write("Connection: keep-alive\r\n".getBytes()); //keep-alive connection
								outToClient.write("\r\n".getBytes());
								outToClient.write(("<b>Your browser visited various URLs on this site ("
										+ visitTimesString + ") times.</b>\r\n").getBytes());// Show the num of visit the browser has made on the html page.
							}
						} else { // If the request does not come with a cookie header, then just issue the client
									// a new cookie with value 1.
							String firstTimeVisit = "1";
							outToClient.write("HTTP/1.1 200 OK\r\n".getBytes());
							outToClient.write(
									"Set-Cookie: visit = 1\r\n Path = /zxc347\r\n"
											.getBytes());
							//; Path = /zxc347; Expires=Thu, 31 Oct 2021 07:28:00 GMT
							outToClient
									.write(("Content-Length: " + ("<b>Your browser visited various URLs on this site ("
											+ firstTimeVisit + ") times.</b>\r\n").length() + "\r\n").getBytes()); //length of the html page
							outToClient.write("Connection: keep-alive\r\n".getBytes()); //connection keep-alive
							outToClient.write("\r\n".getBytes());
							outToClient.write(("<b>Your browser visited various URLs on this site (" + firstTimeVisit
									+ ") times.</b>\r\n").getBytes()); // Show the num of visit the browser has made on the html page.
						}

					} catch (Exception e) {
						System.out.println(e);
						outToClient.write("HTTP/1.1 404 Not Found\r\n".getBytes());
						outToClient.write("<b>404 NOT FOUND</b>".getBytes());
					}
				} else { // If the request is aiming for some non-compliant URL, tell client "404 NOT
							// FOUND"
					outToClient.write("HTTP/1.1 404 Not Found\r\n".getBytes());
					outToClient.write("Connection: Closed\r\n".getBytes());
					outToClient.write("\r\n".getBytes());
					outToClient.write("<b>404 NOT FOUND</b>".getBytes()); // return 404 NOT FOUND if the request is
																			// non-compliant.
				}
				outToClient.flush();
				if (!persistentConnection) { //if we do not implement persistent connection
					connectionSocket.shutdownInput(); //close socket.

					connectionSocket.shutdownOutput();

					connectionSocket.close();
				}

			} catch (SocketTimeoutException e3) {
				System.out.println("Socket Timeout!");
				e3.printStackTrace();
				try {
					connectionSocket.shutdownInput();//Socket times out, close socket

					connectionSocket.shutdownOutput();

					connectionSocket.close(); // Mission complete, close connectionSocket,
				} // wait at welcomeSocket for future connection
				catch (Exception e4) {
					System.out.println(e4);
					e4.printStackTrace();
				}
				break;
			} catch (Exception e1) {
				System.out.println(e1);
				e1.printStackTrace();
			}
		} while (persistentConnection);//if we implement persistent connection, run the while loop.
	}
}
