package img_zip;


import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
		Scanner userInputName = new Scanner(System.in);
		
		String userName = userInputName.nextLine();
		
		
		System.out.println("Copy and paste the path to your file: ");
		Scanner filePathInput = new Scanner(System.in);		
		String filePath = filePathInput.nextLine();
		userInputName.close();
		filePathInput.close();
		File chosenFile = new File(filePath);
		
		
		
		String[] extension = new String[] {
				"gif","png","bmp","jpg","jpeg"
		};

		FilenameFilter fnf = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				for(final String type : extension ) {
					if(name.endsWith("."+type)) {
						return true;
					}
				}
				return false;
			}
		};
		
		if(chosenFile.isDirectory()) {
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.newDocument();
				
				
				Element image = doc.createElement("img");
				image.appendChild(doc.createTextNode(userName));
				doc.appendChild(image);
				for(final File f : chosenFile.listFiles(fnf)) {
					BufferedImage  buffimg = null;
					try {
						images.add(f.getAbsolutePath());
						buffimg = ImageIO.read(f);
						
						Element img1 = doc.createElement("image");
						Attr name = doc.createAttribute("img name");
						name.setValue(String.valueOf(f.getName()));
						img1.setAttributeNode(name);
						Attr width = doc.createAttribute("img width");
						width.setValue(String.valueOf(buffimg.getWidth()));
						img1.setAttributeNode(width);
						Attr height = doc.createAttribute(" img height ");
						height.setValue(String.valueOf(buffimg.getHeight()));
						img1.setAttributeNode(height);
						Attr hashCode = doc.createAttribute("HashCode");
						hashCode.setValue(String.valueOf(buffimg.hashCode()));
						img1.setAttributeNode(hashCode);
						
						image.appendChild(img1);
						
						
					}catch (Exception e) {
						// TODO: handle exception
					}
				}
						
				TransformerFactory tff = TransformerFactory.newInstance();
				Transformer t = tff.newTransformer();
				DOMSource domS = new DOMSource(image);
				File newFile = new File("C:\\Users\\Dragan\\Desktop\\images_folder\\xml.xml");
				StreamResult sr = new StreamResult(newFile);
				
				t.transform(domS, sr);
				
				images.add(newFile.getAbsolutePath());
				
				zip(images);
				
			}catch (Exception e) {
				// TODO: handle exception
			}
		}			
	}
	
	/* Zipuje sliku i xml fajl */
	
    public static void zip(List<String> images){
        
        FileOutputStream fileOutStream = null;
        ZipOutputStream zipOutStream = null;
        FileInputStream fileInputStream = null;
        try {
        	fileOutStream = new FileOutputStream("C:\\Users\\Dragan\\Desktop\\images_foler\\zip.zip");
        	zipOutStream = new ZipOutputStream(new BufferedOutputStream(fileOutStream));
            for(String filePath : images){
                File input = new File(filePath);
                fileInputStream = new FileInputStream(input);
                ZipEntry ze = new ZipEntry(input.getName());
                zipOutStream.putNextEntry(ze);
                byte[] tmp = new byte[4*1024];
                int size = 0;
                while((size = fileInputStream.read(tmp)) != -1){
                	zipOutStream.write(tmp, 0, size);
                }
                zipOutStream.flush();
                fileInputStream.close();
            }
            zipOutStream.close();
            System.out.println("Done... Zipped the files...");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{
            try{
                if(fileOutStream != null) fileOutStream.close();
            } catch(Exception ex){
                 
            }
        }
 
        
    }

}
