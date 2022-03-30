package de.slothsoft.builderpattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.actions.ActionMessages;
import org.eclipse.jdt.internal.ui.dialogs.SourceActionDialog;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class BuilderPatternDialog extends SourceActionDialog {

	private static final int SELECT_GETTERS_ID = IDialogConstants.CLIENT_ID + 1;
	private static final int SELECT_SETTERS_ID = IDialogConstants.CLIENT_ID + 2;
	private static final int SELECT_BUILDERS_ID = IDialogConstants.CLIENT_ID + 3;

	private final IType type;
	private final BuilderPatternDialogSettings settings;
	private final FieldInfoContentProvider contentProvider;

	private SettersForFinalFieldsFilter settersForFinalFieldsFilter;
	private ArrayList<FieldInfo> previouslySelectedFinals;

	public BuilderPatternDialog(Shell parent, CompilationUnitEditor editor, IType type) throws JavaModelException {
		super(parent, new FieldInfoLabelProvider(), new FieldInfoContentProvider(type), editor, type, false);
		this.type = type;
		this.contentProvider = (FieldInfoContentProvider) getContentProvider();
		this.previouslySelectedFinals = new ArrayList<FieldInfo>();
		this.settings = new BuilderPatternDialogSettings();
		this.settersForFinalFieldsFilter = new SettersForFinalFieldsFilter(this.contentProvider);
	}

	@Override
	protected CheckboxTreeViewer createTreeViewer(Composite parent) {
		CheckboxTreeViewer treeViewer = super.createTreeViewer(parent);
		if (!isAllowFinalSetters()) {
			treeViewer.addFilter(this.settersForFinalFieldsFilter);
		}
		return treeViewer;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(shell, IJavaHelpContextIds.ADD_GETTER_SETTER_SELECTION_DIALOG);
	}

	@Override
	protected Composite createInsertPositionCombo(Composite composite) {
		Button addRemoveFinalCheckbox = addAllowSettersForFinalslCheckbox(composite);
		addRemoveFinalCheckbox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite entryComposite = super.createInsertPositionCombo(composite);
		addSortOrder(entryComposite);
		addVisibilityAndModifiersChoices(entryComposite);
		return entryComposite;
	}

	private Button addAllowSettersForFinalslCheckbox(Composite entryComposite) {
		Button allowSettersForFinalsButton = new Button(entryComposite, SWT.CHECK);
		allowSettersForFinalsButton.setText(ActionMessages.AddGetterSetterAction_allow_setters_for_finals_description);

		allowSettersForFinalsButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isSelected = (((Button) e.widget).getSelection());
				setAllowFinalSetters(isSelected);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		allowSettersForFinalsButton.setSelection(isAllowFinalSetters());
		return allowSettersForFinalsButton;
	}

	private Composite addSortOrder(Composite composite) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(ActionMessages.GetterSetterTreeSelectionDialog_sort_label);
		GridData gd = new GridData(GridData.FILL_BOTH);
		label.setLayoutData(gd);

		final ComboViewer combo = new ComboViewer(composite, SWT.READ_ONLY);
		combo.setContentProvider(ArrayContentProvider.getInstance());
		combo.setInput(MethodGeneratorSortOrder.values());
		combo.setLabelProvider(new MethodGeneratorSortOrderLabelProvider());
		combo.getCombo().setLayoutData(new GridData(GridData.FILL_BOTH));
		combo.setSelection(new StructuredSelection(getSortOrder()));
		combo.getCombo().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setSortOrder((MethodGeneratorSortOrder) ((IStructuredSelection) combo.getSelection()).getFirstElement());
			}
		});
		return composite;
	}

	private FieldInfo[] getEntries(IField field) {
		List<Object> result = Arrays.asList(this.contentProvider.getChildren(field));
		return result.toArray(new FieldInfo[result.size()]);
	}

	private void updateViewerFromFinalSetters(boolean allowFinalSetters) {
		CheckboxTreeViewer treeViewer = getTreeViewer();
		if (getTreeViewer() != null) {
			ArrayList<FieldInfo> newChecked = new ArrayList<FieldInfo>();
			if (allowFinalSetters) {
				newChecked.addAll(this.previouslySelectedFinals);
			}
			this.previouslySelectedFinals.clear();
			Object[] checkedElements = treeViewer.getCheckedElements();
			for (int i = 0; i < checkedElements.length; i++) {
				if (checkedElements[i] instanceof FieldInfo) {
					FieldInfo entry = (FieldInfo) checkedElements[i];
					if (allowFinalSetters || entry.type == FieldInfo.Type.GETTER || !entry.isFinal) {
						newChecked.add(entry);
					} else {
						this.previouslySelectedFinals.add(entry);
					}
				}
			}
			if (allowFinalSetters) {
				treeViewer.removeFilter(this.settersForFinalFieldsFilter);
			} else {
				treeViewer.addFilter(this.settersForFinalFieldsFilter);
			}
			treeViewer.setCheckedElements(newChecked.toArray());
		}
		updateOKStatus();
	}

	@Override
	protected Composite createSelectionButtons(Composite composite) {
		Composite buttonComposite = super.createSelectionButtons(composite);
		buttonComposite.setLayout(GridLayoutFactory.fillDefaults().create());
		createGetterSetterButtons(buttonComposite);
		((GridLayout) buttonComposite.getLayout()).numColumns = 1;
		return buttonComposite;
	}

	private void createGetterSetterButtons(Composite parent) {
		createButton(parent, SELECT_GETTERS_ID, ActionMessages.GetterSetterTreeSelectionDialog_select_getters, false);
		createButton(parent, SELECT_SETTERS_ID, ActionMessages.GetterSetterTreeSelectionDialog_select_setters, false);
		createButton(parent, SELECT_BUILDERS_ID, Messages.getString("BuilderPatternDialog.selectBuilders"), false);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
		switch (buttonId) {
		case SELECT_GETTERS_ID:
			getTreeViewer().setCheckedElements(getGetterSetterElements(FieldInfo.Type.GETTER));
			updateOKStatus();
			break;
		case SELECT_SETTERS_ID:
			getTreeViewer().setCheckedElements(getGetterSetterElements(FieldInfo.Type.SETTER));
			updateOKStatus();
			break;
		case SELECT_BUILDERS_ID:
			getTreeViewer().setCheckedElements(getGetterSetterElements(FieldInfo.Type.BUILDER));
			updateOKStatus();
			break;
		}
	}

	private Object[] getGetterSetterElements(FieldInfo.Type type) {
		List<Object> elements = Arrays.stream(this.contentProvider.getElements(null))
				.flatMap(o -> Arrays.stream(getEntries((IField) o))).filter(info -> info.type == type)
				.collect(Collectors.toList());
		elements.addAll(Arrays.asList(getTreeViewer().getCheckedElements()).stream()
				.filter(o -> o instanceof FieldInfo).collect(Collectors.toList()));
		return elements.toArray();
	}

	public MethodGeneratorSortOrder getSortOrder() {
		return this.settings.getSortOrder();
	}

	public void setSortOrder(MethodGeneratorSortOrder sortOrder) {
		this.settings.setSortOrder(sortOrder);
		if (getTreeViewer() != null) {
			getTreeViewer().refresh();
		}
	}

	public boolean isAllowFinalSetters() {
		return this.settings.isAllowFinalSetters();
	}

	public void setAllowFinalSetters(boolean allowFinalSetters) {
		boolean oldAllowFinalSetters = isAllowFinalSetters();
		this.settings.setAllowFinalSetters(allowFinalSetters);
		if (oldAllowFinalSetters != allowFinalSetters) {
			updateViewerFromFinalSetters(allowFinalSetters);
		}
	}

	public MethodGeneratorSettings getMethodGeneratorSettings() {
		Object[] result = getResult();
		if (result == null) return null;
		List<FieldInfo> fields = getCheckedFields();
		return new MethodGeneratorSettings(this.type, fields.toArray(new FieldInfo[fields.size()]))
				.sortOrder(getSortOrder()).useSynchronized(getSynchronized()).useFinal(getFinal())
				.visibility(getVisibilityModifier()).generateComments(getGenerateComment())
				.position(getElementPosition());
	}

	private List<FieldInfo> getCheckedFields() {
		return Arrays.stream(getResult()).filter(e -> e instanceof FieldInfo).map(e -> (FieldInfo) e)
				.collect(Collectors.toList());
	}
}
