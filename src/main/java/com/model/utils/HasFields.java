package com.model.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.structural.FieldInfo;

public interface HasFields {
	
	static final Logger logger = LoggerFactory.getLogger(HasFields.class);
	
	List<FieldInfo> fields();
	
	default List<FieldInfo> getFields() {return Collections.unmodifiableList(fields());}
	
	default List<FieldInfo> copyFields() {return new ArrayList<>(fields());}
	
	default boolean addField(FieldInfo fieldInfo) {
		if (fieldInfo!=null && !fields().contains(fieldInfo) && fields().add(fieldInfo)) {
			logger.debug("Field added: %s".formatted(fieldInfo));
			return true;
		}
		return false;
	}
	
	default boolean addAllFields(List<FieldInfo> fields) {
		return fields.stream().filter(c -> addField(c)).count() > 0;
	}
	
	default boolean removeField(FieldInfo fieldInfo) {
		if (fieldInfo!=null && fields().remove(fieldInfo)) {
			logger.debug("Field removed: %s".formatted(fieldInfo));
			return true;
		}
		return false;
	}
	
	default boolean removeAllFields(List<FieldInfo> fields) {
		return fields.stream().filter(c -> removeField(c)).count() > 0;
	}
	
	default FieldInfo getField(String name) {
		for(FieldInfo fieldInfo : fields()) {
			if (name.equalsIgnoreCase(fieldInfo.getName())) return fieldInfo;
		}
		return null;
	}
	
	default boolean hasField(String name) {
		return getField(name) != null;
	}
	
	default boolean hasFields() {
		return !fields().isEmpty();
	}

}
