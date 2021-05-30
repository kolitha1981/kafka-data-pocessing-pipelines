package com.productmanagement.connector.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.productmanagement.connector.exception.ProductFailedToPersistException;
import com.productmanagement.connector.model.Product;

public class ProductServiceImpl implements ProductService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);
	private JDBCConnectionService connectionService;
	
	public ProductServiceImpl(final JDBCConnectionService connectionService) {
		this.connectionService = connectionService;
	}
	
	@Override
	public Product save(Product product) {
		try(final Connection connection  =  this.connectionService.getConnection();
			final PreparedStatement preparedStatement = connection
						.prepareStatement("INSERT INTO IM_PRODUCT_LOGS.PRODUCT (PRODUCT_NAME,DESCRIPTION,PRICE) VALUES(?,?,?)", 
						Statement.RETURN_GENERATED_KEYS)) {
			preparedStatement.setString(1, product.getProductName());
			preparedStatement.setString(2, product.getDescription());
			preparedStatement.setDouble(3, product.getPrice());
			int numberOfRowsEffected = preparedStatement.executeUpdate();
			if(numberOfRowsEffected> 0) {
				final ResultSet resultet =  preparedStatement.getGeneratedKeys();
				Long productId =  null;
				while(resultet.next()) {
					productId = resultet.getLong(1);
				}
				product.setProductId(productId);
				return product;
			}
		} catch (SQLException e) {
            final String errorMessage = "Error when saving product" + product;
            LOGGER.error(errorMessage, e);
            throw new ProductFailedToPersistException(errorMessage,e);
		}
		return product;
	}

}
