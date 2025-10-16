package com.model.metrics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.interfaces.ModelInfo;

public abstract class NodeMetrics<T extends ModelInfo> {
	
	private static final Logger logger = LoggerFactory.getLogger(NodeMetrics.class);
	
	protected final String name;
	protected final Map<String,Object> data = new HashMap<>();
	protected int linesOfCode = -1;
	protected boolean isCalculated = false;
	
	public NodeMetrics(String name) {
		if (name==null || name.isBlank()) {
			IllegalArgumentException error = new IllegalArgumentException("Name cannot be null or blank");
			logger.error(error.getLocalizedMessage());
			throw error;
		}
		this.name = name.trim();
	}
	
	public final void calculate(T source) {
		if (isCalculated) return;
		reset();
		doCalculate(source);
		setCalculated(true);
	}
	
	protected abstract void doCalculate(T source);
	
	public String getName() {return name;}
	
	public Map<String,Object> getData() {return Collections.unmodifiableMap(data);}
	
	public Map<String,Object> copyData() {return new HashMap<>(data);}
	
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
	public <E> E getDataBy(String key, Class<E> type) {
		Object value = getDataBy(key);
		if (value!=null && type.isInstance(value)) return (E) value;
		return null;
	}
	
	public boolean containsKey(String key) {
		return this.data.containsKey(key);
	}
	
	public int getLinesOfCode() {return linesOfCode;}
	
	public void setLinesOfCode(int loc) {
		int old = this.linesOfCode;
		this.linesOfCode = Math.max(-1, loc);
		logger.debug("Change value of 'linesOfCode': %d -> %d".formatted(old,this.linesOfCode));
	}
	
	public boolean isLocSet() {return linesOfCode >= 0;}
	
	public boolean isCalculated() {return isCalculated;}
	
	public void setCalculated(boolean isCalculated) {
		boolean old = this.isCalculated;
		this.isCalculated = isCalculated;
		logger.debug("Change value of 'isCalculated': %d -> %d".formatted(old,this.isCalculated));
	}
	
	public void reset() {
		this.isCalculated = false;
		data.clear();
		logger.debug("Cleared all data");
	}

	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "name=%s, "
				+ "data=%s, "
				+ "linesOfCode=%d, "
				+ "isCalculated=%s}")
				.formatted(
					name,
					data,
					linesOfCode,
					String.valueOf(isCalculated)
				);
	}

}
