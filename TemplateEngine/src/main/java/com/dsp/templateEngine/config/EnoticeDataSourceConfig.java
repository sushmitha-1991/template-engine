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

import jakarta.persistence.EntityManagerFactory;
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.dsp.templateEngine.repository.enotice",
    entityManagerFactoryRef = "enoticeEntityManagerFactory",
    transactionManagerRef = "enoticeTransactionManager"
)
public class EnoticeDataSourceConfig {

    @Bean(name = "enoticeDataSource")
    @ConfigurationProperties(prefix = "enotice.datasource")
    public DataSource enoticeDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "enoticeEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean enoticeEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("enoticeDataSource") DataSource dataSource) {

        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");

        return builder
                .dataSource(dataSource)
                .packages("com.dsp.templateEngine.entity.enoticeboard")
                .persistenceUnit("enotice")
                .properties(props)
                .build();
    }

    @Bean(name = "enoticeTransactionManager")
    public PlatformTransactionManager enoticeTransactionManager(
            @Qualifier("enoticeEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
