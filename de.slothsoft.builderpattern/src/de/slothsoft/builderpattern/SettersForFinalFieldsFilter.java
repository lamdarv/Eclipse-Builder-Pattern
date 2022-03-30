package de.slothsoft.builderpattern;

import org.eclipse.jdt.core.IField;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

class SettersForFinalFieldsFilter extends ViewerFilter {

	private final FieldInfoContentProvider contentProvider;

	public SettersForFinalFieldsFilter(FieldInfoContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof FieldInfo) {
			FieldInfo getterSetterEntry = (FieldInfo) element;
			return getterSetterEntry.type == FieldInfo.Type.GETTER || !getterSetterEntry.isFinal;
		}
		if (element instanceof IField) {
			Object[] children = this.contentProvider.getChildren(element);
			for (int i = 0; i < children.length; i++) {
				FieldInfo curr = (FieldInfo) children[i];
				if (curr.type == FieldInfo.Type.GETTER || !curr.isFinal) {
					return true;
				}
			}
			return false;
		}
		return true;
	}
}