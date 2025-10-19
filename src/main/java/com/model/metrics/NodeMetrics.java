package com.model.metrics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.NodeInfo;
import com.visitors.LOCMetrics;

public abstract class NodeMetrics<T extends NodeInfo> {
	
	private static final Logger logger = LoggerFactory.getLogger(NodeMetrics.class);
	
	protected final String name;
	protected final Map<Enum<?>,Object> data = new HashMap<>();
	protected boolean isCalculated = false;
	
	public NodeMetrics(String name) {
		if (name==null || name.isBlank()) {
			IllegalArgumentException error = new IllegalArgumentException("Name cannot be null or blank");
			logger.error(error.getLocalizedMessage());
			throw error;
		}
		this.name = name.trim();
	}
	
	public String getName() {return name;}
	
	public Map<Enum<?>,Object> getData() {return Collections.unmodifiableMap(data);}
	
	public Map<Enum<?>,Object> copyData() {return new HashMap<>(data);}
	
	public void addData(Enum<?> key, Object value) {
		Object old = data.get(key);
		data.put(key, value);
		if (old==null)
			logger.debug("Added entry: '"+key+"'="+value);
		else
			logger.debug("Change value of '%s': %s -> %s".formatted(key,old,value));
	}
	
	public Object getDataBy(Enum<?> key) {
		return this.data.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public <E> E getDataBy(Enum<?> key, Class<E> type) {
		Object value = getDataBy(key);
		if (value!=null && type.isInstance(value)) return (E) value;
		return null;
	}
	
	public boolean hasData() {
		return !data.isEmpty();
	}
	
	public boolean containsKey(Enum<?> key) {
		return this.data.containsKey(key);
	}
	
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
	
	// LINES OF CODE METRICS
	public int getLOC() {return getKey(LOCMetrics.LINE_LENGTH);}
	
	public int getStartingLine() {return getKey(LOCMetrics.FIRST_LINE_NUM);}
	
	public int getEndingLine() {return getKey(LOCMetrics.LAST_LINE_NUM);}
	
	public int getLength() {return getKey(LOCMetrics.CHAR_LENGTH);}
	
	public int getStartingCharacter() {return getKey(LOCMetrics.FIRST_CHAR_POS);}
	
	public int getEndingCharacter() {return getKey(LOCMetrics.LAST_CHAR_POS);}
	
	private int getKey(LOCMetrics key) {
		return data.containsKey(key)
			? (int) getDataBy(key, key.getReturnType())
			: -1;
	}

	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "name=%s, "
				+ "data=%s, "
				+ "isCalculated=%s}")
				.formatted(
					name,
					data,
					String.valueOf(isCalculated)
				);
	}

}
