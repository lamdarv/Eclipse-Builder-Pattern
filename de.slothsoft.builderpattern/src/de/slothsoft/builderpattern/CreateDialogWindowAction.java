package de.slothsoft.builderpattern;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CreateDialogWindowAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	private ISelection selection;

	@Override
	public void run(IAction action) {
		IWorkbenchPart part = getActivePart();

		if (part instanceof CompilationUnitEditor) {
			DialogUtil.openDialog(this.window.getShell(), (CompilationUnitEditor) part);
		} else {
			ICompilationUnit compilationUnit = DialogUtil.findCompilationUnit(this.selection);
			if (compilationUnit != null) {
				DialogUtil.openDialog(this.window.getShell(), compilationUnit, this.selection);
			}
		}
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;

		IWorkbenchPart part = getActivePart();
		if (part instanceof CompilationUnitEditor) {
			action.setEnabled(true);
		} else {
			action.setEnabled(DialogUtil.findCompilationUnit(this.selection) != null);
		}
	}

	private IWorkbenchPart getActivePart() {
		return this.window == null ? null : this.window.getActivePage().getActivePart();
	}

	@Override
	public void dispose() {
		this.window = null;
	}

}
