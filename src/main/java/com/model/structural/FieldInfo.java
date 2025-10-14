package com.model.structural;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldInfo extends NodeInfo {
	
	private static final Logger logger = LoggerFactory.getLogger(FieldInfo.class);

	protected String type;
	
	public FieldInfo() {
		super();
	}
	
	public String getType() {return this.type;}
	
	public void setType(String type) {
		String old = this.type;
		this.type = type;
		logger.debug("Change value of 'type': %s -> %s".formatted(old,this.type));
	}
	
	public boolean isConstant() {
		return isStatic() && isFinal();
	}
	
	@Override
	public String getSignature() {
		return getType()+" "+getName();
	}

	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "visibility=%s, "
				+ "name=%s, "
				+ "type=%s, "
				+ "isStatic=%s, "
				+ "isAbstract=%s, "
				+ "isFinal=%s}")
				.formatted(
					this.getVisibility().name(),
					this.getName(),
					type,
					String.valueOf(this.isStatic()),
					String.valueOf(this.isAbstract()),
					String.valueOf(this.isFinal())
				);
	}
	
}
