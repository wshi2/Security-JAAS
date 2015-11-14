
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import javax.security.auth.Subject;

/*
* This class contains an example of an action that we want a subject to execute.
* The action involves reading the test2 file. If the subject is a root, then it will be successful, else
* it will fail.
*/
public class RootAction {
	public void runAsRoot(Subject loggedUser) {
		Set principal = loggedUser.getPrincipals();
		Iterator i = principal.iterator();
		Scanner scan = new Scanner(System.in);
		String answer = "";
		System.out.println("Running as Supervisor.");
		System.out.print("Change password (1): View data (2): View empolyee data (3): ");
		answer = scan.next();
		if (answer.equals("1")) {
			while (i.hasNext()) {
				String s = ((ExamplePrincipal) i.next()).getUsername();
				changePassword(s);
			}
		} else if (answer.equals("2")) {
			while (i.hasNext()) {
				String s = ((ExamplePrincipal) i.next()).getUsername();
				System.out.println(viewMyData(s));
			}
		} else if (answer.equals("3")) {
			while (i.hasNext()) {
				String s = ((ExamplePrincipal) i.next()).getName();
				i = principal.iterator();
				s += " (" + ((ExamplePrincipal) i.next()).getId().substring(1, 5) + ")";
				System.out.println(s);
				System.out.println(showEmployeeData(s));
			}
		}
	}

	public void replaceIt(String fileName, String newPass, String oldPass, String userNaem) {
		String nameOldPassCombo = (userNaem + ", " + oldPass);
		String nameNewPassCombo = (userNaem + ", " + newPass);
		Path path = Paths.get(fileName);
		Charset charset = StandardCharsets.UTF_8;

		String content = "";
		try {
			content = new String(Files.readAllBytes(path), charset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		content = content.replaceAll(nameOldPassCombo, nameNewPassCombo);
		try {
			Files.write(path, content.getBytes(charset));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String changePassword(String username) {
		String newPassword = "";
		String oldPassword = "";
		String verifyPassword = "";
		String line = "";
		String accountDBText = "";
		Scanner Scan = new Scanner(System.in);

		try {
			BufferedReader accountDB = new BufferedReader(new FileReader("example/accountDB.txt"));
			String dataRow = accountDB.readLine(); // Read First Line
			while (dataRow != null) {
				String[] dataArray = dataRow.split(",");
				for (String item : dataArray) {
					if (dataArray[0].equals(username)) {
						System.out.print("Enter old password: ");
						oldPassword = Scan.next();
						if (dataArray[1].substring(1, dataArray[1].length()).equals(oldPassword)) {
							System.out.print("Enter new password: ");
							newPassword = Scan.next();
							System.out.print("Confirm password: ");
							verifyPassword = Scan.next();
							if (newPassword.equals(verifyPassword)) {
								dataArray[1] = newPassword;
								replaceIt("example/accountDB.txt", newPassword, oldPassword, username);
								break;
							}
						}
					}
				}
				dataRow = accountDB.readLine();
			}

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return newPassword;
	}
	public String viewMyData(String username) {
		String data = "";
		try {
			BufferedReader accountDB = new BufferedReader(new FileReader("example/accountDB.txt"));
			String dataRow = accountDB.readLine(); // Read First Line
			while (dataRow != null) {
				String[] dataArray = dataRow.split(",");
				for (String item : dataArray) {
					if (dataArray[0].equals(username)) {
						data = dataArray[2] + "," + dataArray[3] + "," + dataArray[4] + "," + dataArray[5] + ","
								+ dataArray[6];
					}
				}
				dataRow = accountDB.readLine();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	public String showEmployeeData(String supervisor) {
		String allEmployeeData = "";
		try {
			BufferedReader accountDB = new BufferedReader(new FileReader("example/accountDB.txt"));
			String dataRow = accountDB.readLine(); // Read First Line
			while (dataRow != null) {
				String[] dataArray = dataRow.split(",");
				for (String item : dataArray) {
					if (dataArray[5].equals(supervisor)) {
						allEmployeeData += dataArray[2] + "," + dataArray[3] + "," + dataArray[4] + "," + dataArray[5] + ","
								+ dataArray[6] + "\n";
					}
					break;
				}
				dataRow = accountDB.readLine();

			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allEmployeeData;
	}
}