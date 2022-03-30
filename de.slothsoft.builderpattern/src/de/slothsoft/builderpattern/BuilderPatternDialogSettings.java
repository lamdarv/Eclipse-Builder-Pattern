package de.slothsoft.builderpattern;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.dialogs.IDialogSettings;

public class BuilderPatternDialogSettings {

	private static final String SETTINGS_SECTION = "BuilderPatternDialog"; //$NON-NLS-1$
	private static final String SORT_ORDER = "SortOrder"; //$NON-NLS-1$
	private static final String ALLOW_FINAL_SETTERS = "AllowFinalSetters"; //$NON-NLS-1$

	private static final MethodGeneratorSortOrder DEFAULT_SORT_ORDER = MethodGeneratorSortOrder.BY_FIELD;

	public static IDialogSettings createDefaultSettings() {
		IDialogSettings dialogSettings = JavaPlugin.getDefault().getDialogSettings();
		IDialogSettings settings = dialogSettings.getSection(SETTINGS_SECTION);
		if (settings == null) {
			settings = dialogSettings.addNewSection(SETTINGS_SECTION);
			settings.put(SORT_ORDER, DEFAULT_SORT_ORDER.name());
			settings.put(ALLOW_FINAL_SETTERS, false);
		}
		return settings;
	}

	private IDialogSettings settings;

	private MethodGeneratorSortOrder sortOrder;
	private boolean allowFinalSetters;

	public BuilderPatternDialogSettings() {
		this(createDefaultSettings());
	}

	public BuilderPatternDialogSettings(IDialogSettings settings) {
		this.settings = settings;
		this.sortOrder = getSortOrder(this.settings.get(SORT_ORDER));
		this.allowFinalSetters = this.settings.getBoolean(ALLOW_FINAL_SETTERS);
	}

	private MethodGeneratorSortOrder getSortOrder(String string) {
		return string == null || string.isEmpty() ? DEFAULT_SORT_ORDER : MethodGeneratorSortOrder.valueOf(string);
	}

	public MethodGeneratorSortOrder getSortOrder() {
		return this.sortOrder;
	}

	public void setSortOrder(MethodGeneratorSortOrder sortOrder) {
		if (this.sortOrder != sortOrder) {
			this.sortOrder = sortOrder;
			this.settings.put(SORT_ORDER, sortOrder == null ? null : sortOrder.name());
		}
	}

	public boolean isAllowFinalSetters() {
		return this.allowFinalSetters;
	}

	public void setAllowFinalSetters(boolean allowFinalSetters) {
		if (this.allowFinalSetters != allowFinalSetters) {
			this.allowFinalSetters = allowFinalSetters;
			this.settings.put(ALLOW_FINAL_SETTERS, allowFinalSetters);
		}
	}

}
