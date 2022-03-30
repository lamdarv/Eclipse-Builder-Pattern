package de.slothsoft.builderpattern;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.codemanipulation.GetterSetterUtil;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

class FieldInfoContentProvider implements ITreeContentProvider {

	private static final Object[] EMPTY = new Object[0];

	static Map<IField, FieldInfo[]> createGetterSetterMapping(IType type) throws JavaModelException {
		IField[] fields = type.getFields();
		Map<IField, FieldInfo[]> result = new LinkedHashMap<IField, FieldInfo[]>();
		for (int i = 0; i < fields.length; i++) {

			IField field = fields[i];
			int flags = field.getFlags();

			if (!Flags.isEnum(flags)) {
				List<FieldInfo> l = new ArrayList<FieldInfo>(3);
				if (GetterSetterUtil.getGetter(field) == null) {
					l.add(new FieldInfo(field, FieldInfo.Type.GETTER, Flags.isFinal(flags)));
				}
				if (GetterSetterUtil.getSetter(field) == null) {
					l.add(new FieldInfo(field, FieldInfo.Type.SETTER, Flags.isFinal(flags)));
				}
				if (BuilderPatternUtil.getBuilder(field) == null && !Flags.isStatic(flags)) {
					l.add(new FieldInfo(field, FieldInfo.Type.BUILDER, Flags.isFinal(flags)));
				}
				if (!l.isEmpty()) {
					result.put(field, l.toArray(new FieldInfo[l.size()]));
				}
			}
		}
		return result;
	}

	private Map<IField, FieldInfo[]> getterSetterEntries;

	public FieldInfoContentProvider(IType type) throws JavaModelException {
		this(createGetterSetterMapping(type));
	}

	public FieldInfoContentProvider(Map<IField, FieldInfo[]> entries) {
		this.getterSetterEntries = entries;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// nothing to do
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IField) {
			return this.getterSetterEntries.get(parentElement);
		}
		return EMPTY;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IMember) {
			return ((IMember) element).getDeclaringType();
		}
		if (element instanceof FieldInfo) {
			return ((FieldInfo) element).field;
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return this.getterSetterEntries.keySet().toArray();
	}

	@Override
	public void dispose() {
		this.getterSetterEntries.clear();
		this.getterSetterEntries = null;
	}

	public Map<IField, FieldInfo[]> getGetterSetterEntries() {
		return this.getterSetterEntries;
	}
}