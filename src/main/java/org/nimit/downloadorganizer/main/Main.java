package org.nimit.downloadorganizer.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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

	
	private static boolean moveFile(File file) throws IOException {
		
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
				e.printStackTrace();
			}
		} else {
			System.err.println("The mapping for " + extension + " is missing");
		}
		
		return false;
		
	}
	
	private static String getMappingPath() throws IOException {
		
		String path = "";
		
		Console c = System.console();
		c.printf("Please enter the path of the mapping file");
		c.readLine("%s", path);
		FileWriter fw = new FileWriter("mapping.txt");
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(path);
		bw.write("\n");
		bw.close();
		fw.close();
		
		return path;
	}
	
	public static void main(String[] args) {
		
		String mappingPath = "";
		try {
			
			//Get the location of the mapping file
			if(Files.exists(Paths.get("mapping.txt"))){
				FileReader i = new FileReader("mapping.txt");
				BufferedReader br = new BufferedReader(i);
				mappingPath = br.readLine();
				br.close();
				i.close();
				
				if(mappingPath == null) {
					mappingPath = getMappingPath();
				}
				
			} else {
				Console c = System.console();
				c.printf("Please enter the path of the mapping file");
				c.readLine("%s", mappingPath);
				FileWriter fw = new FileWriter("mapping.txt");
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(mappingPath);
				bw.write("\n");
				bw.close();
				fw.close();
			}
			
			//Parse the mapping file
			kb = KnowledgeBase.load(mappingPath);

		} catch (NoSuchFileException ex) {
			System.err.println("The configuration file does not exist");
			return;
		} catch (FileNotFoundException cx) {
			System.err.println("mapping.txt is not present");
		} catch (IOException ex) {
			System.err.println("Faced an io exception");
		}
		
		
		//For each download folder, process every file
		for(String folder : kb.getDownloadFolders()) {
			File downloadDirectory = new File(folder);
			if(downloadDirectory.isDirectory()) {
				File[] files = downloadDirectory.listFiles();
				for(int i = 0; i < files.length;i++) {
					File currentFile = files[i];
					if(currentFile.canWrite()) {    
						try {
							moveFile(currentFile);
						} catch(Exception ex) {
							System.err.println(ex.toString());
						}
					}
					/*
					 * If we can't write the file, it is probably in use so it is better
					 * not to move it right now. It can be moved in the next run.
					 */
				}
				
			} else {
				System.err.println(downloadDirectory.getName() + " is not a directory!");
			}
		}
		
		
	}

}
