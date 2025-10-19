package com.model.structural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.model.HasModifiers;
import com.model.HasVisibility;
import com.model.NodeInfo;
import com.model.NodeModifiers;
import com.model.NodeVisibility;

public abstract class StructuralNode<T extends ASTNode> extends NodeInfo implements HasModifiers, HasVisibility {
    
    private static final Logger logger = LoggerFactory.getLogger(StructuralNode.class);

	protected final T node;
	protected NodeVisibility visibility = NodeVisibility.PACKAGE;
	protected final List<NodeModifiers> modifiers = new ArrayList<>();

	public StructuralNode(T node, String name) {
		super(name);
		logger.trace("{} -> StructuralNode(T,String)",this.name);
		this.node = Objects.requireNonNull(node, "Node cannot be null");
	}
	
	public T getNode() {
		logger.trace("{} -> getNode()",name);
		return node;
	}
	
	public boolean isPublic() {
		logger.trace("{} -> isPublic()",name);
		return visibility == NodeVisibility.PUBLIC;
	}
	
	public boolean isProtected() {
		logger.trace("{} -> isProtected()",name);
		return visibility == NodeVisibility.PROTECTED;
	}
	
	public boolean isDefault() {
		logger.trace("{} -> isDefault()",name);
		return visibility == NodeVisibility.PACKAGE;
	}
	
	public boolean isPrivate() {
		logger.trace("{} -> isPrivate()",name);
		return visibility == NodeVisibility.PRIVATE;
	}
	
	public NodeVisibility getVisibility() {
		logger.trace("{} -> getVisibility()",name);
		return visibility;
	}
	
	public void setVisibility(NodeVisibility visibility) {
		logger.trace("{} -> setVisibility(NodeVisibility)",name);
		this.visibility = visibility;
	}
	
	public boolean isStatic() {
		logger.trace("{} -> isStatic()",name);
		return modifiers.contains(NodeModifiers.STATIC);
	}
	
	public boolean isAbstract() {
		logger.trace("{} -> isAbstract()",name);
		return modifiers.contains(NodeModifiers.ABSTRACT);
	}
	
	public boolean isFinal() {
		logger.trace("{} -> isFinal()",name);
		return modifiers.contains(NodeModifiers.FINAL);
	}
	
	public List<NodeModifiers> getModifiers() {
		logger.trace("{} -> getModifiers()",name);
		return Collections.unmodifiableList(modifiers);
	}
	
	public boolean setModifiers(int modifierFlag) {
		logger.trace("{} -> setModifiers(int)",name);
		setVisibility(NodeVisibility.getFrom(modifierFlag));
		modifiers.clear();
		return modifiers.addAll(NodeModifiers.getAllFrom(modifierFlag));
	}
	
	public boolean hasModifiers() {
		logger.trace("{} -> hasModifiers()",name);
		return modifiers.isEmpty();
	}
	
	public boolean hasModifier(NodeModifiers modifier) {
		logger.trace("{} -> hasModifier(NodeModifiers)",name);
		return modifiers.contains(modifier);
	}
	
	public String modifiersToString(String sep) {
		logger.trace("{} -> modifiersToString(String)",name);
		return !hasModifiers() ?
			getModifiers().stream().map(NodeModifiers::toString).collect(Collectors.joining(sep)) :
			""
		;
	}
	
	public String getFullSignature() {
		logger.trace("{} -> getFullSignature()",name);
		return
			getVisibility().toString()+" "+
			(hasModifiers() ? modifiersToString()+" " : "")+
			getSignature()
		;
	}
	
	public abstract String getShortSignature();
	
	public abstract String getSignature();
	
	@Override
	public boolean equals(Object obj) {
		logger.trace("{} -> equals(Object)",name);
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		@SuppressWarnings("unchecked")
		StructuralNode<T> that = (StructuralNode<T>) obj;
		return 
			Objects.equals(this.name, that.name) &&
			Objects.equals(this.node, that.node) &&
			Objects.equals(this.visibility, that.visibility) &&
			Objects.equals(this.modifiers, that.modifiers)
		;
	}
	
	@Override
	public int hashCode() {
		logger.trace("{} -> hashCode()",name);
		return Objects.hash(name, node, visibility, modifiers);
	}
	
	@Override
	public String toString() {
		logger.trace("{} -> toString()",name);
		return (this.getClass().getSimpleName()+"{"
				+ "name=%s, "
				+ "node=%s, "
				+ "visibility=%s, "
				+ "modifiers=%s}")
				.formatted(
					name,
					node.getClass().getSimpleName(),
					visibility,
					modifiers
				);
	}

}
