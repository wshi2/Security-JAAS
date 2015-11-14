

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.security.auth.login.*;

/* 
 * This class demonstrates 
 *        how to create a logincontext;
 *        how to invoke the login method; 
 *        how to store and extract the logged in subject.
 * This class is also the driver program. 
 * This class uses: 
 *       class LoginModuleExample: implements login method 
 *                                 to perform authentication, 
 *                                 creates a Subject object, and
 *                                 implements the logout method.                     
 */

public class LoginContextExample {

	static LoginContext lc;

	public static void main(String[] args) {

		LoginContextExample lce = new LoginContextExample();

		/*
		 * Create a call back handler. This call back handler will be populated
		 * with different callbacks by the various login modules. For example,
		 * if a login module implements a username/password style login, it
		 * populates this object with NameCallback (to get username) and
		 * PasswordCallback (which gets password).
		 */
		CallBackHandlerExample cbe = new CallBackHandlerExample();

		/*
		 * Create a new login context.
		 * 
		 * @param Policy Name : We defined a policy in the file JAASPolicy.txt
		 * and it is called "JAASExample"
		 * 
		 * @param Call Back Handler
		 */
		try {
			lc = new LoginContext("JAASExample", cbe);
		} catch (LoginException e) {
			System.err.println("Login exception.");
		}

		/**
		 * LOOK HERE try { BufferedWriter writer = new BufferedWriter(new
		 * FileWriter("accountsDB.txt")); } catch (IOException e2) { // TODO
		 * Auto-generated catch block e2.printStackTrace(); }
		 **/

		// BEGIN OUR LOOPSUFF
		System.out.print("Put Login (2) Create New Account (1): ");
		Scanner Scan = new Scanner(System.in);
		String answer = Scan.next();
		if (answer.equals("1")) {

			System.out.print("Enter Employee Name: ");
			answer = Scan.next();

			try {
				BufferedReader CSVFile = new BufferedReader(new FileReader("example/employeeDB.txt"));
				String dataRow = CSVFile.readLine(); // Read First Line

				while (dataRow != null) {
					String[] dataArray = dataRow.split(",");
					for (String item : dataArray) {
						// System.out.print(item + "\t");
						// System.out.println(dataArray[0]);
						if (answer.equals(dataArray[0])) {
							// System.out.println("You Entered: " + answer);
							System.out.print("Enter ID: ");
							answer = Scan.next();
							if ((dataArray[1].substring(1, dataArray[1].length())).equals(answer)) {
								System.out.print("Enter A Username: ");
								answer = Scan.next();
								if (isUserValid(answer)) {

									// System.out.println(answer);
									PrintWriter writer = new PrintWriter(
											new BufferedWriter(new FileWriter("example/accountDB.txt", true)));
									writer.print(answer);
									System.out.print("Enter A Password: ");
									answer = Scan.next();
									String passwordToHash = answer;
							        String generatedPassword = null;
									try {
							            // Create MessageDigest instance for MD5
							            MessageDigest md = MessageDigest.getInstance("MD5");
							            //Add password bytes to digest
							            md.update(passwordToHash.getBytes());
							            //Get the hash's bytes
							            byte[] bytes = md.digest();
							            //This bytes[] has bytes in decimal format;
							            //Convert it to hexadecimal format
							            StringBuilder sb = new StringBuilder();
							            for(int i=0; i< bytes.length ;i++)
							            {
							                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
							            }
							            //Get complete hashed password in hex format
							            generatedPassword = sb.toString();
							        }
							        catch (NoSuchAlgorithmException e)
							        {
							            e.printStackTrace();
							        }
							        String hashedPassword = generatedPassword;
									
									
									// System.out.println(answer);
									writer.print(", " + answer /*hashedPassword*/+ ", " + dataArray[0] + "," + dataArray[1] + "," + 
												 dataArray[2] + "," + dataArray[3] + "," + dataArray[4]);
									writer.println();
									writer.close();
								} else {
									System.out.println("Username Already Taken!!!");
								}
							}
							break;
						}
					}
					dataRow = CSVFile.readLine();
				}

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (answer.equals("2")) {
			/*
			 * Perform login.
			 */
			try {
				lc.login();

				// If we reach this point then login has succeeded.
				System.out.println("You are now logged in!");
				/*
				 * Print the various Principals attached with the logged
				 * Subject. In this example, we attach only one principal with
				 * each subject.
				 */
				Subject loggedUser = lc.getSubject();
				Set principal = lc.getSubject().getPrincipals();
				Iterator i = principal.iterator();
				while (i.hasNext()) {
					String s = ((ExamplePrincipal) i.next()).getPosition();
					
					/*
					 * An example of how to perform authorization actions. E.g.,
					 * class RootAction contains the methods that a root can
					 * perform.
					 */
					if (s.equals(" Manager") || s.equals(" VP") || s.equals(" President")) {
						System.out.println(s);
						RootAction ra = new RootAction();
						ra.runAsRoot(loggedUser);
					} else {
						System.out.println(s);
						EmployeeAction ea = new EmployeeAction();
						ea.runAsEmployee(loggedUser);
					}
				}
			}

			catch (LoginException e) {
				System.out.println("Username/password incorrect! " + e);

			} catch (SecurityException e) {
				System.out.println(" " + e);
			}
		} else
			System.out.print("Wrong Input");

	}

	public static boolean isUserValid(String userName) {
		try {
			BufferedReader CSVFile2 = new BufferedReader(new FileReader("example/accountDB.txt"));
			String dataRow2 = CSVFile2.readLine(); // Read First Line

			while (dataRow2 != null) {
				String[] dataArray = dataRow2.split(",");
				for (String item : dataArray) {
					// System.out.print(item + "\t");
					if (userName.equals(dataArray[0])) {
						return false;
					}
				}
				dataRow2 = CSVFile2.readLine();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public static boolean overwriteUsername(String name, String ID, String username) {
		
		return false;
	}

}
