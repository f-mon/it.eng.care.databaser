package it.eng.care.databaser.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import it.eng.care.databaser.service.ConfigurationService;
import it.eng.care.databaser.service.events.ReloadedConfigurations;
import it.fmoon.fxapp.events.StartupApplication;

public class ConfigurationServiceImpl implements ConfigurationService {
	
	private File workspaceDir;
	
	private Properties configurationProperties;
	
	@Autowired
    private ApplicationEventPublisher applicationEventPublisher;
	
	@EventListener
	public void startup(StartupApplication event) {
		File workspaceDir = workspaceDir();
		if (!workspaceDir.exists()) {
			initializeWorkspace();
		} else {			
			this.workspaceDir = workspaceDir;
		}
		reloadConfigurationProperties();
	}
	
	public Properties getConfigurationProperties() {
		return this.configurationProperties;
	}

	public String getConfigurationPropertiesFileContent() {
		try {
			return IOUtils.toString(new FileInputStream(configPropFile()),Charset.defaultCharset());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void saveConfigurationPropertiesFileContent(String content) {
		try {
			FileOutputStream outputStream = new FileOutputStream(configPropFile());
			IOUtils.write(content, outputStream, Charset.defaultCharset());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		reloadConfigurationProperties();
	}

	public List<String> getChangelogLocations() {
		List<String> locations = getConfigurationProperties().entrySet().stream()
			.filter((e)->String.class.cast(e.getKey()).startsWith("changelogs.location."))
			.map(e->String.class.cast(e.getValue()))
			.collect(Collectors.toList());	
		return locations;
	}

	
	private void reloadConfigurationProperties() {
		this.configurationProperties = new Properties();
		try {
			this.configurationProperties.load(new FileInputStream(configPropFile()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		this.applicationEventPublisher.publishEvent(new ReloadedConfigurations(this.configurationProperties));
	}
	
	private void initializeWorkspace() {
		File workspaceDir = workspaceDir();
		boolean success = workspaceDir.mkdirs();
		if (!success) {
			throw new IllegalStateException("Cannot initialize workspace directory");
		}
		this.workspaceDir = workspaceDir;
	}
	
	private File configPropFile() {
		File file = new File(this.workspaceDir,"configuration.properties");
		return file;
	}
	
	private File workspaceDir() {
		String userHomeDir = System.getProperty("user.home");
		File workspaceDir = new File(userHomeDir, "databaser_workspace");
		return workspaceDir;
	}

}
