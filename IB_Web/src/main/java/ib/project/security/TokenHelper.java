package ib.project.security;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenHelper {
	@Value("IB_Project_Shell")
	private String APP_NAME;

	@Value("somesecret")
	public String SECRET;

	@Value("300")
	private int EXPIRES_IN;

	@Value("Authorization")
	private String AUTH_HEADER;

	// HMAC sa SHA-512 hash algoritmom
	private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

	// Metoda koja iz sadrzaja tokena (claims) pronalazi username korisnika
	public String getUsernameFromToken(String token) {
		String username;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			username = claims.getSubject();
		} catch (Exception e) {
			username = null;
		}
		return username;
	}

	public Date getIssuedAtDateFromToken(String token) {
		Date issueAt;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			issueAt = claims.getIssuedAt();
		} catch (Exception e) {
			issueAt = null;
		}
		return issueAt;
	}

	public String getAudienceFromToken(String token) {
		String audience;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			audience = claims.getAudience();
		} catch (Exception e) {
			audience = null;
		}
		return audience;
	}

	public String refreshToken(String token) {
		String refreshedToken;
		Date a = new Date();
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			claims.setIssuedAt(a);
			refreshedToken = Jwts.builder().setClaims(claims).setExpiration(generateExpirationDate())
					.signWith(SIGNATURE_ALGORITHM, SECRET).compact();
		} catch (Exception e) {
			refreshedToken = null;
		}
		return refreshedToken;
	}

	// Funkcija koja pri autentifikaciji korisnika kreira novi JWT token
	public String generateToken(String username) {
		return Jwts.builder().setIssuer(APP_NAME).setSubject(username).setIssuedAt(new Date())
				.setExpiration(generateExpirationDate()).signWith(SIGNATURE_ALGORITHM, SECRET).compact();
	}

	private Claims getAllClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			claims = null;
		}
		return claims;
	}

	private Date generateExpirationDate() {
		return new Date(new Date().getTime() + EXPIRES_IN * 1000);
	}

	public int getExpiredIn() {
		return EXPIRES_IN;
	}

	// FUnkcija koja proverava da li je token validan u odnosu na korisnika koji ga
	// je prosledio serveru
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return username != null && username.equals(userDetails.getUsername());
	}

	public String getToken(HttpServletRequest request) {
		/**
		 * Getting the token from Authentication header e.g Bearer your_token
		 */
		String authHeader = getAuthHeaderFromHeader(request);
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}

		return null;
	}

	public String getAuthHeaderFromHeader(HttpServletRequest request) {
		return request.getHeader(AUTH_HEADER);
	}

}
