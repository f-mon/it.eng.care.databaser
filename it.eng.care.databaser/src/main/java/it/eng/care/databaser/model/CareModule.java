package it.eng.care.databaser.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class CareModule implements ChangeLogElement {

	private String moduleName;
	private Set<CareChangelogFile> changeLogFiles = new LinkedHashSet<>();

	public CareModule(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	
	public Collection<CareChangelogFile> getChangeLogFiles() {
		return Collections.unmodifiableCollection(changeLogFiles);
	}

	public void addChangelog(CareChangelogFile file) {
		this.changeLogFiles.add(file);
		file.setModule(this);
	}

}
