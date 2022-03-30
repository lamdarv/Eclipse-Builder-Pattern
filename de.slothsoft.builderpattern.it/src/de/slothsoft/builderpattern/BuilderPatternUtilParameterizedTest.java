package de.slothsoft.builderpattern;

import java.lang.reflect.Modifier;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ITypeParameter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BuilderPatternUtilParameterizedTest {

	private DummyType classUnderTest = new DummyType("SomeClass");
	private DummyField field = new DummyField(this.classUnderTest, "oneField").type("String");
	private BuilderPatternSettings settings;

	@Before
	public void setup() throws CoreException {
		this.classUnderTest.typeParameters(new ITypeParameter[] { new DummyTypeParameter(this.classUnderTest, "T") });
		this.settings = new BuilderPatternSettings(this.field);
	}

	@Test
	public void testBuilderStub() throws CoreException {
		this.settings.hasSetter = true;
		this.settings.addComments = false;
		this.settings.flags = Modifier.PUBLIC;

		String expected = "" + //
				"public SomeClass<T> oneField(String newOneField) {" + "\n" + //
				"setOneField(newOneField);" + "\n" + //
				"return this;" + "\n" + //
				"}" + "\n"; //

		Assert.assertEquals(expected, BuilderPatternUtil.getBuilderStub(this.settings));
	}

	@Test
	public void testBuilderStubProtected() throws CoreException {
		this.settings.hasSetter = true;
		this.settings.addComments = false;
		this.settings.flags = Modifier.PROTECTED;

		String expected = "" + //
				"protected SomeClass<T> oneField(String newOneField) {" + "\n" + //
				"setOneField(newOneField);" + "\n" + //
				"return this;" + "\n" + //
				"}" + "\n"; //

		Assert.assertEquals(expected, BuilderPatternUtil.getBuilderStub(this.settings));
	}

	@Test
	public void testBuilderStubPackageVisible() throws CoreException {
		this.settings.hasSetter = true;
		this.settings.addComments = false;
		this.settings.flags = 0;

		String expected = "" + //
				"SomeClass<T> oneField(String newOneField) {" + "\n" + //
				"setOneField(newOneField);" + "\n" + //
				"return this;" + "\n" + //
				"}" + "\n"; //

		Assert.assertEquals(expected, BuilderPatternUtil.getBuilderStub(this.settings));
	}

	@Test
	public void testBuilderStubPrivate() throws CoreException {
		this.settings.hasSetter = true;
		this.settings.addComments = false;
		this.settings.flags = Modifier.PRIVATE;

		String expected = "" + //
				"private SomeClass<T> oneField(String newOneField) {" + "\n" + //
				"setOneField(newOneField);" + "\n" + //
				"return this;" + "\n" + //
				"}" + "\n"; //

		Assert.assertEquals(expected, BuilderPatternUtil.getBuilderStub(this.settings));
	}

	@Test
	public void testBuilderStubFinal() throws CoreException {
		this.settings.hasSetter = true;
		this.settings.addComments = false;
		this.settings.flags = Modifier.PUBLIC | Modifier.FINAL;

		String expected = "" + //
				"public final SomeClass<T> oneField(String newOneField) {" + "\n" + //
				"setOneField(newOneField);" + "\n" + //
				"return this;" + "\n" + //
				"}" + "\n"; //

		Assert.assertEquals(expected, BuilderPatternUtil.getBuilderStub(this.settings));
	}

	@Test
	public void testBuilderStubStatic() throws CoreException {
		this.settings.hasSetter = true;
		this.settings.addComments = false;
		this.settings.flags = Modifier.PUBLIC | Modifier.STATIC;

		String expected = "" + //
				"public static SomeClass<T> oneField(String newOneField) {" + "\n" + //
				"setOneField(newOneField);" + "\n" + //
				"return this;" + "\n" + //
				"}" + "\n"; //

		Assert.assertEquals(expected, BuilderPatternUtil.getBuilderStub(this.settings));
	}

	@Test
	public void testBuilderStubStaticFinal() throws CoreException {
		this.settings.hasSetter = true;
		this.settings.addComments = false;
		this.settings.flags = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

		String expected = "" + //
				"public static final SomeClass<T> oneField(String newOneField) {" + "\n" + //
				"setOneField(newOneField);" + "\n" + //
				"return this;" + "\n" + //
				"}" + "\n"; //

		Assert.assertEquals(expected, BuilderPatternUtil.getBuilderStub(this.settings));
	}

	@Test
	public void testBuilderStubSynchronized() throws CoreException {
		this.settings.hasSetter = true;
		this.settings.addComments = false;
		this.settings.flags = Modifier.PUBLIC | Modifier.SYNCHRONIZED;

		String expected = "" + //
				"public synchronized SomeClass<T> oneField(String newOneField) {" + "\n" + //
				"setOneField(newOneField);" + "\n" + //
				"return this;" + "\n" + //
				"}" + "\n"; //

		Assert.assertEquals(expected, BuilderPatternUtil.getBuilderStub(this.settings));
	}

}
