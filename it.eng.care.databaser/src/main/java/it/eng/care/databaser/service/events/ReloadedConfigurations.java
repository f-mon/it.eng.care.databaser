package it.eng.care.databaser.service.events;

import java.util.Properties;

public class ReloadedConfigurations {
	
	private final Properties properties;

	public ReloadedConfigurations(Properties properties) {
		super();
		this.properties = properties;
	}

	public Properties getProperties() {
		return properties;
	}
	
	
	
}
