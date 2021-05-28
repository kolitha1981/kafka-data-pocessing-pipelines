package com.productmanagement.connector.jdbc.config;

import com.productmanagement.connector.MysqlKafkaConnectorConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class JDBCDataSourceConfig {
	
	private HikariDataSource hikariDataSource;
	
	public JDBCDataSourceConfig(MysqlKafkaConnectorConfig connectorConfig) {
		final HikariConfig hikariConfiguration = new HikariConfig();
		final StringBuilder urlBuilder =  new StringBuilder("jdbc:mysql://")
				.append(connectorConfig.getMySqlHostName()).append(":")
				.append(connectorConfig.getMySqlServerPort()).append("/")
				.append(connectorConfig.getMySqlDatabaseName());
		hikariConfiguration.setJdbcUrl(urlBuilder.toString());
		hikariConfiguration.setDriverClassName("com.mysql.jdbc.Driver");
		hikariConfiguration.setUsername(connectorConfig.getMySqlUserName());
		hikariConfiguration.setPassword(connectorConfig.getMySqlPassword());
		hikariConfiguration.addDataSourceProperty("maximumPoolSize", "10");
		hikariConfiguration.addDataSourceProperty("minimumIdle", "2");
		hikariConfiguration.addDataSourceProperty("cachePrepStmts", "true");
		hikariConfiguration.addDataSourceProperty("useServerPrepStmts", "true");
		hikariConfiguration.addDataSourceProperty("prepStmtCacheSize", "250");
		hikariConfiguration.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		this.hikariDataSource =  new HikariDataSource(hikariConfiguration);
	}
	
	public HikariDataSource getDataSourceConfig() {
		return this.hikariDataSource;
	}

}
