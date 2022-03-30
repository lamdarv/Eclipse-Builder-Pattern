package de.slothsoft.builderpattern;

import java.lang.reflect.Modifier;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.core.SourceField;

@SuppressWarnings("restriction")
public class DummyField extends SourceField {

	private String type = "Object";
	private int visibility = Modifier.PUBLIC;

	public DummyField(String name) {
		this(new DummyType("SomeClas"), name);
	}

	public DummyField(JavaElement parent, String name) {
		super(parent, name);
	}

	@Override
	public String getTypeSignature() throws JavaModelException {
		return Signature.createTypeSignature(this.type, false);
	}

	@Override
	public int getElementType() {
		return FIELD;
	}

	@Override
	public int getFlags() throws JavaModelException {
		return this.visibility;
	}

	public String getType() {
		return this.type;
	}

	public DummyField type(String type) {
		this.type = type;
		return this;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getVisibility() {
		return this.visibility;
	}

	public DummyField visibility(int visibility) {
		this.visibility = visibility;
		return this;
	}

	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}

}
