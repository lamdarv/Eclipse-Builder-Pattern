package de.slothsoft.builderpattern;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

public class CreateDialogEditorAction implements IEditorActionDelegate {

	private IEditorPart editor;
	private ISelection selection;

	@Override
	public void run(IAction action) {
		if (this.editor instanceof CompilationUnitEditor) {
			DialogUtil.openDialog(this.editor.getEditorSite().getShell(), (CompilationUnitEditor) this.editor);
		} else {
			ICompilationUnit compilationUnit = DialogUtil.findCompilationUnit(this.selection);
			if (compilationUnit != null) {
				Shell shell = this.editor == null ? Display.getCurrent().getActiveShell() : this.editor.getEditorSite()
						.getShell();
				DialogUtil.openDialog(shell, compilationUnit, this.selection);
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.editor = targetEditor;
	}

}
