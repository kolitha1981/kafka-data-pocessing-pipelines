package com.productmanagement.connector.service;

import java.sql.Connection;

public interface JDBCConnectionService {
	
	Connection getConnection();
	
	Connection closeConnection();

}
