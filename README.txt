The source files are in the directory: ExamplePackage.


To run the program from Command line (in Mac/Linux or Windows command prompt): 
	(1) Compile the code: 
		javac ExamplePackage/*.java
	(2) Run the code: 
		java -Djava.security.manager \
			-Djava.security.policy==ExamplePolicies.txt \
			 -Djava.security.auth.login.config==JAASPolicy.txt \
			  ExamplePackage/LoginContextExample 

To run the program on Eclipse:
 	(1) Create a new Java project in Eclipse. 
	(2) Select the directory JAASExample to import the source files into
		Eclipse.
	(3) Select the LoginContextExample.java file. 
	(4) In the menu option: Run->Run Configurations, select the tab 
		"Arguments"
	(5) In the VM arguments add: 
 		-Djava.security.manager -Djava.security.policy==ExamplePolicies.txt -Djava.security.auth.login.config==JAASPolicy.txt  
	(6) Run. 

Usernames and passwords that will work: root/security or team/security. 
