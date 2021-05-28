package com.productmanagement.connector.constants;

public class ConnectorConstants {
	
	private ConnectorConstants() {}
	
	public static final String CONNECTOR_VERSION_RESOURCE_KEY = "mysql.sinkconnector.version";
	
	public static final String MYSQL_USERNAME= "mysql.username";
	public static final String MYSQL_USERNAME_DOC = "Username of the mysql datasource.";
	public static final String MYSQL_PASSWORD= "mysql.password";
	public static final String MYSQL_PASSWORD_DOC = "Password of the mysql datasource.";
	public static final String MYSQL_SERVER_NAME= "mysql.server.name";
	public static final String MYSQL_SERVER_NAME_DOC = "Server hosting the mysql datasource.";
	public static final String MYSQL_SERVER_PORT= "mysql.server.port";
	public static final String MYSQL_SERVER_PORT_DOC = "Server port hosting the mysql datasource.";
	public static final String MYSQL_SERVER_DATABASE_NAME= "mysql.server.dbname";
	public static final String MYSQL_SERVER_DATABASE_NAME_DOC = "Database  the mysql datasource.";
	public static final String KAFKA_MYSQL_TABLE_VALUE_MAPPINGS = "kafka.mysql.value.mapppings";
	public static final String KAFKA_MYSQL_TABLE_VALUE_MAPPINGS_DOC = "The values of the kafka topic that ia mapped to mysql datasource.";

}
