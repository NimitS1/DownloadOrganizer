package org.nimit.downloadorganizer.main;


import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {
	
	private static KnowledgeBase kb = null;
	private static final Logger logger = Logger.getLogger("org.nimit.downloadorganizer.main");
	public static final int ok = 1;
	public static final int notADirectory = -1;
	public static final int destinationDoesNotExist = -2;
	
	private static int move(File file,String destination) throws IOException, InterruptedException {

		CopyOption option = StandardCopyOption.REPLACE_EXISTING;
		Files.move(Paths.get(file.getAbsolutePath()), Paths.get(destination + "\\" + file.getName()), option);
		
		File destinationFile = new File(destination+"\\"+file.getName());
		
		int counter = 0;
		while(!destinationFile.canWrite()) {
			//This is a hacky way to ensure that the copying of large files is completed
			counter++;
			if(counter == 5) {
				System.out.println(file.getName() + " is a big file. Taking some time to transfer");
			}
			Thread.sleep(2000);
		}		
		return ok;
	}
	
	private static int moveFile(File file,String destination) throws IOException, InterruptedException {
		
		File destinationDirectory = new File(destination);
		if(destinationDirectory.exists()) {
			
			//Highly unlikely to fail but still!
			if(destinationDirectory.isDirectory()) {
				return move(file, destination);
			} else {
				return notADirectory;
			}
			
		} else {
			
			//Try to create the directory
			try {

				Files.createDirectories(Paths.get(destinationDirectory.getAbsolutePath()));
				
			} catch (IOException ex) {
				logger.log(Level.SEVERE, ex.toString());
				return destinationDoesNotExist;
			}

			return move(file,destination);
			
		}

	}

	
	private static boolean processFile(File file) throws IOException, InterruptedException {

		String[] parts = file.getName().split("\\.");
		String extension = parts[parts.length -1 ];
		if(kb.getFileTypeMap().containsKey(extension)){
			String destinationFolder = kb.getFileTypeMap().get(extension);
			try {

				int status = moveFile(file,destinationFolder);
				if(status == ok) {
				
					logger.info("Copied " + file.getName());
					return true;
				} else if(status == notADirectory) {
					logger.severe(destinationFolder + " is not a directory!");
					return false;
				} else if(status == destinationDoesNotExist) {
					logger.severe(destinationFolder + " does not exist and could not create it");
					return false;
				}
			} catch(IOException ex) {
				throw ex;
			} catch(SecurityException sx) {
				throw sx;
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			logger.severe("The mapping for " + extension + " is missing");
		}
		
		return false;
		
	}
	
	public static int processDirectory(File downloadDirectory) {
		int movedFiles = 0;
		
		System.out.println("Started processing " + downloadDirectory);
		File[] files = downloadDirectory.listFiles();
		if(files != null) {
			for(int i = 0; i < files.length;i++) {
				File currentFile = files[i];
				
				//Bug!! Read only files will be skipped
				if(currentFile.canWrite() && !currentFile.isDirectory()) {    
					try {
						if(processFile(currentFile)) {
							movedFiles++;
						}
					} catch(Exception ex) {
						logger.severe(ex.toString());
					}
				}
				/*
				 * If we can't write the file, it is probably in use so it is better
				 * not to attempt to move it right now. It can be moved in the next run.
				 */
			}
		}
		return movedFiles;
	}
	
	public static void main(String[] args) {
		
		String mappingPath = "";
		if(args.length < 1) {
			logger.severe("Usage: DownloadOrganizer <mapping_file_path>");
			return;
		}
		mappingPath = args[0];
		try {
			
			//Parse the mapping file
			kb = KnowledgeBase.load(mappingPath);

		} catch (NoSuchFileException ex) {
			logger.severe("The configuration file does not exist");
			return;
		}
		
		
		//For each download folder, process every file
		for(String folder : kb.getDownloadFolders()) {
			File downloadDirectory = new File(folder);
			if(downloadDirectory.isDirectory()) {
				int movedFiles = processDirectory(downloadDirectory);
				logger.severe("Moved " + movedFiles + " files from " + downloadDirectory);
				System.out.println("Moved " + movedFiles + " files from " + downloadDirectory);
				
			} else {
				logger.severe(downloadDirectory.getName() + " is not a directory!");
			}
		}
		
		
	}

}
