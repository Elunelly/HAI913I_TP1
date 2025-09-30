package com.visitors.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VisitorResult {
	
	private final String visitorName;
	private final Map<String,Object> data = new HashMap<>();
	private boolean isSuccess = true;
	private String errorMessage = "";
	
	public VisitorResult(String visitorName) {
		this.visitorName = visitorName;
	}
	
	public boolean addData(String key, Object value) {
		this.data.put(key, value);
		return this.data.get(key).equals(value);
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
		this.isSuccess = false;
		this.errorMessage = errorMessage;
	}
	
	public String getVisitorName() {return this.visitorName;}
	
	public boolean isSuccessful() {return this.isSuccess;}
	
	public String getErrorMessage() {return this.errorMessage;}
	
	@Override
	public String toString() {
		return "VisitorResult{visitor='%s', successful=%s, dataKeys=%s}"
				.formatted(getVisitorName(),isSuccessful(),getData().toString());
	}

}
