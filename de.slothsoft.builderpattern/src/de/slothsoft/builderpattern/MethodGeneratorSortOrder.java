package de.slothsoft.builderpattern;

import java.util.Comparator;

public enum MethodGeneratorSortOrder {
	BY_FIELD, BY_TYPE;

	public String getDisplayName() {
		return Messages.getString("MethodGeneratorSortOrder." + name());
	}

	public Comparator<FieldInfo> getComparator() {
		switch (this) {
		case BY_FIELD:
			return Comparator.<FieldInfo, String> comparing(i -> i.field.getElementName()).thenComparing(
					Comparator.<FieldInfo, FieldInfo.Type> comparing(i -> i.type));
		case BY_TYPE:
			return Comparator.comparing(i -> i.type);
		}
		throw new UnsupportedOperationException("Not implemented for " + this);
	}
}