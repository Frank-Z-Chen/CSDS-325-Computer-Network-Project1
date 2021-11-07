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