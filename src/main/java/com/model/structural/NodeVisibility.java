package com.model.structural;

import org.eclipse.jdt.core.dom.Modifier;

public enum NodeVisibility {
	
	PUBLIC		("public"	,0x0001	),
	PRIVATE		("private"	,0x0002	),
	PROTECTED	("protected",0x0004	),
	PACKAGE		("package"	,0x0000	);
	
	private final String name;
	private final int bit;
	
	private NodeVisibility(String name, int bit) {
		this.name = name;
		this.bit = bit;
	}
	
	public int getBit() {return this.bit;}
	
	public static NodeVisibility getFrom(int modifiersFlag) {
        if (Modifier.isPublic(modifiersFlag)) return PUBLIC;
        else if (Modifier.isPrivate(modifiersFlag)) return PRIVATE;
        else if (Modifier.isProtected(modifiersFlag)) return PROTECTED;
        else return PACKAGE;
	}
	
	public boolean isMoreAccessibleThan(NodeVisibility that) {
		return this.getAccessLevel() > that.getAccessLevel();
	}
	
	public int getAccessLevel() {
		switch (this) {
			case PUBLIC: return 3;
			case PROTECTED: return 2;
			case PACKAGE: return 1;
			case PRIVATE: return 0;
			default: return 0;
		}
	}
	
	@Override
	public String toString() {
		return this.name;
	}

}
