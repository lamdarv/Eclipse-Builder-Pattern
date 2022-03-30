package de.slothsoft.builderpattern;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.codemanipulation.IRequestQuery;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.actions.ActionMessages;
import org.eclipse.jdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesSettings;
import org.eclipse.jdt.internal.ui.util.BusyIndicatorRunnableContext;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

public final class MethodGeneratorUtil {

	public static void generate(IEditorPart editor, MethodGeneratorSettings methodGeneratorSettings) {
		IRewriteTarget target = (IRewriteTarget) editor.getAdapter(IRewriteTarget.class);
		if (target != null) {
			target.beginCompoundChange();
		}
		try {
			generate(editor.getSite().getShell(), methodGeneratorSettings);
		} finally {
			if (target != null) {
				target.endCompoundChange();
			}
		}
	}

	private static void generate(Shell shell, MethodGeneratorSettings methodGeneratorSettings) {
		try {
			CodeGenerationSettings settings = JavaPreferencesSettings.getCodeGenerationSettings(methodGeneratorSettings
					.getType().getCompilationUnit().getJavaProject());
			settings.createComments = methodGeneratorSettings.generateComments;
			MethodGeneratorOperation operation = new MethodGeneratorOperation(methodGeneratorSettings, settings);

			IRunnableContext context = JavaPlugin.getActiveWorkbenchWindow();
			if (context == null) {
				context = new BusyIndicatorRunnableContext();
			}
			PlatformUI
					.getWorkbench()
					.getProgressService()
					.runInUI(context, new WorkbenchRunnableAdapter(operation, operation.getSchedulingRule()),
							operation.getSchedulingRule());
		} catch (InterruptedException e) {
			// operation canceled
		} catch (InvocationTargetException e) {
			ExceptionHandler.handle(e, shell, ActionMessages.AddGetterSetterAction_error_title,
					ActionMessages.AddGetterSetterAction_error_actionfailed);
		}
	}

	static IRequestQuery skipReplaceQuery() {
		return new IRequestQuery() {

			@Override
			public int doQuery(IMember method) {
				int[] returnCodes = { IRequestQuery.YES, IRequestQuery.NO, IRequestQuery.YES_ALL, IRequestQuery.CANCEL };
				String skipLabel = ActionMessages.AddGetterSetterAction_SkipExistingDialog_skip_label;
				String replaceLabel = ActionMessages.AddGetterSetterAction_SkipExistingDialog_replace_label;
				String skipAllLabel = ActionMessages.AddGetterSetterAction_SkipExistingDialog_skipAll_label;
				String[] options = { skipLabel, replaceLabel, skipAllLabel, IDialogConstants.CANCEL_LABEL };
				String methodName = JavaElementLabels.getElementLabel(method, JavaElementLabels.M_PARAMETER_TYPES);
				String formattedMessage = Messages.format(
						ActionMessages.AddGetterSetterAction_SkipExistingDialog_message,
						BasicElementLabels.getJavaElementName(methodName));
				return showQueryDialog(formattedMessage, options, returnCodes);
			}
		};
	}

	private static int showQueryDialog(final String message, final String[] buttonLabels, int[] returnCodes) {
		final Shell shell = Display.getCurrent().getActiveShell();
		if (shell == null) {
			JavaPlugin.logErrorMessage("AddGetterSetterAction.showQueryDialog: No active shell found"); //$NON-NLS-1$
			return IRequestQuery.CANCEL;
		}
		final int[] result = { Window.CANCEL };
		shell.getDisplay().syncExec(
				() -> {
					String title = ActionMessages.AddGetterSetterAction_QueryDialog_title;
					MessageDialog dialog = new MessageDialog(shell, title, null, message, MessageDialog.QUESTION,
							buttonLabels, 0);
					result[0] = dialog.open();
				});
		return result[0] < 0 ? IRequestQuery.CANCEL : returnCodes[result[0]];
	}

	private MethodGeneratorUtil() {
		// hide me
	}
}
