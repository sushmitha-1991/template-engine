/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
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
@EnableJpaRepositories(basePackages = "com.dsp.templateEngine.repository.nelss", 
entityManagerFactoryRef = "nelssEntityManagerFactory", transactionManagerRef = "nelssTransactionManager")
public class NelssDataSourceConfig {

	@Bean(name = "nelssDataSource")
	@ConfigurationProperties(prefix = "nelss.datasource")
	public DataSource nelssDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "nelssEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean nelssEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("nelssDataSource") DataSource dataSource) {

		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");

		return builder.dataSource(dataSource).packages("com.dsp.templateEngine.entity.nelss").persistenceUnit("nelss")
				.properties(properties).build();
	}

	@Bean(name = "nelssTransactionManager")
	public PlatformTransactionManager nelssTransactionManager(
			@Qualifier("nelssEntityManagerFactory") LocalContainerEntityManagerFactoryBean factoryBean) {
		return new JpaTransactionManager(factoryBean.getObject());
	}
}
