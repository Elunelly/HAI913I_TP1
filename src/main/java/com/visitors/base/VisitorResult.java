package com.visitors.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisitorResult {
	
	private static final Logger logger = LoggerFactory.getLogger(VisitorResult.class);
	
	private final String visitorName;
	private final Map<String,Object> data = new HashMap<>();
	private boolean isSuccess = true;
	private String errorMessage = "";
	
	public VisitorResult(String visitorName) {
		this.visitorName = visitorName;
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
		this.isSuccess = errorMessage.isEmpty();
		this.errorMessage = errorMessage;
		if (!isSuccess)
			logger.error(errorMessage);
		else
			logger.debug("Reset on no-error");
	}
	
	public String getVisitorName() {return this.visitorName;}
	
	public boolean isSuccessful() {return this.isSuccess;}
	
	public String getErrorMessage() {return this.errorMessage;}
	
	@Override
	public String toString() {
		return ("VisitorResult{"
				+ "name=%s, "
				+ "successful=%s, "
				+ "data=%s}")
				.formatted(visitorName, isSuccess, data);
	}

}
