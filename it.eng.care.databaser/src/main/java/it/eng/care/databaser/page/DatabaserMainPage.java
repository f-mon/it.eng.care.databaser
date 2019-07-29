package it.eng.care.databaser.page;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.eng.care.databaser.applyer.ApplyerActivityDef;
import it.eng.care.databaser.configuration.ConfigurationActivityDef;
import it.fmoon.fxapp.components.menu.ActivityMenuItem;
import it.fmoon.fxapp.components.menu.AppMenuItem;
import it.fmoon.fxapp.mvc.ActivityDef;
import it.fmoon.fxapp.mvc.PageDef;

@Component
public class DatabaserMainPage extends PageDef {

	@Autowired
	private ConfigurationActivityDef configAct;
	
	@Autowired
	private ApplyerActivityDef applyerAct;
	
	@Override
	public String getName() {
		return "databaser";
	}
	
	@Override
	protected void definePageMenu(List<AppMenuItem> pageMenuDefinition) {
		pageMenuDefinition.add(
			ActivityMenuItem.builder()
				.activityDef(configAct)
				.label("Configure")
				.icon("icona")
				.build()
		);
		pageMenuDefinition.add(
			ActivityMenuItem.builder()
				.activityDef(applyerAct)
				.label("Apply Changelogs")
				.icon("icona")
				.build()
			);
	}

	@Override
	public ActivityDef<?> getInitialActivity() {
		return configAct;
	}

}
