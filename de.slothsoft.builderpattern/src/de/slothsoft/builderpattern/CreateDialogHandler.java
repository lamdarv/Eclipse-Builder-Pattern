package de.slothsoft.builderpattern;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

public class CreateDialogHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof CompilationUnitEditor) {
			DialogUtil.openDialog(HandlerUtil.getActiveShell(event), (CompilationUnitEditor) part);
		}else  {
			ISelection selection = HandlerUtil.getCurrentSelection(event);
			ICompilationUnit compilationUnit = DialogUtil.findCompilationUnit(selection);
			if (compilationUnit != null) {
				DialogUtil.openDialog(HandlerUtil.getActiveShell(event), compilationUnit, selection);
			}
		}
		return null;
	}
}
