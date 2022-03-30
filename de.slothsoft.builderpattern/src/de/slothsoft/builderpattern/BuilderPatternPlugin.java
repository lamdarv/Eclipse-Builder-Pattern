package de.slothsoft.builderpattern;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class BuilderPatternPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "de.slothsoft.builderpattern"; //$NON-NLS-1$

	private static BuilderPatternPlugin plugin;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static BuilderPatternPlugin getDefault() {
		return plugin;
	}

}
