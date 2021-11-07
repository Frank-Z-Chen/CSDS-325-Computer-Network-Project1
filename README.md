# CSDS-325-Computer-Network
This is the first project of Computer Network in CWRU. Project Content: Create 3 html pages that utilize cookies to store the visit times.
A special URL, http://eecslab-10.case.edu:50009/zxc347/visit.html that returns an HTML page specifying the number of visits a given browser has made to any valid URLs on your web server.
A URL http://eecslab-10.case.edu:50009/zxc347/test1.html that will return an HTML object served from a statically stored file
A URL http://eecslab-10.case.edu:50009/zxc347/test2.html, another static HTML page that includes  (i) a header “This is test page 2”, (ii) the text “Hi, my name is <Your name>. Below is the iframe that embeds my test page 1, and (3) an iframe (an “inlined frame”) with borders showing your test page 1. 
eecslab-10.case.edu can be replaced by localhost when running this project locally.

In order for you to run the server by using cmd or IDE without throwing an exception, I made 2 config files.
Please do not delete either of them.
Please be aware, if you delete one of them, either cmd or IDE will throw an exception.
If you want to test my code on cmd, please take changes on the config file in src folder.
The idle time of persistent connection is defaulted to 60 seconds. 

The source code:HTTPServer\src\zxc347\
The test1 and test2 Html file:HTTPServer\src
Config file:HTTPServer\ & HTTPServer\src

Command:I am using Windows so I used Putty to run the java code remotely. 
	The following command are those I used on Putty. Be aware of "/" and spaces in the command.
	cd HTTPServer/src/zxc347
	javac ./*.java
	cd ..
	java zxc347.HTTPServer

Browser: Firefox 82.0.2
