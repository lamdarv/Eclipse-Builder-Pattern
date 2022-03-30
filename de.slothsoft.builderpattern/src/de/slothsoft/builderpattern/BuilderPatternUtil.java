package de.slothsoft.builderpattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.core.manipulation.StubUtility;
import org.eclipse.jdt.internal.corext.codemanipulation.GetterSetterUtil;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.JdtFlags;
import org.eclipse.jdt.ui.CodeGeneration;

final class BuilderPatternUtil {

	private static final String[] EMPTY = new String[0];

	public static IMethod getBuilder(IField field) throws JavaModelException {
		String[] args = new String[] { field.getTypeSignature() };
		return JavaModelUtil.findMethod(getBuilderName(field.getElementName()), args, false, field.getDeclaringType());
	}

	public static String getBuilderName(String fieldName) {
		return fieldName;
	}

	/**
	 * Create a stub for a getter of the given field using getter/setter templates. The
	 * resulting code
	 * has to be formatted and indented.
	 * 
	 * @param settings the entire settings for this operation
	 */

	public static String getBuilderStub(BuilderPatternSettings settings) throws CoreException {

		IField field = settings.field;
		IType parentType = field.getDeclaringType();
		String returnSig = field.getTypeSignature();
		String typeName = Signature.toString(returnSig);
		IJavaProject project = field.getJavaProject();

		String accessorName = StubUtility.getBaseName(field);
		String argname = suggestArgumentName(project, accessorName);

		boolean isStatic = Flags.isStatic(settings.flags);

		String lineDelim = "\n"; // Use default line delimiter, as generated stub has to be formatted anyway //$NON-NLS-1$
		StringBuffer buf = new StringBuffer();
		if (settings.addComments) {
			String comment = CodeGeneration.getSetterComment(field.getCompilationUnit(),
					parentType.getTypeQualifiedName('.'), settings.builderName, field.getElementName(), typeName,
					argname, accessorName, lineDelim);
			if (comment != null) {
				buf.append(comment);
				buf.append(lineDelim);
			}
		}
		String visibility = JdtFlags.getVisibilityString(settings.flags);
		buf.append(visibility);
		if (!visibility.isEmpty()) {
			buf.append(' ');
		}
		if (isStatic) {
			buf.append("static "); //$NON-NLS-1$
		}
		if (Flags.isSynchronized(settings.flags)) {
			buf.append("synchronized "); //$NON-NLS-1$
		}
		if (Flags.isFinal(settings.flags)) {
			buf.append("final "); //$NON-NLS-1$
		}

		buf.append(parentType.getElementName());

		ITypeParameter[] parameters = parentType.getTypeParameters();
		if (parameters.length > 0) {
			buf.append("<");
			for (int i = 0; i < parameters.length; i++) {
				if (i > 0) {
					buf.append(", "); //$NON-NLS-1$
				}
				buf.append(parameters[i].getElementName());
			}
			buf.append(">");
		}

		buf.append(' '); //$NON-NLS-1$
		buf.append(settings.builderName);
		buf.append('(');
		buf.append(typeName);
		buf.append(' ');
		buf.append(argname);
		buf.append(") {"); //$NON-NLS-1$
		buf.append(lineDelim);

		if (settings.hasSetter) {
			buf.append(GetterSetterUtil.getSetterName(field, null)).append('(').append(argname).append(");");
		} else {
			String fieldName = field.getElementName();
			boolean useThis = StubUtility.useThisForFieldAccess(project);
			if (argname.equals(fieldName) || (useThis && !isStatic)) {
				if (isStatic) {
					fieldName = parentType.getElementName() + '.' + fieldName;
				} else {
					fieldName = "this." + fieldName; //$NON-NLS-1$
				}
			}
			String body = CodeGeneration.getSetterMethodBodyContent(field.getCompilationUnit(),
					parentType.getTypeQualifiedName('.'), settings.builderName, fieldName, argname, lineDelim);
			if (body != null) {
				buf.append(body);
			}
		}

		buf.append(lineDelim);
		buf.append("return this;");
		buf.append(lineDelim);
		buf.append("}"); //$NON-NLS-1$
		buf.append(lineDelim);
		return buf.toString();
	}

	private static String suggestArgumentName(IJavaProject project, String accessorName) {
		return prependNew(StubUtility.suggestArgumentName(project, accessorName, EMPTY));
	}

	protected static String prependNew(String name) {
		String nameWithCapitalFirstLetter = name.substring(0, 1).toUpperCase() + name.substring(1);
		return "new" + nameWithCapitalFirstLetter;
	}

	private BuilderPatternUtil() {
		// at work I'm adding stupid comments, but at home I'm serious as shit
	}

}
