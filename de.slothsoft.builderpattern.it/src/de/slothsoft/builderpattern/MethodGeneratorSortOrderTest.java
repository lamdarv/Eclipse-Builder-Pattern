package de.slothsoft.builderpattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.slothsoft.builderpattern.FieldInfo.Type;

public class MethodGeneratorSortOrderTest {

	private DummyType myClass = new DummyType("MyClass");
	private DummyField field1 = new DummyField(this.myClass, "one").type("String");
	private DummyField field2 = new DummyField(this.myClass, "two").type("int");
	private DummyField field3 = new DummyField(this.myClass, "three").type("int");

	private FieldInfo fieldInfo1Getter = new FieldInfo(this.field1, Type.GETTER, false);
	private FieldInfo fieldInfo1Setter = new FieldInfo(this.field1, Type.SETTER, false);
	private FieldInfo fieldInfo2Getter = new FieldInfo(this.field2, Type.GETTER, false);
	private FieldInfo fieldInfo2Builder = new FieldInfo(this.field2, Type.BUILDER, false);
	private FieldInfo fieldInfo3Setter = new FieldInfo(this.field3, Type.SETTER, false);
	private FieldInfo fieldInfo3Builder = new FieldInfo(this.field3, Type.BUILDER, false);

	private List<FieldInfo> fields = new ArrayList<>(Arrays.asList(this.fieldInfo1Getter, this.fieldInfo1Setter,
			this.fieldInfo2Getter, this.fieldInfo2Builder, this.fieldInfo3Setter, this.fieldInfo3Builder));

	@Test
	public void testSortByType() throws Exception {
		this.fields.sort(MethodGeneratorSortOrder.BY_TYPE.getComparator());

		int index = 0;
		Assert.assertSame(this.fieldInfo1Getter, this.fields.get(index++));
		Assert.assertSame(this.fieldInfo2Getter, this.fields.get(index++));
		Assert.assertSame(this.fieldInfo2Builder, this.fields.get(index++));
		Assert.assertSame(this.fieldInfo3Builder, this.fields.get(index++));
		Assert.assertSame(this.fieldInfo1Setter, this.fields.get(index++));
		Assert.assertSame(this.fieldInfo3Setter, this.fields.get(index++));
	}

	@Test
	public void testSortByField() throws Exception {
		this.fields.sort(MethodGeneratorSortOrder.BY_FIELD.getComparator());

		int index = 0;
		Assert.assertSame(this.fieldInfo1Getter, this.fields.get(index++));
		Assert.assertSame(this.fieldInfo1Setter, this.fields.get(index++));
		Assert.assertSame(this.fieldInfo3Builder, this.fields.get(index++));
		Assert.assertSame(this.fieldInfo3Setter, this.fields.get(index++));
		Assert.assertSame(this.fieldInfo2Getter, this.fields.get(index++));
		Assert.assertSame(this.fieldInfo2Builder, this.fields.get(index++));
	}
}
