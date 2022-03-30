package de.slothsoft.builderpattern;

import org.eclipse.jdt.core.IField;

class FieldInfo {

	final IField field;
	final Type type;
	final boolean isFinal;

	public FieldInfo(IField field, Type type, boolean isFinal) {
		this.field = field;
		this.type = type;
		this.isFinal = isFinal;
	}

	public IField getField() {
		return this.field;
	}

	public Type getType() {
		return this.type;
	}

	public boolean isFinal() {
		return this.isFinal;
	}

	@Override
	public String toString() {
		return "FieldInfo [field=" + this.field.getElementName() + ", type=" + this.type + ", isFinal=" + this.isFinal
				+ "]";
	}

	/*
	 * 
	 */

	public static enum Type {
		GETTER, BUILDER, SETTER;
	}
}