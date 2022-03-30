package de.slothsoft.builderpattern;

import java.lang.reflect.Modifier;

import org.eclipse.core.runtime.CoreException;
import org.junit.Assert;
import org.junit.Test;

public class BuilderPatternUtilTest {

	@Test
	public void testBuilderName() {
		Assert.assertEquals("field", BuilderPatternUtil.getBuilderName("field"));
	}

	@Test
	public void testBuilderStub() throws CoreException {
		DummyField field = new DummyField(new DummyType("SomeClass"), "oneField").type("String");
		BuilderPatternSettings settings = new BuilderPatternSettings(field);
		settings.hasSetter = true;
		settings.addComments = false;
		settings.flags = Modifier.PUBLIC;

		String expected = "" + //
				"public SomeClass oneField(String newOneField) {" + "\n" + //
				"setOneField(newOneField);" + "\n" + //
				"return this;" + "\n" + //
				"}" + "\n"; //

		Assert.assertEquals(expected, BuilderPatternUtil.getBuilderStub(settings));
	}

	@Test
	public void testBuilderStubProtected() throws CoreException {
		DummyField field = new DummyField(new DummyType("SomeClass"), "oneField").type("int");
		BuilderPatternSettings settings = new BuilderPatternSettings(field);
		settings.hasSetter = true;
		settings.addComments = false;
		settings.flags = Modifier.PROTECTED;

		String expected = "" + //
				"protected SomeClass oneField(int newOneField) {" + "\n" + //
				"setOneField(newOneField);" + "\n" + //
				"return this;" + "\n" + //
				"}" + "\n"; //

		Assert.assertEquals(expected, BuilderPatternUtil.getBuilderStub(settings));
	}

	@Test
	public void testBuilderStubPackageVisible() throws CoreException {
		DummyField field = new DummyField(new DummyType("SomeClass"), "otherField").type("Long");
		BuilderPatternSettings settings = new BuilderPatternSettings(field);
		settings.hasSetter = true;
		settings.addComments = false;
		settings.flags = 0;

		String expected = "" + //
				"SomeClass otherField(Long newOtherField) {" + "\n" + //
				"setOtherField(newOtherField);" + "\n" + //
				"return this;" + "\n" + //
				"}" + "\n"; //

		Assert.assertEquals(expected, BuilderPatternUtil.getBuilderStub(settings));
	}

	@Test
	public void testBuilderStubPrivate() throws CoreException {
		DummyField field = new DummyField(new DummyType("Blob"), "oneField").type("String");
		BuilderPatternSettings settings = new BuilderPatternSettings(field);
		settings.hasSetter = true;
		settings.addComments = false;
		settings.flags = Modifier.PRIVATE;

		String expected = "" + //
				"private Blob oneField(String newOneField) {" + "\n" + //
				"setOneField(newOneField);" + "\n" + //
				"return this;" + "\n" + //
				"}" + "\n"; //

		Assert.assertEquals(expected, BuilderPatternUtil.getBuilderStub(settings));
	}

	@Test
	public void testBuilderStubFinal() throws CoreException {
		DummyField field = new DummyField(new DummyType("SomeClass"), "otherField").type("String");
		BuilderPatternSettings settings = new BuilderPatternSettings(field);
		settings.hasSetter = true;
		settings.addComments = false;
		settings.flags = Modifier.PUBLIC | Modifier.FINAL;

		String expected = "" + //
				"public final SomeClass otherField(String newOtherField) {" + "\n" + //
				"setOtherField(newOtherField);" + "\n" + //
				"return this;" + "\n" + //
				"}" + "\n"; //

		Assert.assertEquals(expected, BuilderPatternUtil.getBuilderStub(settings));
	}

	@Test
	public void testBuilderStubStatic() throws CoreException {
		DummyField field = new DummyField(new DummyType("SomeClass"), "oneField").type("String");
		BuilderPatternSettings settings = new BuilderPatternSettings(field);
		settings.hasSetter = true;
		settings.addComments = false;
		settings.flags = Modifier.PUBLIC | Modifier.STATIC;

		String expected = "" + //
				"public static SomeClass oneField(String newOneField) {" + "\n" + //
				"setOneField(newOneField);" + "\n" + //
				"return this;" + "\n" + //
				"}" + "\n"; //

		Assert.assertEquals(expected, BuilderPatternUtil.getBuilderStub(settings));
	}

	@Test
	public void testBuilderStubStaticFinal() throws CoreException {
		DummyField field = new DummyField(new DummyType("SomeClass"), "oneField").type("String");
		BuilderPatternSettings settings = new BuilderPatternSettings(field);
		settings.hasSetter = true;
		settings.addComments = false;
		settings.flags = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

		String expected = "" + //
				"public static final SomeClass oneField(String newOneField) {" + "\n" + //
				"setOneField(newOneField);" + "\n" + //
				"return this;" + "\n" + //
				"}" + "\n"; //

		Assert.assertEquals(expected, BuilderPatternUtil.getBuilderStub(settings));
	}

	@Test
	public void testBuilderStubSynchronized() throws CoreException {
		DummyField field = new DummyField(new DummyType("SomeClass"), "oneField").type("String");
		BuilderPatternSettings settings = new BuilderPatternSettings(field);
		settings.hasSetter = true;
		settings.addComments = false;
		settings.flags = Modifier.PUBLIC | Modifier.SYNCHRONIZED;

		String expected = "" + //
				"public synchronized SomeClass oneField(String newOneField) {" + "\n" + //
				"setOneField(newOneField);" + "\n" + //
				"return this;" + "\n" + //
				"}" + "\n"; //

		Assert.assertEquals(expected, BuilderPatternUtil.getBuilderStub(settings));
	}

	@Test
	public void testPrependNew() {
		Assert.assertEquals("newParameter", BuilderPatternUtil.prependNew("parameter"));
		Assert.assertEquals("newArgument", BuilderPatternUtil.prependNew("Argument"));
	}

}
