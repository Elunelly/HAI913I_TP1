package com.visitors.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisitorResult {
	
	private static final Logger logger = LoggerFactory.getLogger(VisitorResult.class);
	
	private final String name;
	private final Map<String,Object> data = new HashMap<>();
	private boolean isSuccess = true;
	private String errorMessage;
	
	public VisitorResult(String visitorName) {
		this.name = visitorName;
	}
	
	public void addData(String key, Object value) {
		Object old = data.get(key);
		data.put(key, value);
		if (old==null)
			logger.debug("Added entry: '"+key+"'="+value);
		else
			logger.debug("Change value of '%s': %s -> %s".formatted(key,old,value));
	}
	
	public Object getDataBy(String key) {
		return this.data.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getDataBy(String key, Class<T> type) {
		Object value = getDataBy(key);
		if (value!=null && type.isInstance(value)) return (T) value;
		return null;
	}
	
	public boolean containsKey(String key) {
		return this.data.containsKey(key);
	}
	
	public Map<String,Object> getData() {return Collections.unmodifiableMap(this.data);}
	public Map<String,Object> copyData() {return new HashMap<>(this.data);}
	
	public void setError(String errorMessage) {
		this.isSuccess = errorMessage==null || errorMessage.isEmpty();
		this.errorMessage = errorMessage;
		if (!isSuccess)
			logger.error(errorMessage);
		else
			logger.debug("Reset on no-error");
	}
	
	public String getVisitorName() {return this.name;}
	
	public boolean isSuccessful() {return this.isSuccess;}
	
	public String getErrorMessage() {return this.errorMessage;}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		VisitorResult that = (VisitorResult) obj;
		return 
			Objects.equals(this.name, that.name) &&
			Objects.equals(this.data, that.data) &&
			Objects.equals(this.isSuccess, that.isSuccess)
		;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, data, isSuccess);
	}
	
	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "Visitorname=%s, "
				+ "successful=%s, "
				+ "data=%s}")
				.formatted(name, isSuccess, data);
	}

}
