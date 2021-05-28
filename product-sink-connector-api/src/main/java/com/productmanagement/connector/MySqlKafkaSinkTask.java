package com.productmanagement.connector;

import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import com.productmanagement.connector.constants.ConnectorConstants;

public class MySqlKafkaSinkTask extends SinkTask{

	private ResourceBundle resourceBundle = ResourceBundle.getBundle("connector-dev");
	
	@Override
	public String version() {
		return resourceBundle.getString(ConnectorConstants.CONNECTOR_VERSION_RESOURCE_KEY);
	}

	@Override
	public void start(Map<String, String> props) {
		
	}

	@Override
	public void put(Collection<SinkRecord> records) {
		
	}

	@Override
	public void stop() {
		
	}

}
