package de.slothsoft.builderpattern;

import java.util.HashMap;

import org.eclipse.jdt.internal.codeassist.impl.AssistTypeParameter;
import org.eclipse.jdt.internal.core.JavaElement;

@SuppressWarnings("restriction")
public class DummyTypeParameter extends AssistTypeParameter {

	public DummyTypeParameter(JavaElement parent, String name) {
		super(parent, name, new HashMap<>());
	}

}
