package it.eng.care.databaser.model;

public interface CareModuleRepository {

	CareModule getOrCreateModule(String moduleName);

}
