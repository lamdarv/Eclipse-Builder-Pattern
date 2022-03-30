package de.slothsoft.builderpattern;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.corext.codemanipulation.GetterSetterUtil;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jdt.ui.JavaElementImageDescriptor;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

class FieldInfoLabelProvider extends JavaElementLabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof FieldInfo) {
			FieldInfo entry = (FieldInfo) element;
			try {
				if (entry.type == FieldInfo.Type.GETTER) {
					return GetterSetterUtil.getGetterName(entry.field, null) + "()"; //$NON-NLS-1$
				}
				String bracket = '(' + Signature.getSimpleName(Signature.toString(entry.field.getTypeSignature())) + ')';
				if (entry.type == FieldInfo.Type.SETTER) {
					return GetterSetterUtil.getSetterName(entry.field, null) + bracket;
				}
				return entry.field.getElementName() + bracket; //$NON-NLS-1$
			} catch (JavaModelException e) {
				return ""; //$NON-NLS-1$
			}
		}
		return super.getText(element);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof FieldInfo) {
			int flags = 0;
			try {
				flags = ((FieldInfo) element).field.getFlags();
			} catch (JavaModelException e) {
				JavaPlugin.log(e);
			}
			ImageDescriptor desc = JavaElementImageProvider.getFieldImageDescriptor(false, Flags.AccPublic);
			int adornmentFlags = Flags.isStatic(flags) ? JavaElementImageDescriptor.STATIC : 0;
			desc = new JavaElementImageDescriptor(desc, adornmentFlags, JavaElementImageProvider.BIG_SIZE);
			return JavaPlugin.getImageDescriptorRegistry().get(desc);
		}
		return super.getImage(element);
	}
}
