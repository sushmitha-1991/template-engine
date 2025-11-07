
package com.dsp.templateEngine.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.dsp.templateEngine.repository.lssupdate", 
entityManagerFactoryRef = "lssupdateEntityManagerFactory", transactionManagerRef = "lssupdateTransactionManager")
public class LssUpdatesLsDatasourceConfig {

	@Bean(name = "lssupdateDataSource")
	@ConfigurationProperties(prefix = "lssupdate.datasource")
	public DataSource lssupdateDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "lssupdateEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean lssupdateEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("lssupdateDataSource") DataSource dataSource) {

		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

		return builder.dataSource(dataSource).packages("com.dsp.templateEngine.entity.lssupdate").persistenceUnit("lssupdate")
				.properties(properties).build();
	}

	@Bean(name = "lssupdateTransactionManager")
	public PlatformTransactionManager lssupdateTransactionManager(
			@Qualifier("lssupdateEntityManagerFactory") LocalContainerEntityManagerFactoryBean factoryBean) {
		return new JpaTransactionManager(factoryBean.getObject());
	}
}

