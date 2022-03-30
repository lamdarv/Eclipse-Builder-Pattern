package de.slothsoft.builderpattern;

import java.util.Objects;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;

class BuilderPatternSettings {

	IField field;
	String builderName;
	boolean hasSetter;
	boolean addComments;
	int flags;

	public BuilderPatternSettings(IField field) throws JavaModelException {
		this.field = Objects.requireNonNull(field);
		this.builderName = field.getElementName();
		this.flags = field.getFlags();
	}

	/**
	 * Returns <code>true</code>, if comments will be added.
	 * 
	 * @return true or false
	 */

	public boolean isAddComments() {
		return this.addComments;
	}

	/**
	 * Set to <code>true</code>, if comments should be added.
	 * 
	 * @param newAddComments
	 * @return this instance
	 */

	public BuilderPatternSettings addComments(boolean newAddComments) {
		setAddComments(newAddComments);
		return this;
	}

	/**
	 * Set to <code>true</code>, if comments should be added.
	 * 
	 * @param addComments
	 */

	public void setAddComments(boolean addComments) {
		this.addComments = addComments;
	}

	/**
	 * Returns the chosen name for the setter
	 * 
	 * @return a string
	 */

	public String getBuilderName() {
		return this.builderName;
	}

	/**
	 * Sets the chosen name for the setter
	 * 
	 * @param newBuilderName
	 * @return this instance
	 */

	public BuilderPatternSettings builderName(String newBuilderName) {
		setBuilderName(newBuilderName);
		return this;
	}

	/**
	 * Sets the chosen name for the setter
	 * 
	 * @param builderName
	 */

	public void setBuilderName(String builderName) {
		this.builderName = builderName;
	}

	/**
	 * Returns the field to create a getter for
	 * 
	 * @return the field
	 */

	public IField getField() {
		return this.field;
	}

	/**
	 * Sets the field to create a getter for
	 * 
	 * @param newField
	 * @return this instance
	 */

	public BuilderPatternSettings field(IField newField) {
		setField(newField);
		return this;
	}

	/**
	 * Sets the field to create a getter for
	 * 
	 * @param field
	 */

	public void setField(IField field) {
		this.field = Objects.requireNonNull(field);
	}

	/**
	 * Returns the flags signaling visibility, if static, synchronized or final
	 * 
	 * @return the flags
	 */
	public int getFlags() {
		return this.flags;
	}

	/**
	 * Sets the flags signaling visibility, if static, synchronized or final
	 * 
	 * @param newFlags
	 * @return this instance
	 */

	public BuilderPatternSettings flags(int newFlags) {
		setFlags(newFlags);
		return this;
	}

	/**
	 * Sets the flags signaling visibility, if static, synchronized or final
	 * 
	 * @param newFlags
	 */

	public void setFlags(int flags) {
		this.flags = flags;
	}

	/**
	 * Returns if a setter should be used
	 * 
	 * @return true or false
	 */

	public boolean isHasSetter() {
		return this.hasSetter;
	}

	/**
	 * Sets if the setter should be used
	 * 
	 * @param newHasSetter
	 * @return this instance
	 */

	public BuilderPatternSettings hasSetter(boolean newHasSetter) {
		setHasSetter(newHasSetter);
		return this;
	}

	/**
	 * Sets if the setter should be used
	 * 
	 * @param newHasSetter
	 */

	public void setHasSetter(boolean hasSetter) {
		this.hasSetter = hasSetter;
	}

}
