package it.eng.care.databaser.service.impl;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import it.eng.care.databaser.service.ConfigurationService;
import it.eng.care.databaser.service.DatabaseService;
import it.eng.care.databaser.service.events.ReloadedConfigurations;
import it.eng.care.databaser.service.events.ReloadedDatabase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;

public class DatabaseServiceImpl implements DatabaseService {
	
	@Autowired
	private ConfigurationService configurationService;

	@Autowired
    private ApplicationEventPublisher applicationEventPublisher;
	
	private Database database;
	
	public DatabaseServiceImpl() {
		super();
	}

	@EventListener
	public void onConfigLoaded(ReloadedConfigurations event) {
		try {
			DataSource dataSource = initDataSource(event.getProperties());
			DatabaseConnection conn = new JdbcConnection(dataSource.getConnection());
			database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(conn);		
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Database created");
		applicationEventPublisher.publishEvent(new ReloadedDatabase());
	}
	
	
	@Override
	public Database getDatabase() {
		return database;
	}
	
	public DataSource initDataSource(Properties env) {
		DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(env.getProperty("dataSource.driverClassName"));
        dataSourceBuilder.url(env.getProperty("dataSource.url"));
        dataSourceBuilder.username(env.getProperty("dataSource.username"));
        dataSourceBuilder.password(env.getProperty("dataSource.password"));
        return dataSourceBuilder.build();
	}

}
