package it.eng.care.databaser.service;

import liquibase.database.Database;

public interface DatabaseService {
	
	Database getDatabase();
	
}
