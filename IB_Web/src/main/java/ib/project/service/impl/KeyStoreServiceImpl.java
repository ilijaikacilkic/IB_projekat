package ib.project.service.impl;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.stereotype.Service;

import ib.project.certificate.CertificateGenerator;
import ib.project.keystore.IssuerData;
import ib.project.keystore.KeyStoreWriter;
import ib.project.keystore.SubjectData;
import ib.project.service.KeyStoreService;

@Service
public class KeyStoreServiceImpl implements KeyStoreService {

	@Override
	public String generateKeyStoreFile(String username) throws ParseException {
		CertificateGenerator gen = new CertificateGenerator();
		KeyPair keyPair = gen.generateKeyPair();

		SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = iso8601Formater.parse("2019-12-31");
		Date endDate = iso8601Formater.parse("2029-12-31");

		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
		// builder.addRDN(BCStyle.CN, userDTO.GE+" "+userDTO.getLastname());
		// builder.addRDN(BCStyle.SURNAME, userDTO.getFirstname());
		// builder.addRDN(BCStyle.GIVENNAME, userDTO.getLastname());
		builder.addRDN(BCStyle.O, "UNS-FTN");
		builder.addRDN(BCStyle.OU, "Katedra za informatiku");
		builder.addRDN(BCStyle.C, "RS");
		builder.addRDN(BCStyle.E, username);
		builder.addRDN(BCStyle.UID, "12345");

		String sn = "1";
		IssuerData issuerData = new IssuerData(keyPair.getPrivate(), builder.build());
		SubjectData subjectData = new SubjectData(keyPair.getPublic(), builder.build(), sn, startDate, endDate);

		X509Certificate cert = gen.generateCertificate(issuerData, subjectData);

		KeyStoreWriter keyStoreWriter = new KeyStoreWriter();
		keyStoreWriter.loadKeyStore(null, username.toCharArray());
		keyStoreWriter.write(username, keyPair.getPrivate(), "test10".toCharArray(), cert);
		String location = "C:\\Users\\Dragan\\Desktop\\ib_keys\\" + username + ".jks";
		keyStoreWriter.saveKeyStore(location, "pass".toCharArray());

		return location;
	}

}
