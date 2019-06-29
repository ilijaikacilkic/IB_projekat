package ib.project.service;

import java.text.ParseException;

public interface KeyStoreService {

	String generateKeyStoreFile(String username) throws ParseException;
}
