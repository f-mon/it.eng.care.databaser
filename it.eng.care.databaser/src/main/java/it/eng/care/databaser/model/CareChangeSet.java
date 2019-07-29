package it.eng.care.databaser.model;

import liquibase.changelog.ChangeSet;

public class CareChangeSet implements ChangeLogElement {
	
	private final ChangeSet changeSet;

	public CareChangeSet(ChangeSet changeSet) {
		super();
		this.changeSet = changeSet;
	}

	public ChangeSet getChangeSet() {
		return changeSet;
	}

}
