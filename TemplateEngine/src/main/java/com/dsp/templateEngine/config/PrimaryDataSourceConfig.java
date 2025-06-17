package com.dsp.templateEngine.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "com.dsp.templateEngine.entity")
@EnableJpaRepositories(basePackages = "com.dsp.templateEngine.repository", 
entityManagerFactoryRef = "primaryEntityManagerFactory", transactionManagerRef = "primaryTransactionManager")
public class PrimaryDataSourceConfig {

	@Primary
	@Bean(name = "primaryDataSource")
	@ConfigurationProperties(prefix = "spring.datasource") // Default datasource for PostgreSQL
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Primary
	@Bean(name = "primaryEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("primaryDataSource") DataSource dataSource) {

		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		properties.put("hibernate.hbm2ddl.auto", "none");
		properties.put("hibernate.show_sql", true);
		properties.put("hibernate.format_sql", true);
		properties.put("hibernate.physical_naming_strategy",
				"org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
		properties.put("hibernate.default_schema", "authentication");

		// 🆕 Add this line to specify the stringtype
		properties.put("hibernate.hikari.dataSourceProperties.stringtype", "unspecified");

		return builder.dataSource(dataSource).packages("com.dsp.templateEngine.entity").persistenceUnit("primary")
				.properties(properties).build();
	}

	@Primary
	@Bean(name = "primaryTransactionManager")
	public PlatformTransactionManager transactionManager(
			@Qualifier("primaryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
}
