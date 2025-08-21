/**
 * 
 * @author Mrinal Lakhanpal
 * @version 1.0
 */
package com.dsp.templateEngine.util;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * 
 */
@Converter
public class LongListJsonConverter implements AttributeConverter<List<Long>, String> {

	private static final ObjectMapper mapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(List<Long> attribute) {
		if (attribute == null || attribute.isEmpty()) {
			return "[]"; // or return null if you want to store DB NULL
		}
		try {
			return mapper.writeValueAsString(attribute);
		} catch (Exception e) {
			throw new RuntimeException("Failed to convert list of longs to JSON", e);
		}
	}

	@Override
	public List<Long> convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.trim().isEmpty()) {
			return Collections.emptyList(); // or return null if that's preferred
		}
		try {
			return mapper.readValue(dbData, new TypeReference<List<Long>>() {
			});
		} catch (Exception e) {
			throw new RuntimeException("Failed to convert JSON to list of longs", e);
		}
	}
}
