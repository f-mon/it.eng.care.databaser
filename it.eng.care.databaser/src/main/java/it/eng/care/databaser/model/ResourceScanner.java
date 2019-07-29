package it.eng.care.databaser.model;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;


public class ResourceScanner implements CareModuleRepository {

	private List<Path> locations = new ArrayList<>();
	
	private Map<String,CareModule> modules = new HashMap<>();
	private List<CareChangelogFile> changelogFiles = new ArrayList<>();

	public ResourceScanner addLocation(Path location) {
		this.locations.add(location);
		return this;
	}
	
	public void scan() {
		this.modules = new HashMap<>();
		this.changelogFiles = new ArrayList<>();
		this.locations.stream().forEach(this::scan);
	}

	public void includeChangeLogFile(Path file) {
		
		CareChangelogFile newChangelogFile = CareChangelogFile.fromFile(file,this);
		
		System.out.println("changelog "+changelogFiles.size()+"  "+file.toString());
		changelogFiles.add(newChangelogFile);
	}
	
	@Override
	public CareModule getOrCreateModule(String moduleName) {
		CareModule careModule = this.modules.get(moduleName);
		if (careModule==null) {
			this.modules.put(moduleName, careModule = new CareModule(moduleName));
		}
		return careModule;
	}
	
	
	public List<CareChangelogFile> getChangelogFiles() {
		return changelogFiles;
	}
	
    public Collection<CareModule> getModules() {
		return Collections.unmodifiableCollection(modules.values());
	}
    
	private void scan(Path path) {
		try {
			Files.walkFileTree(path, new ResourceFinder());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
  
	private void scanJar(Path jarFile) throws IOException {
    	
//    	Map<String, String> zipProperties = new HashMap<>();
//    	zipProperties.put("create", "false");
//    	zipProperties.put("encoding", "UTF-8");
    	
    	//URI zipFile = URI.create("jar:" + jarFile.toUri().getPath());
    	try (FileSystem zipfs = FileSystems.newFileSystem(jarFile,null)) {
    	  Path rootPath = zipfs.getPath("/");
    	  Files.walkFileTree(rootPath, new ResourceFinder());
    	}
	}

	private class ResourceFinder extends SimpleFileVisitor<Path> {

	    @Override
	    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
	            throws IOException {
	        
	    	//System.out.println("FILE "+count+"  "+file.toString());
	    	
	    	if ("jar".equals(FilenameUtils.getExtension(file.getFileName().toString()))) {

	        	System.out.println("JAR   "+file.toString());
	    		scanJar(file);
	    	}
	    	
	    	if (CareChangelogFile.isAChangelog(file)) {
	    		includeChangeLogFile(file);
	    	}
	    	
	        return FileVisitResult.CONTINUE;
	    }
	    
		@Override
	    public FileVisitResult visitFileFailed(Path file, IOException io) {   
	    	System.out.println("ERROR ==> "+file.getFileName()+"  "+io.getMessage());
	        return FileVisitResult.SKIP_SUBTREE;
	    }
	    
	}

}
