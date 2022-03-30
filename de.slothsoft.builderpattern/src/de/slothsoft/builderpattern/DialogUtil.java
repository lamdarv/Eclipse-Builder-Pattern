package de.slothsoft.builderpattern;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.ActionMessages;
import org.eclipse.jdt.internal.ui.actions.ActionUtil;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.util.ElementValidator;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;

public final class DialogUtil {

	public static void openDialog(Shell shell, CompilationUnitEditor editor) {
		try {
			CompilationUnitEditor unitEditor = editor;
			IType type = findSelectedType(editor);
			IField[] preselected = null;
			openDialog(shell, unitEditor, type, preselected, (s) -> MethodGeneratorUtil.generate((IEditorPart) editor, s));
		} catch (CoreException e) {
			ExceptionHandler.handle(e, shell, ActionMessages.AddGetterSetterAction_error_title,
					ActionMessages.AddGetterSetterAction_error_actionfailed);
		}
	}

	private static IType findSelectedType(CompilationUnitEditor editor) throws JavaModelException {
		IJavaElement selection = SelectionConverter.getElementAtOffset(editor);
		if (selection != null) {
			if (selection instanceof IType) return (IType) selection;
			IType type = (IType) selection.getAncestor(IJavaElement.TYPE);
			if (type != null) return type;
		}
		ICompilationUnit compilationUnit = SelectionConverter.getInputAsCompilationUnit(editor);
		return compilationUnit.findPrimaryType();
	}

	public static void openDialog(Shell shell, ICompilationUnit compilationUnit, ISelection selection) {
		try {
			CompilationUnitEditor unitEditor = null;
			IType type = compilationUnit.findPrimaryType();
			IField[] preselected = guessSelection(selection);
			openDialog(shell, unitEditor, type, preselected, (s) -> {
				try {
					MethodGeneratorUtil.generate(JavaUI.openInEditor(compilationUnit), s);
				} catch (CoreException e) {
					ExceptionHandler.handle(e, shell, ActionMessages.AddGetterSetterAction_error_title,
							ActionMessages.AddGetterSetterAction_error_actionfailed);
				}
			});
		} catch (CoreException e) {
			ExceptionHandler.handle(e, shell, ActionMessages.AddGetterSetterAction_error_title,
					ActionMessages.AddGetterSetterAction_error_actionfailed);
		}
	}

	private static void openDialog(Shell shell, CompilationUnitEditor editor, IType type, IField[] preselected,
			Consumer<MethodGeneratorSettings> generator) throws CoreException {
		if (!isValidType(shell, type)) return;
		BuilderPatternDialog dialog = new BuilderPatternDialog(shell, editor, type);
		dialog.setComparator(new JavaElementComparator());
		dialog.setTitle(ActionMessages.AddGetterSetterAction_error_title);
		dialog.setMessage(ActionMessages.AddGetterSetterAction_dialog_label);
		dialog.setContainerMode(true);
		dialog.setSize(60, 18);
		dialog.setInput(type);

		if (preselected != null && preselected.length > 0) {
			dialog.setInitialSelections(preselected);
			dialog.setExpandedElements(preselected);
		}
		if (dialog.open() == Window.OK) {
			MethodGeneratorSettings settings = dialog.getMethodGeneratorSettings();
			if (settings != null) {
				generator.accept(settings);
			}
		}
	}

	private static boolean isValidType(Shell shell, IType type) throws JavaModelException {
		if (type.isAnnotation()) {
			MessageDialog.openInformation(shell, ActionMessages.AddGetterSetterAction_error_title,
					ActionMessages.AddGetterSetterAction_annotation_not_applicable);
			return false;
		}
		if (type.isInterface()) {
			MessageDialog.openInformation(shell, ActionMessages.AddGetterSetterAction_error_title,
					ActionMessages.AddGetterSetterAction_interface_not_applicable);
			return false;
		}
		if (type.getCompilationUnit() == null) {
			MessageDialog.openInformation(shell, ActionMessages.AddGetterSetterAction_error_title,
					ActionMessages.AddGetterSetterAction_error_not_in_source_file);
			return false;
		}
		if (!ElementValidator.check(type, shell, ActionMessages.AddGetterSetterAction_error_title, true)) return false;
		if (!ActionUtil.isEditable(shell, type)) return false;
		return true;
	}

	public static ICompilationUnit findCompilationUnit(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
			if (selectedObject instanceof ICompilationUnit) return (ICompilationUnit) selectedObject;
			else if (selectedObject instanceof IType) return ((IType) selectedObject).getCompilationUnit();
			else if (selectedObject instanceof IField) return ((IField) selectedObject).getDeclaringType()
					.getCompilationUnit();
		}
		return null;
	}

	private static IField[] guessSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) return guessSelectionByObjects(((IStructuredSelection) selection)
				.toArray());
		return null;
	}

	private static IField[] guessSelectionByObjects(Object[] selectedObjects) {
		List<IField> result = new ArrayList<>();
		for (Object selectedObject : selectedObjects) {
			if (selectedObject instanceof IField) {
				result.add((IField) selectedObject);
			}
		}
		return result.toArray(new IField[result.size()]);
	}

	private DialogUtil() {
		// hide me
	}

}
