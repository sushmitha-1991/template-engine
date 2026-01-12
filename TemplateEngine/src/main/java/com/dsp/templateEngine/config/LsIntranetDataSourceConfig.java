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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.dsp.templateEngine.repository.lsintra", entityManagerFactoryRef = "lsintraEntityManagerFactory", transactionManagerRef = "lsintraTransactionManager")
public class LsIntranetDataSourceConfig {

	@Bean(name = "lsintraDataSource")
	@ConfigurationProperties(prefix = "lsintra.datasource")
	public DataSource lsintraDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "lsintraEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean lsintraEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("lsintraDataSource") DataSource dataSource) {

		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");

		return builder.dataSource(dataSource).packages("com.dsp.templateEngine.entity.lsintra")
				.persistenceUnit("lsintra").properties(properties).build();
	}

	@Bean(name = "lsintraTransactionManager")
	public PlatformTransactionManager lsintraTransactionManager(
			@Qualifier("lsintraEntityManagerFactory") LocalContainerEntityManagerFactoryBean factoryBean) {
		return new JpaTransactionManager(factoryBean.getObject());
	}

	@Bean(name = "lsintraJdbcTemplate")
	public JdbcTemplate lsintraJdbcTemplate(@Qualifier("lsintraDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

}
