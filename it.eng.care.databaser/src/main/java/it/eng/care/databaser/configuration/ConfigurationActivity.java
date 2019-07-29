package it.eng.care.databaser.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import it.eng.care.databaser.service.impl.ConfigurationServiceImpl;
import it.fmoon.fxapp.mvc.AbstractActivity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConfigurationActivity extends AbstractActivity<ConfigurationActivityDef> {
	
	@FXML
	private TextArea textarea;
	
	@Autowired
	private ConfigurationServiceImpl configurationService;
	
	public ConfigurationActivity(ConfigurationActivityDef def) {
		super(def);
	}
	
	@FXML
    public void initialize() {
		String fileContent = configurationService.getConfigurationPropertiesFileContent();
		textarea.setText(fileContent);
    }
	
	@FXML 
	public void onSave(ActionEvent event) {
		String value = textarea.getText();
		configurationService.saveConfigurationPropertiesFileContent(value);
	}

}
