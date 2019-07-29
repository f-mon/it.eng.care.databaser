package it.eng.care.databaser.service.impl;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import it.eng.care.databaser.model.CareChangelogFile;
import it.eng.care.databaser.model.CareModule;
import it.eng.care.databaser.model.ResourceScanner;
import it.eng.care.databaser.service.ChangeLogsService;
import it.eng.care.databaser.service.ConfigurationService;
import it.eng.care.databaser.service.DatabaseService;
import it.eng.care.databaser.service.events.ReloadedDatabase;
import liquibase.changelog.ChangeLogHistoryService;
import liquibase.changelog.ChangeLogHistoryServiceFactory;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.changelog.RanChangeSet;
import liquibase.database.Database;
import liquibase.exception.LiquibaseException;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.AbstractResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;

public class ChangeLogsServiceImpl implements ChangeLogsService {

	@Autowired
	private ConfigurationService configurationService;
	
	@Autowired
	private DatabaseService databaseService;
	
	private AbstractResourceAccessor resourceAccessor;
	private ChangeLogParser changeLogParser;
	private ChangeLogParameters changeLogParameters;

	private ChangeLogHistoryService changeLogService;
	
	public  Collection<CareModule> scanAndGetModules() {

		ResourceScanner resourceScanner = new ResourceScanner();
		
		List<String> changelogLocations = configurationService.getChangelogLocations();
		changelogLocations.forEach(loc->{			
			resourceScanner.addLocation(Paths.get(loc));
		});
		resourceScanner.scan();
		
		return resourceScanner.getModules();
	}
	
	@EventListener
	public void onLoadedDatabase(ReloadedDatabase event) {
		resourceAccessor = new FileSystemResourceAccessor();
		Database database = this.databaseService.getDatabase();
		changeLogParameters = new ChangeLogParameters(database);
		changeLogService = ChangeLogHistoryServiceFactory.getInstance().getChangeLogService(database);
		
	}
	
	

	public void analyzeChangeLogFile(CareChangelogFile careChangelogFile) {
		
		if (changeLogParser==null) {
			try {			
				changeLogParser = ChangeLogParserFactory.getInstance().getParser(careChangelogFile.getExtension(),resourceAccessor);
			} catch (LiquibaseException e) {
				throw new RuntimeException(e);
			}
		}
		
		DatabaseChangeLog databaseChangeLog;
		try {
			databaseChangeLog = changeLogParser.parse(careChangelogFile.getLocation(), changeLogParameters , resourceAccessor);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		for (ChangeSet changeSet : databaseChangeLog.getChangeSets()) {			
			try {
				RanChangeSet ranChangeSet = changeLogService.getRanChangeSet(changeSet);
				
				System.out.println(" "+ranChangeSet!=null?ranChangeSet.getDateExecuted():"null");
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
		careChangelogFile.setParsedDatabaseChangeLog(databaseChangeLog);
	}

}
