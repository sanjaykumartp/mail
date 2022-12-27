//package com.ebs.config;
//
//import java.util.HashMap;
//
//import javax.persistence.EntityManagerFactory;
//import javax.sql.DataSource;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//@Configuration
//@EnableTransactionManagement
////@EnableJpaRepositories(entityManagerFactoryRef = "emsEntityManagerFactory", transactionManagerRef = "emsTransactionManager", basePackages = {
////		"com.ebs.repository1" })
//public class EmsCofig {
//
//	@Bean(name = "emsDataSource")
//	@ConfigurationProperties(prefix = "spring.ems.datasource")
//	public DataSource dataSource() {
//		return DataSourceBuilder.create().build();
//	}
//
//	@Bean(name = "emsEntityManagerFactory")
//	public LocalContainerEntityManagerFactoryBean bookEntityManagerFactory(EntityManagerFactoryBuilder builder,
//			@Qualifier("emsDataSource") DataSource dataSource) {
//		HashMap<String, Object> properties = new HashMap<>();
//		properties.put("hibernate.hbm2ddl.auto", "update");
//		properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
//		return builder.dataSource(dataSource)
//				.packages("com.ebs.entity").persistenceUnit("ems").build();
//	}//.properties(properties)
//
////	@Bean(name = "emsTransactionManager")
//	public PlatformTransactionManager emsTransactionManager(
//			@Qualifier("emsEntityManagerFactory") EntityManagerFactory emsEntityManagerFactory) {
//		return new JpaTransactionManager(emsEntityManagerFactory);
//	}
//}
