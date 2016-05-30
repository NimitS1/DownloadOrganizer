package org.nimit.downloadorganizer.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.yaml.snakeyaml.Yaml;

public class Main {
	
	private static KnowledgeBase kb = null;
	private Logger logger = Logger.getLogger("org.nimit.downloadorganizer.main");

	
	private static boolean copyFile(File file) throws IOException {
		
		String[] parts = file.getName().split("\\.");
		String extension = parts[parts.length -1 ];
		if(kb.getFileTypeMap().containsKey(extension)){
			String destinationFolder = kb.getFileTypeMap().get(extension);
			CopyOption option = StandardCopyOption.REPLACE_EXISTING;
			try {
				Files.move(Paths.get(file.getAbsolutePath()), Paths.get(destinationFolder+"\\"+file.getName()), option);
				
				File destinationFile = new File(destinationFolder+"\\"+file.getName());
				if(!destinationFile.canWrite()) {
					//This is a hacky way to ensure that the copying of large files is completed					
					Thread.sleep(2000);
				}
				
				System.out.println("Copied " + file.getName());
				return true;
			} catch(IOException ex) {
				throw ex;
			} catch(SecurityException sx) {
				throw sx;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.err.println("The mapping for " + extension + " is missing");
		}
		
		return false;
		
	}
	
	public static void main(String[] args) {
		
		try {
			//kb = KnowledgeBase.load("C:\\Users\\snimit\\workspace\\downloadorganizer\\download_organizer.yml");
			kb = KnowledgeBase.load("download_organizer.yml");
		} catch (NoSuchFileException ex) {
			System.out.println("The configuration file does not exist");
			return;
		}
		
		for(String folder : kb.getDownloadFolders()) {
			File downloadDirectory = new File(folder);
			if(downloadDirectory.isDirectory()) {
				File[] files = downloadDirectory.listFiles();
				for(int i = 0; i < files.length;i++) {
					File currentFile = files[i];
					if(currentFile.canWrite()) {    
						try {
							copyFile(currentFile);
						} catch(Exception ex) {
							System.err.println(ex.toString());
						}
					}
					/*
					 * If we can't write the file, it is probably in use so it is better
					 * not to copy it right now. It can be copied in the next run.
					 */
				}
				
			} else {
				System.err.println(downloadDirectory.getName() + " is not a directory!");
			}
		}
		
		
	}

}
