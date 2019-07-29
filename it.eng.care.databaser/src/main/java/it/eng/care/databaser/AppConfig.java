package it.eng.care.databaser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.eng.care.databaser.service.ChangeLogsService;
import it.eng.care.databaser.service.ConfigurationService;
import it.eng.care.databaser.service.DatabaseService;
import it.eng.care.databaser.service.impl.ChangeLogsServiceImpl;
import it.eng.care.databaser.service.impl.ConfigurationServiceImpl;
import it.eng.care.databaser.service.impl.DatabaseServiceImpl;

@Configuration
@EnableAutoConfiguration(exclude = {
	DataSourceAutoConfiguration.class, 
	DataSourceTransactionManagerAutoConfiguration.class, 
	HibernateJpaAutoConfiguration.class
})
public class AppConfig {
    
    @Bean 
    public ConfigurationService configurationService() {
    	return new ConfigurationServiceImpl();
    } 
    
    @Bean 
    public ChangeLogsService changeLogsService() {
    	return new ChangeLogsServiceImpl();
    } 
    
    @Bean 
    public DatabaseService databaseService() {
    	return new DatabaseServiceImpl();
    } 
    
    @Bean
    public ExecutorService executorService() {
    	ExecutorService pool = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setUncaughtExceptionHandler((Thread t, Throwable e)->{						
					e.printStackTrace();
				});
				thread.setName("Service_Thread");
				return thread;
			}
		});
    	return pool;
    }

}
