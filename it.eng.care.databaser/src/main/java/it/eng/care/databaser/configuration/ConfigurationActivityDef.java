package it.eng.care.databaser.configuration;

import org.springframework.stereotype.Component;

import it.fmoon.fxapp.mvc.ActivityDef;

@Component
public class ConfigurationActivityDef extends ActivityDef<ConfigurationActivity> {

	public ConfigurationActivityDef() {
		super(ConfigurationActivity.class);
	}

	@Override
	public String getName() {
		return "ConfigurationActivity";
	}

}
