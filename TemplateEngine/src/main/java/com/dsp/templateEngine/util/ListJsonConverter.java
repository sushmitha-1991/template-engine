/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * 
 */
@Converter
public class ListJsonConverter implements AttributeConverter<List<Map<String, Object>>, String> {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(List<Map<String, Object>> attribute) {
		if (attribute == null || attribute.isEmpty()) {
			return "[]"; // or return null if you prefer DB NULL instead of empty array
		}
		try {
			return objectMapper.writeValueAsString(attribute);
		} catch (Exception e) {
			throw new RuntimeException("Error converting list to JSON", e);
		}
	}

	@Override
	public List<Map<String, Object>> convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.trim().isEmpty()) {
			return Collections.emptyList(); // or return null if that's your desired behavior
		}
		try {
			return objectMapper.readValue(dbData, new TypeReference<List<Map<String, Object>>>() {
			});
		} catch (Exception e) {
			throw new RuntimeException("Error converting JSON to list", e);
		}
	}
}
