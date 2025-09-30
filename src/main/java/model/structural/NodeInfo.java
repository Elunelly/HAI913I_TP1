package model.structural;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.Modifier;

class NodeInfo {
	
	private String name;
	private NodeVisibility visibility = NodeVisibility.PACKAGE;
	private boolean isStatic;
	private boolean isAbstract;
	private boolean isFinal;
	private boolean isPublic;
	private List<NodeModifiers> modifiers = new ArrayList<>();
	
	public NodeInfo() {}

	public String getName() {return name;}

	public void setName(String name) {
		this.name = name;
	}

	public NodeVisibility getVisibility() {return visibility;}

	public void setVisibility(NodeVisibility visibility) {
		this.visibility = visibility;
		this.isPublic = (visibility == NodeVisibility.PUBLIC);
	}

	public boolean isStatic() {return isStatic;}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public boolean isAbstract() {return isAbstract;}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public boolean isFinal() {return isFinal;}

	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}
	
	public boolean isPublic() {return this.isPublic;}
	
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
		if (isPublic) this.visibility = NodeVisibility.PUBLIC;
	}
	
	public List<NodeModifiers> getModifiers() {return this.modifiers;}
	
	public void addAllModifiers(int modifiersFlag) {
		this.modifiers.clear();
		for (NodeModifiers modifier : NodeModifiers.values()) {
			if ((modifiersFlag & modifier.getBit())!=0) this.modifiers.add(modifier);
		}
	}
	
	public void setDefinedModifiers(int modifiersFlag) {		
		setVisibility(NodeVisibility.getFrom(modifiersFlag));
		
		setStatic(Modifier.isStatic(modifiersFlag));
		setAbstract(Modifier.isAbstract(modifiersFlag));
		setFinal(Modifier.isFinal(modifiersFlag));
		
		addAllModifiers(modifiersFlag);
	}
	
	public boolean isModifiersEmpty() {
		return this.modifiers==null || this.modifiers.isEmpty();
	}
	
	public boolean hasModifiers(NodeModifiers modifier) {
		return !isModifiersEmpty() && this.modifiers.contains(modifier);
	}
	
	protected String modifiersToString(String sep) {
		return (!isModifiersEmpty() ? getModifiers().stream().map(NodeModifiers::toString).collect(Collectors.joining(sep)) : "");
	}
	
	protected String modifiersToString() {
		return modifiersToString(" ");
	}
	
	public String getFullSignature() {
		return
			getVisibility().toString()+" "+
			(isModifiersEmpty() ? modifiersToString(" ")+" " : "")+
			getSignature()
		;
	}
	
	// Inherited class will have to @Override this class
	public String getSignature() {
		return "";
	}

}