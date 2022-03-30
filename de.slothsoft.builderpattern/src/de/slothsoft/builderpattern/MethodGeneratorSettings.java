package de.slothsoft.builderpattern;

import java.util.Objects;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;

public class MethodGeneratorSettings {

	FieldInfo[] fields;
	IType type;
	MethodGeneratorSortOrder sortOrder = MethodGeneratorSortOrder.BY_FIELD;
	boolean useSynchronized;
	boolean useFinal;
	boolean allowFinalSetters;
	boolean generateComments;
	int visibility;
	IJavaElement position;
	boolean useListeners;

	public MethodGeneratorSettings(IType type, FieldInfo[] fields) {
		this.fields = fields;
		this.type = type;
	}

	public MethodGeneratorSettings fields(FieldInfo[] fields) {
		setFields(fields);
		return this;
	}

	public void setFields(FieldInfo[] fields) {
		this.fields = Objects.requireNonNull(fields);
	}

	public FieldInfo[] getFields() {
		return this.fields;
	}

	public MethodGeneratorSettings type(IType type) {
		setType(type);
		return this;
	}

	public void setType(IType type) {
		this.type = type;
	}

	public IType getType() {
		return this.type;
	}

	public MethodGeneratorSortOrder getSortOrder() {
		return this.sortOrder;
	}

	public MethodGeneratorSettings sortOrder(MethodGeneratorSortOrder sort) {
		setSortOrder(sort);
		return this;
	}

	public void setSortOrder(MethodGeneratorSortOrder sort) {
		this.sortOrder = sort;
	}

	public boolean isUseSynchronized() {
		return this.useSynchronized;
	}

	public MethodGeneratorSettings useSynchronized(boolean useSynchronized) {
		this.useSynchronized = useSynchronized;
		return this;
	}

	public void setUseSynchronized(boolean useSynchronized) {
		this.useSynchronized = useSynchronized;
	}

	public boolean isUseFinal() {
		return this.useFinal;
	}

	public MethodGeneratorSettings useFinal(boolean useFinal) {
		this.useFinal = useFinal;
		return this;
	}

	public void setUseFinal(boolean useFinal) {
		this.useFinal = useFinal;
	}

	public boolean isAllowFinalSetters() {
		return this.allowFinalSetters;
	}

	public MethodGeneratorSettings allowFinalSetters(boolean allowFinalSetters) {
		this.allowFinalSetters = allowFinalSetters;
		return this;
	}

	public void setAllowFinalSetters(boolean allowFinalSetters) {
		this.allowFinalSetters = allowFinalSetters;
	}

	public boolean isGenerateComments() {
		return this.generateComments;
	}

	public MethodGeneratorSettings generateComments(boolean generateComments) {
		this.generateComments = generateComments;
		return this;
	}

	public void setGenerateComments(boolean generateComments) {
		this.generateComments = generateComments;
	}

	public int getVisibility() {
		return this.visibility;
	}

	public MethodGeneratorSettings visibility(int visibility) {
		this.visibility = visibility;
		return this;
	}

	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}

	public IJavaElement getPosition() {
		return this.position;
	}

	public MethodGeneratorSettings position(IJavaElement position) {
		setPosition(position);
		return this;
	}

	public void setPosition(IJavaElement position) {
		this.position = position;
	}

	public boolean isUseListeners() {
		return this.useListeners;
	}

	public MethodGeneratorSettings useListeners(boolean newUseListeners) {
		setUseListeners(newUseListeners);
		return this;
	}

	public void setUseListeners(boolean useListeners) {
		this.useListeners = useListeners;
	}

}
