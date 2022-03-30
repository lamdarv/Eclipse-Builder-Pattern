package de.slothsoft.builderpattern;

import org.eclipse.jdt.ui.JavaElementLabelProvider;

class MethodGeneratorSortOrderLabelProvider extends JavaElementLabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof MethodGeneratorSortOrder) {
			return ((MethodGeneratorSortOrder) element).getDisplayName();
		}
		return super.getText(element);
	}
}
