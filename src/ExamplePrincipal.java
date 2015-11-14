
import java.security.Principal;

/* 
 * We have created a custom principal class to store each principal.
 */

public class ExamplePrincipal implements Principal {
	String username;
	String password;
	String name;
	String id;
	String position;
	String supervisor;
	String salary;
	
	ExamplePrincipal(String username, String password, String name, String id, String position, String supervisor,String salary) {
		this.username = username;
		this.password = password;
		this.name = name;
		this.id = id;
		this.position = position;
		this.supervisor = supervisor;
		this.salary = salary;
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}
	
}
