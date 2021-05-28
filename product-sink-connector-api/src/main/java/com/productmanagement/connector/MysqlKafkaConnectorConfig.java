package com.productmanagement.connector;

import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.productmanagement.connector.constants.ConnectorConstants;

public class MysqlKafkaConnectorConfig extends AbstractConfig {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MysqlKafkaConnectorConfig.class);

	public MysqlKafkaConnectorConfig(ConfigDef definition, Map<?, ?> connectorConfigProperties) {
		super(definition, connectorConfigProperties);
		LOGGER.info("@@@@ Constructor MysqlConnectorConfig(ConfigDef definition, Map<?, ?> connectorConfigProperties)");
		final String userName = String.valueOf(connectorConfigProperties.get(ConnectorConstants.MYSQL_USERNAME));
		LOGGER.info("@@@@ Username:"+ userName);
		final String password = String.valueOf(connectorConfigProperties.get(ConnectorConstants.MYSQL_PASSWORD));
		LOGGER.info("@@@@ Password:"+ password);
		final String serverHostName = String
				.valueOf(connectorConfigProperties.get(ConnectorConstants.MYSQL_SERVER_NAME_DOC));
		LOGGER.info("@@@@ Server host:"+ serverHostName);
		final String serverPort = String
				.valueOf(connectorConfigProperties.get(ConnectorConstants.MYSQL_SERVER_PORT));
		LOGGER.info("@@@@ Server port:"+ serverPort);
		final String databaseName = String
				.valueOf(connectorConfigProperties.get(ConnectorConstants.MYSQL_SERVER_DATABASE_NAME));
		LOGGER.info("@@@@ DatabaseName:"+ databaseName);
	}
	
	public MysqlKafkaConnectorConfig(Map<?, ?> connectorConfigProperties) {
		super(configurationdefinition(), connectorConfigProperties);
	}
	
	public static ConfigDef configurationdefinition() {
		LOGGER.info("@@@@ ConfigDef configurationdefinition() ===============>");
		return new ConfigDef()
				.define(ConnectorConstants.MYSQL_USERNAME, Type.STRING, Importance.HIGH,
						ConnectorConstants.MYSQL_USERNAME_DOC)
				.define(ConnectorConstants.MYSQL_PASSWORD, Type.STRING, Importance.HIGH,
						ConnectorConstants.MYSQL_PASSWORD_DOC)
				.define(ConnectorConstants.MYSQL_SERVER_NAME, Type.STRING, Importance.HIGH,
						ConnectorConstants.MYSQL_SERVER_NAME_DOC)
				.define(ConnectorConstants.MYSQL_SERVER_DATABASE_NAME, Type.STRING, Importance.HIGH,
						ConnectorConstants.MYSQL_SERVER_DATABASE_NAME_DOC)
				.define(ConnectorConstants.MYSQL_SERVER_PORT, Type.STRING, Importance.HIGH,
						ConnectorConstants.MYSQL_SERVER_PORT)
				.define(ConnectorConstants.KAFKA_MYSQL_TABLE_VALUE_MAPPINGS, Type.LIST, Importance.HIGH,
						ConnectorConstants.KAFKA_MYSQL_TABLE_VALUE_MAPPINGS_DOC);
	}
	
	public String getMySqlUserName() {
		return this.getString(ConnectorConstants.MYSQL_USERNAME);
	}

	public String getMySqlPassword() {
		return this.getString(ConnectorConstants.MYSQL_PASSWORD);
	}

	public String getMySqlDatabaseName() {
		return this.getString(ConnectorConstants.MYSQL_SERVER_DATABASE_NAME);
	}

	public String getMySqlHostName() {
		return this.getString(ConnectorConstants.MYSQL_SERVER_NAME);
	}

	public String getMySqlServerPort() {
		return this.getString(ConnectorConstants.MYSQL_SERVER_PORT);
	}

	public List<String> getMySqlValueMappings() {
		return this.getList(ConnectorConstants.KAFKA_MYSQL_TABLE_VALUE_MAPPINGS);
	}

}
