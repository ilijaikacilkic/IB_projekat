package ib.project.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ib.project.model.User;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class FileController {
	private static String UPLOAD_FOLDER = "C:\\Users\\Dragan\\Desktop\\images_upload\\";

	@PostMapping("/uploadZip")
	public void fileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes)
			throws FileNotFoundException {

		if (file.isEmpty()) {
			throw new FileNotFoundException("Please select a file and try again");
		}

		try {
			// read and write the file to the slelected location-
			byte[] bytes = file.getBytes();
			Path path = Paths.get(UPLOAD_FOLDER + file.getOriginalFilename());
			Files.write(path, bytes);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@GetMapping(value = "/downloadJKS")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<byte[]> downloadJKS() {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		File file = null;
		try {
			file = new File("C:\\Users\\Dragan\\Desktop\\ib_keys\\" + user.getUsername() + ".jks");
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("filename", user.getUsername() + ".jks");

		byte[] bFile = readBytesFromFile(file.toString());

		return ResponseEntity.ok().headers(headers).body(bFile);
	}

	private static byte[] readBytesFromFile(String filePath) {

		FileInputStream fileInputStream = null;
		byte[] bytesArray = null;
		try {

			File file = new File(filePath);
			bytesArray = new byte[(int) file.length()];

			// read file into bytes[]
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bytesArray);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		return bytesArray;
	}
}
