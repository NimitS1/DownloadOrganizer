package org.nimit.downloadorganizer.main;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.yaml.snakeyaml.Yaml;

public class KnowledgeBase {
	
	private static Logger logger = Logger.getLogger("org.nimit.downloadmanager.knowledgeBase");
	
	private Map<String,String> fileTypeMap;
	private List<String> downloadFolders;
	
	public static KnowledgeBase load(String filePath) throws NoSuchFileException {
		try {
			
			Yaml yaml = new Yaml();  
	        InputStream in = Files.newInputStream( Paths.get( filePath ) );
	        return yaml.loadAs( in, KnowledgeBase.class );
		
		} catch (NoSuchFileException ex) {
			throw ex;
		} catch (Exception ex) {

			logger.log(Level.SEVERE, ex.toString());
			return null;
		
		}
	}

	public Map<String, String> getFileTypeMap() {
		return fileTypeMap;
	}

	public void setFileTypeMap(Map<String, String> fileTypeMap) {
		this.fileTypeMap = fileTypeMap;
	}

	public List<String> getDownloadFolders() {
		return downloadFolders;
	}

	public void setDownloadFolders(List<String> downloadFolders) {
		this.downloadFolders = downloadFolders;
	}
	
}
