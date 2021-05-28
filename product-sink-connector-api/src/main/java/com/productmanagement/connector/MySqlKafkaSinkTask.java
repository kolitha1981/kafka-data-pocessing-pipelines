package com.productmanagement.connector;

import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.productmanagement.connector.constants.ConnectorConstants;
import com.productmanagement.connector.jdbc.config.JDBCDataSourceConfig;
import com.productmanagement.connector.service.JDBCConnectionService;
import com.productmanagement.connector.service.JDBCConnectionServiceImpl;

public class MySqlKafkaSinkTask extends SinkTask{

	private ResourceBundle resourceBundle = ResourceBundle.getBundle("connector-dev");
	private static final Logger LOGGER = LoggerFactory.getLogger(MySqlKafkaSinkTask.class);
	
	@Override
	public String version() {
		return resourceBundle.getString(ConnectorConstants.CONNECTOR_VERSION_RESOURCE_KEY);
	}

	@Override
	public void start(Map<String, String> connectorConfigProperties) {
		LOGGER.info("@@@@@ Intiaalizing the connector services =========>");
		final MysqlKafkaConnectorConfig connectorConfig =  new MysqlKafkaConnectorConfig(connectorConfigProperties);
		final JDBCDataSourceConfig dataSourceConfig =  new JDBCDataSourceConfig(connectorConfig);
		final JDBCConnectionService connectionService =  new JDBCConnectionServiceImpl(dataSourceConfig);
		
	}

	@Override
	public void put(Collection<SinkRecord> records) {
		
	}

	@Override
	public void stop() {
		
	}

}
