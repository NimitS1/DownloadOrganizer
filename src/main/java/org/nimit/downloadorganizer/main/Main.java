package org.nimit.downloadorganizer.main;


import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;


public class Main {
	
	private static KnowledgeBase kb = null;
	private static final Logger logger = Logger.getLogger("org.nimit.downloadorganizer.main");

	
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
				
				logger.info("Copied " + file.getName());
				return true;
			} catch(IOException ex) {
				throw ex;
			} catch(SecurityException sx) {
				throw sx;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			logger.severe("The mapping for " + extension + " is missing");
		}
		
		return false;
		
	}
	
	public static int processDirectory(File downloadDirectory) {
		int movedFiles = 0;
		
		File[] files = downloadDirectory.listFiles();
		if(files != null) {
			for(int i = 0; i < files.length;i++) {
				File currentFile = files[i];
				if(currentFile.canWrite()) {    
					try {
						if(moveFile(currentFile)) {
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
		} catch (IOException ex) {
			logger.severe("Faced an io exception");
			return;
		}
		
		
		//For each download folder, process every file
		for(String folder : kb.getDownloadFolders()) {
			File downloadDirectory = new File(folder);
			if(downloadDirectory.isDirectory()) {
				int movedFiles = processDirectory(downloadDirectory);
				logger.severe("Moved " + movedFiles + " files in " + downloadDirectory);
				
			} else {
				logger.severe(downloadDirectory.getName() + " is not a directory!");
			}
		}
		
		
	}

}
