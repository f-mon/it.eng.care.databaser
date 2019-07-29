package it.eng.care.databaser.service;

import java.util.Collection;

import it.eng.care.databaser.model.CareChangelogFile;
import it.eng.care.databaser.model.CareModule;

public interface ChangeLogsService {

	void analyzeChangeLogFile(CareChangelogFile changeLogFile);

	Collection<CareModule> scanAndGetModules();

}
