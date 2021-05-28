package com.productmanagement.connector.service;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.productmanagement.connector.exception.DataSourceConnectionFailedException;
import com.productmanagement.connector.jdbc.config.JDBCDataSourceConfig;

public class JDBCConnectionServiceImpl implements JDBCConnectionService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JDBCConnectionServiceImpl.class);
	
	private JDBCDataSourceConfig jdbcDataSourceConfig;
	
	public JDBCConnectionServiceImpl(JDBCDataSourceConfig jdbcDataSourceConfig) {
		this.jdbcDataSourceConfig = jdbcDataSourceConfig;
	}

	@Override
	public Connection getConnection() {
		Connection jdbcConnection = null;
		try {
			jdbcConnection = this.jdbcDataSourceConfig.getDataSourceConfig().getConnection();
		} catch (Exception e) {
			final String errorMesssage = "Error when creating a jdbc connection";
			LOGGER.error("Error when creating a jdbc connection", e);
			throw new DataSourceConnectionFailedException(errorMesssage, e);
		}
		return jdbcConnection;
	}

	@Override
	public Connection closeConnection() {
		return null;
	}

}
