package com.productmanagement.connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.productmanagement.connector.constants.ConnectorConstants;

public class MySqlKafkaSinkConnector extends SinkConnector {
	
	private ResourceBundle resourceBundle = ResourceBundle.getBundle("connector-dev");	
	private Map<String, String> connectorConfigProperties;
	private static final Logger LOGGER = LoggerFactory.getLogger(MySqlKafkaSinkConnector.class);

	@Override
	public String version() {
		return resourceBundle.getString(ConnectorConstants.CONNECTOR_VERSION_RESOURCE_KEY);
	}

	@Override
	public void start(Map<String, String> connectorConfigProperties) {
		LOGGER.info("@@@@@@@@@@ connectorConfigProperties" + connectorConfigProperties);
		connectorConfigProperties.forEach((key, value) -> {
			LOGGER.info("@@@@@@@@@@ Key: " + key);
			LOGGER.info("@@@@@@@@@@ Value: " + value);
		});
		try {
			this.connectorConfigProperties = connectorConfigProperties;
			new MysqlKafkaConnectorConfig(connectorConfigProperties);
		} catch (Exception e) {
			LOGGER.warn("@@@@@@ Error whane starting the connector: " + e.getMessage());
		}
	}

	@Override
	public Class<? extends Task> taskClass() {
		return MySqlKafkaSinkTask.class;
	}

	@Override
	public List<Map<String, String>> taskConfigs(int maxTasks) {
		LOGGER.info("@@@@@@@@@@ Task configs @@@@@@@@@@@@@@@@@@@@@@@");
		final List<Map<String, String>> taskConfigurations = new ArrayList<>();
		final Map<String, String> taskProps = new HashMap<>();
		taskProps.putAll(this.connectorConfigProperties);
		for (int i = 0; i < maxTasks; i++) {
			taskConfigurations.add(taskProps);
		}
		return taskConfigurations;
	}

	@Override
	public void stop() {
		LOGGER.info("@@@@@@@@@@ Stopping the connector @@@@@@@@@@@@@@@@@@@@@@@");
	}

	@Override
	public ConfigDef config() {
		LOGGER.info("@@@@@@@@@@ Calling config() of the connector @@@@@@@@@@@@@@@@@@@@@@@");
		return MysqlKafkaConnectorConfig.configurationdefinition();
	}

}
