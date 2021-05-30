package com.productmanagement.connector;

import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productmanagement.connector.constants.ConnectorConstants;
import com.productmanagement.connector.converter.JsonNodeConverter;
import com.productmanagement.connector.exception.ProductFailedToPersistException;
import com.productmanagement.connector.jdbc.config.JDBCDataSourceConfig;
import com.productmanagement.connector.model.Product;
import com.productmanagement.connector.service.JDBCConnectionService;
import com.productmanagement.connector.service.JDBCConnectionServiceImpl;
import com.productmanagement.connector.service.ProductService;
import com.productmanagement.connector.service.ProductServiceImpl;
import com.productmanagement.connector.service.ServiceFactory;
import com.productmanagement.connector.service.ServiceFactoryImpl;

public class MySqlKafkaSinkTask extends SinkTask{

	private ResourceBundle resourceBundle = ResourceBundle.getBundle("connector-dev");
	private ObjectMapper objectMapper =  new ObjectMapper();
	private ServiceFactory serviceFactory =  new ServiceFactoryImpl();
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
		final ProductService productService =  new ProductServiceImpl(connectionService);
		this.serviceFactory.registerService(ProductService.class, productService);
		
	}

	@Override
	public void put(Collection<SinkRecord> records) {
		LOGGER.info("@@@@@ Processing the sink records =========>");
		records.forEach(record -> {
			final JsonNode jsonNode = JsonNodeConverter.convertSinkRecordToJsonNode(record.valueSchema(), record.value());
			try {
				final Product product = objectMapper.reader().treeToValue(jsonNode, Product.class);
				final ProductService productService = this.serviceFactory.getService(ProductService.class);
				productService.save(product);
			} catch (JsonProcessingException e) {
				String errorMessage = "Error processing the sink record ======>";
				LOGGER.error(errorMessage, e);
				throw new ProductFailedToPersistException(errorMessage,e);
			}
		});
	}

	@Override
	public void stop() {
		LOGGER.info("@@@@@ Processing the sink records =========>");
		this.serviceFactory.deregisterServices();
		this.serviceFactory =  null;
		this.resourceBundle =  null;
	}

}
