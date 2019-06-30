package ib.project.security.auth;

public class JwtAuthenticationRequest {
	private String username;
	private String password;
	private String email;

	public JwtAuthenticationRequest() {
		super();
	}

	public JwtAuthenticationRequest(String username, String password, String email) {
		this.setUsername(username);
		this.setPassword(password);
		this.setEmail(email);
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
