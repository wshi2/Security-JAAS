
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class LoginModuleExample implements LoginModule {

	// Flag to keep track of successful login.
	Boolean successfulLogin = false;

	// Variable that keeps track of the principal.
	ExamplePrincipal principal;

	/*
	 * Subject keeps track of who is currently logged in.
	 */
	Subject subject;

	/*
	 * String username String password Temporary storage for usernames and
	 * passwords (before authentication). After authentication we can clear
	 * these variables.
	 */
	String username, password, hashedPassword;

	/*
	 * Other variables that are initialized by the login context.
	 */

	CallbackHandler cbh;
	Map sharedState;
	Map options;

	/*
	 * This method is called by the login context automatically.
	 * 
	 * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.
	 * Subject , javax.security.auth.callback.CallbackHandler, java.util.Map,
	 * java.util.Map)
	 */
	public void initialize(Subject subject, CallbackHandler cbh, Map sharedState, Map options) {

		this.subject = subject;
		this.cbh = cbh;
		this.sharedState = sharedState;
		this.options = options;

	}

	/*
	 * If a user tries to abort a login then the state is reset.
	 * 
	 * @see javax.security.auth.spi.LoginModule#abort()
	 */
	public boolean abort() throws LoginException {
		if (!successfulLogin) {

			username = null;
			password = null;
			return false;
		} else {
			logout();
			return true;
		}

	}

	/*
	 * If login is valid, then the commit method is called by the LoginContext
	 * object. Here the logged in user is associated with a "principle". Think
	 * of this as a token that can from now on be used for authorization.
	 * 
	 * @see javax.security.auth.spi.LoginModule#commit()
	 */
	public boolean commit() throws LoginException {

		if (successfulLogin) {
			try {
				BufferedReader CSVFile = new BufferedReader(new FileReader("example/accountDB.txt"));
				String dataRow = CSVFile.readLine(); // Read First Line

				while (dataRow != null) {
					String[] dataArray = dataRow.split(",");
					for (String item : dataArray) {
						if (username.equals(dataArray[0])) {
							if (password.equals(dataArray[1].substring(1, dataArray[1].length()))) {
								principal = new ExamplePrincipal(dataArray[0], dataArray[1], dataArray[2], dataArray[3],
										dataArray[4], dataArray[5], dataArray[6]);
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
			// Example Principal object stores the logged in user name.

			// subject stores the current logged in user.
			subject.getPrincipals().add(principal);
			return true;
		}

		return false;
	}

	/*
	 * The actual login method that performs the authentication
	 * 
	 * @see javax.security.auth.spi.LoginModule#login()
	 */
	public boolean login() throws LoginException {
		// We will use two call backs - one for username and the other
		// for password.
		Callback exampleCallbacks[] = new Callback[2];
		exampleCallbacks[0] = new NameCallback("Username: ");
		exampleCallbacks[1] = new PasswordCallback("Password: ", false);
		// pass the callbacks to the handler.
		try {
			cbh.handle(exampleCallbacks);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedCallbackException e) {
			e.printStackTrace();
		}

		// Now populate username/passwords etc. from the handler
		username = ((NameCallback) exampleCallbacks[0]).getName();
		password = new String(((PasswordCallback) exampleCallbacks[1]).getPassword());

		// Now perform validation. This part, you can either read from a file or a
		// database. You can also incorporate secure password handling here.
		System.out.println("Checking username and password: " + username + "/" + password);

		String passwordToHash = password;
		String generatedPassword = null;
		try {
			// Create MessageDigest instance for MD5
			MessageDigest md = MessageDigest.getInstance("MD5");
			// Add password bytes to digest
			md.update(passwordToHash.getBytes());
			// Get the hash's bytes
			byte[] bytes = md.digest();
			// This bytes[] has bytes in decimal format;
			// Convert it to hexadecimal format
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			// Get complete hashed password in hex format
			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		hashedPassword = generatedPassword;
		
		try {
			BufferedReader CSVFile = new BufferedReader(new FileReader("example/accountDB.txt"));
			String dataRow = CSVFile.readLine(); // Read First Line

			while (dataRow != null) {
				String[] dataArray = dataRow.split(",");
				for (String item : dataArray) {
					
					if (dataArray[0].equals(username)) {
						System.out.println(dataArray[0]);
						System.out.println(username);
						System.out.println(hashedPassword);
						System.out.println(dataArray[1].substring(1, dataArray[1].length()));
						if ((dataArray[1].substring(1, dataArray[1].length())).equals(password /*hashedPassword*/)) {
							successfulLogin = true;
							return true;
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
		return successfulLogin;
	}

	/*
	 * 
	 * @see javax.security.auth.spi.LoginModule#logout()
	 */
	public boolean logout() throws LoginException {
		username = null;
		password = null;
		subject.getPrincipals().remove(principal);
		return true;
	}

}
