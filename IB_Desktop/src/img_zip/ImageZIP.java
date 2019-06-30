package img_zip;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ImageZIP {

	public static void main(String[] args) {
		List<String> images = new ArrayList<>();

		System.out.println("user:");
		Scanner userNameInput = new Scanner(System.in);

		String userName = userNameInput.nextLine();

		System.out.println("Copy and paste the image location path: ");
		Scanner filePathInput = new Scanner(System.in);

		String filePath = filePathInput.nextLine();
		userNameInput.close();
		filePathInput.close();
		File chosenFile = new File(filePath);

		String[] extension = new String[] { "gif", "png", "bmp", "jpg", "jpeg" };

		FilenameFilter fnf = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				for (final String ext : extension) {
					if (name.endsWith("." + ext)) {
						return true;
					}
				}
				return false;
			}
		};

		if (chosenFile.isDirectory()) {
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.newDocument();
				SimpleDateFormat sdf = new SimpleDateFormat("dd.mm.yyyy HH:mm");

				Element el = doc.createElement("img");
				el.appendChild(doc.createTextNode(userName));
				doc.appendChild(el);
				for (final File f : chosenFile.listFiles(fnf)) {
					BufferedImage bi = null;
					try {
						images.add(f.getAbsolutePath());
						bi = ImageIO.read(f);

						Element el1 = doc.createElement("Image");
						Attr name = doc.createAttribute("Name");
						name.setValue(String.valueOf(f.getName()));
						el1.setAttributeNode(name);
						Attr width = doc.createAttribute("Width");
						width.setValue(String.valueOf(bi.getWidth()));
						el1.setAttributeNode(width);
						Attr height = doc.createAttribute("Height");
						height.setValue(String.valueOf(bi.getHeight()));
						el1.setAttributeNode(height);
						Attr hashCode = doc.createAttribute("HashCode");
						hashCode.setValue(String.valueOf(bi.hashCode()));
						el1.setAttributeNode(hashCode);
						Attr date = doc.createAttribute("Date");
						date.setValue(sdf.format(new Date()));
						el1.setAttributeNode(date);

						el.appendChild(el1);

					} catch (Exception e) {
						// TODO: handle exception
					}
				}

				TransformerFactory tff = TransformerFactory.newInstance();
				Transformer t = tff.newTransformer();
				DOMSource domS = new DOMSource(el);
				File newFile = new File("C:\\Users\\Dragan\\Desktop\\images\\xml.xml");
				StreamResult sr = new StreamResult(newFile);

				t.transform(domS, sr);

				images.add(newFile.getAbsolutePath());

				zip(images);

			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public static void zip(List<String> images) {

		FileOutputStream fos = null;
		ZipOutputStream zipOut = null;
		FileInputStream fis = null;
		try {
			fos = new FileOutputStream("C:\\Users\\Dragan\\Desktop\\images\\zip.zip");
			zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
			for (String filePath : images) {
				File input = new File(filePath);
				fis = new FileInputStream(input);
				ZipEntry ze = new ZipEntry(input.getName());

				zipOut.putNextEntry(ze);
				byte[] tmp = new byte[4 * 1024];
				int size = 0;
				while ((size = fis.read(tmp)) != -1) {
					zipOut.write(tmp, 0, size);
				}
				zipOut.flush();
				fis.close();
			}
			zipOut.close();
			System.out.println("Done... Zipped the files...");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (Exception ex) {

			}
		}

	}

}
