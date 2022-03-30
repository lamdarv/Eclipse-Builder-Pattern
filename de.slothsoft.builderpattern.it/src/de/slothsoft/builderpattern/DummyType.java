package de.slothsoft.builderpattern;

import java.util.Objects;

import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.SourceType;

@SuppressWarnings("restriction")
public class DummyType extends SourceType {

	private ITypeParameter[] typeParameters = new ITypeParameter[0];
	private String classPackage = "dummy";

	public DummyType(String name) {
		super(null, name);
	}

	@Override
	public String getTypeQualifiedName(char enclosingTypeSeparator, boolean showParameters) throws JavaModelException {
		return classPackage + enclosingTypeSeparator;
	}

	@Override
	public int getElementType() {
		return TYPE;
	}

	@Override
	public ITypeParameter[] getTypeParameters() {
		return typeParameters;
	}

	public DummyType typeParameters(ITypeParameter[] newTypeParameters) {
		setTypeParameters(newTypeParameters);
		return this;
	}

	public void setTypeParameters(ITypeParameter[] typeParameters) {
		this.typeParameters = Objects.requireNonNull(typeParameters);
	}

	public String getClassPackage() {
		return classPackage;
	}

	public de.slothsoft.builderpattern.DummyType classPackage(String newClassPackage) {
		setClassPackage(newClassPackage);
		return this;
	}

	public void setClassPackage(String classPackage) {
		this.classPackage = Objects.requireNonNull(classPackage);
	}

}
