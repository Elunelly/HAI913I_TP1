package com.model.structural;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.Modifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.interfaces.ModelInfo;

public abstract class NodeInfo implements ModelInfo {
	
	private static final Logger logger = LoggerFactory.getLogger(NodeInfo.class);
	
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
		String old = this.name;
		this.name = name;
		logger.debug("Change value of 'name': %s -> %s".formatted(old,this.name));
	}

	public NodeVisibility getVisibility() {return visibility;}

	public void setVisibility(NodeVisibility visibility) {
		NodeVisibility oldv = this.visibility;
		this.visibility = visibility;
		logger.debug("Change value of 'visibility': %s -> %s".formatted(oldv,this.visibility));
		boolean oldp = this.isPublic;
		this.isPublic = (visibility == NodeVisibility.PUBLIC);
		logger.debug("Change value of 'isPublic': %s -> %s".formatted(oldp,this.isPublic));
	}

	public boolean isStatic() {return isStatic;}

	public void setStatic(boolean isStatic) {
		boolean old = this.isStatic;
		this.isStatic = isStatic;
		logger.debug("Change value of 'isStatic': %s -> %s".formatted(old,this.isStatic));
	}

	public boolean isAbstract() {return isAbstract;}

	public void setAbstract(boolean isAbstract) {
		boolean old = this.isAbstract;
		this.isAbstract = isAbstract;
		logger.debug("Change value of 'isAbstract': %s -> %s".formatted(old,this.isAbstract));
	}

	public boolean isFinal() {return isFinal;}

	public void setFinal(boolean isFinal) {
		boolean old = this.isFinal;
		this.isFinal = isFinal;
		logger.debug("Change value of 'isFinal': %s -> %s".formatted(old,this.isFinal));
	}
	
	public boolean isPublic() {return this.isPublic;}
	
	public void setPublic(boolean isPublic) {
		if (!isPublic) {
			if (this.visibility == NodeVisibility.PUBLIC) {
				logger.error("Cannot change 'isPublic' to false while visibility is set to PUBLIC");
				return;
			}
		} else {
			NodeVisibility oldv = this.visibility;
			this.visibility = NodeVisibility.PUBLIC;
			logger.debug("Change value of 'visibility': %s -> %s".formatted(oldv,this.visibility));
		}
		boolean oldp = this.isPublic;
		this.isPublic = isPublic;
		logger.debug("Change value of 'isPublic': %s -> %s".formatted(oldp,this.isPublic));
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
	
	public boolean hasModifier(NodeModifiers modifier) {
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

	@Override
	public String toString() {
		return (this.getClass().getSimpleName()+"{"
				+ "visibility=%s, "
				+ "name=%s, "
				+ "isStatic=%s, "
				+ "isAbstract=%s, "
				+ "isFinal=%s}")
				.formatted(
					visibility.name(),
					name,
					String.valueOf(isStatic),
					String.valueOf(isAbstract),
					String.valueOf(isFinal)
				);
	}

}