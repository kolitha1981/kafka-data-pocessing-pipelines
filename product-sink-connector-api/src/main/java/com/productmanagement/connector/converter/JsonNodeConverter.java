package com.productmanagement.connector.converter;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.connect.data.ConnectSchema;
import org.apache.kafka.connect.data.Date;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Time;
import org.apache.kafka.connect.data.Timestamp;
import org.apache.kafka.connect.errors.DataException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonNodeConverter {
	public static JsonNode convertSinkRecordToJsonNode(final Schema schema, final Object sinkRecord) {
		if (sinkRecord == null) {
			if (schema == null) {
				return null;
			}
			if (schema.defaultValue() != null) {
				return convertSinkRecordToJsonNode(schema, schema.defaultValue());
			}
			if (schema.isOptional()) {
				return JsonNodeFactory.instance.nullNode();
			}
			throw new DataException("Conversion error: null value for field that is required and has no default value");
		}

		Object value = sinkRecord;
		if (schema != null && schema.name() != null) {
			final LogicalTypeConverter logicalConverter = TO_JSON_LOGICAL_CONVERTERS.get(schema.name());
			if (logicalConverter != null) {
				value = logicalConverter.convert(schema, sinkRecord);
			}
		}

		try {
			final Schema.Type schemaType;
			if (schema == null) {
				schemaType = ConnectSchema.schemaType(value.getClass());
				if (schemaType == null) {
					throw new DataException(
							"Java class " + value.getClass() + " does not have corresponding schema type.");
				}
			} else {
				schemaType = schema.type();
			}
			switch (schemaType) {
			case INT8:
				return JsonNodeFactory.instance.numberNode((Byte) value);
			case INT16:
				return JsonNodeFactory.instance.numberNode((Short) value);
			case INT32:
				return JsonNodeFactory.instance.numberNode((Integer) value);
			case INT64:
				return JsonNodeFactory.instance.numberNode((Long) value);
			case FLOAT32:
				return JsonNodeFactory.instance.numberNode((Float) value);
			case FLOAT64:
				return JsonNodeFactory.instance.numberNode((Double) value);
			case BOOLEAN:
				return JsonNodeFactory.instance.booleanNode((Boolean) value);
			case STRING:
				final CharSequence charSeq = (CharSequence) value;
				return JsonNodeFactory.instance.textNode(charSeq.toString());
			case BYTES:
				if (value instanceof byte[]) {
					return JsonNodeFactory.instance.binaryNode((byte[]) value);
				} else if (value instanceof ByteBuffer) {
					return JsonNodeFactory.instance.binaryNode(((ByteBuffer) value).array());
				} else {
					throw new DataException("Invalid type for bytes type: " + value.getClass());
				}
			case ARRAY: {
				@SuppressWarnings("rawtypes")
				final Collection collection = (Collection) value;
				final ArrayNode list = JsonNodeFactory.instance.arrayNode();
				for (final Object elem : collection) {
					final Schema valueSchema = schema == null ? null : schema.valueSchema();
					final JsonNode fieldValue = convertSinkRecordToJsonNode(valueSchema, elem);
					list.add(fieldValue);
				}
				return list;
			}
			case MAP: {
				final Map<?, ?> map = (Map<?, ?>) value;
				// If true, using string keys and JSON object; if false, using non-string keys
				// and Array-encoding
				boolean objectMode;
				if (schema == null) {
					objectMode = true;
					for (final Map.Entry<?, ?> entry : map.entrySet()) {
						if (!(entry.getKey() instanceof String)) {
							objectMode = false;
							break;
						}
					}
				} else {
					objectMode = schema.keySchema().type() == Schema.Type.STRING;
				}
				ObjectNode obj = null;
				ArrayNode list = null;
				if (objectMode) {
					obj = JsonNodeFactory.instance.objectNode();
				} else {
					list = JsonNodeFactory.instance.arrayNode();
				}
				for (final Map.Entry<?, ?> entry : map.entrySet()) {
					final Schema keySchema = schema == null ? null : schema.keySchema();
					final Schema valueSchema = schema == null ? null : schema.valueSchema();
					final JsonNode mapKey = convertSinkRecordToJsonNode(keySchema, entry.getKey());
					final JsonNode mapValue = convertSinkRecordToJsonNode(valueSchema, entry.getValue());

					if (objectMode) {
						obj.set(mapKey.asText(), mapValue);
					} else {
						list.add(JsonNodeFactory.instance.arrayNode().add(mapKey).add(mapValue));
					}
				}
				return objectMode ? obj : list;
			}
			case STRUCT: {
				final Struct struct = (Struct) value;

				if (struct.schema() != schema) {
					throw new DataException("Mismatching schema.");
				}
				if (schema == null) {
					throw new DataException("Schema is mandatory");
				}
				final ObjectNode obj = JsonNodeFactory.instance.objectNode();
				for (final Field field : schema.fields()) {
					obj.set(field.name(), convertSinkRecordToJsonNode(field.schema(), struct.get(field)));
				}
				return obj;
			}
			}

			throw new DataException("Couldn't convert " + value + " to JSON.");
		} catch (final ClassCastException e) {
			throw new DataException(
					"Invalid type for " + schema == null ? "" : schema.type() + ": " + value.getClass());
		}
	}

	private interface LogicalTypeConverter {
		Object convert(Schema schema, Object value);
	}

	private static final HashMap<String, LogicalTypeConverter> TO_JSON_LOGICAL_CONVERTERS = new HashMap<>();
	static {
		TO_JSON_LOGICAL_CONVERTERS.put(Decimal.LOGICAL_NAME, new LogicalTypeConverter() {
			@Override
			public Object convert(final Schema schema, final Object value) {
				if (!(value instanceof BigDecimal)) {
					throw new DataException(
							"Invalid type for Decimal, expected BigDecimal but was " + value.getClass());
				}
				return Decimal.fromLogical(schema, (BigDecimal) value);
			}
		});

		TO_JSON_LOGICAL_CONVERTERS.put(Date.LOGICAL_NAME, new LogicalTypeConverter() {
			@Override
			public Object convert(final Schema schema, final Object value) {
				if (!(value instanceof java.util.Date)) {
					throw new DataException("Invalid type for Date, expected Date but was " + value.getClass());
				}
				return Date.fromLogical(schema, (java.util.Date) value);
			}
		});

		TO_JSON_LOGICAL_CONVERTERS.put(Time.LOGICAL_NAME, new LogicalTypeConverter() {
			@Override
			public Object convert(final Schema schema, final Object value) {
				if (!(value instanceof java.util.Date)) {
					throw new DataException("Invalid type for Time, expected Date but was " + value.getClass());
				}
				return Time.fromLogical(schema, (java.util.Date) value);
			}
		});

		TO_JSON_LOGICAL_CONVERTERS.put(Timestamp.LOGICAL_NAME, new LogicalTypeConverter() {
			@Override
			public Object convert(final Schema schema, final Object value) {
				if (!(value instanceof java.util.Date)) {
					throw new DataException("Invalid type for Timestamp, expected Date but was " + value.getClass());
				}
				return Timestamp.fromLogical(schema, (java.util.Date) value);
			}
		});
	}
}
