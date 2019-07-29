package it.eng.care.databaser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import it.eng.care.databaser.page.DatabaserMainPage;
import it.fmoon.fxapp.FxApplication;
import it.fmoon.fxapp.components.menu.MenuManager;
import it.fmoon.fxapp.components.menu.PageMenuItem;
import javafx.application.Application;

@ComponentScan(basePackageClasses = {
	AppConfig.class
})
public class DatabaserApp extends FxApplication {

	@Autowired
	private MenuManager menuManager;
	
	@Autowired
	private DatabaserMainPage databaserMainPage;
	
	public static void main(String[] args) {
		Application.launch(DatabaserApp.class,args);
	}
	
	public void afterInitialize() {
		menuManager.addToApplicationMenu(PageMenuItem.builder()
			.pageDef(databaserMainPage)
			.build());
	}
	
}
