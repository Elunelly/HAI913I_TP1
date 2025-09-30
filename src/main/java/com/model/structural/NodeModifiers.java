package com.model.structural;

import java.util.ArrayList;
import java.util.List;

public enum NodeModifiers {
	
	 STATIC			("static"		,0x0008	),
	 FINAL			("final"		,0x0010	),
	 SYNCHRONIZED	("synchronized"	,0x0020	),
	 VOLATILE		("volatile"		,0x0040	),
	 TRANSIENT		("transient"	,0x0080	),
	 NATIVE			("native"		,0x0100	),
	 SEALED			("sealed"		,0x0200	),
	 ABSTRACT		("abstract"		,0x0400	),
	 STRICTFP		("strictfp"		,0x0800	),
	 NON_SEALED		("non-sealed"	,0x1000	),
	 MODULE			("module"		,0x8000	),
	 DEFAULT		("default"		,0x10000);
	
	private final String name;
	private final int bit;
	
	private NodeModifiers(String name, int bit) {
		this.name = name;
		this.bit = bit;
	}
	
	public int getBit() {return this.bit;}
	
	public boolean isPresentIn(int modifiersFlag) {
		return ((modifiersFlag & this.bit) != 0);
	}
	
	public static List<NodeModifiers> getAllFrom(int modifiersFlag) {
		List<NodeModifiers> result = new ArrayList<>();
		for (NodeModifiers modifier : NodeModifiers.values()) {
			if (modifier.isPresentIn(modifiersFlag)) result.add(modifier);
		}
		return result;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

}
