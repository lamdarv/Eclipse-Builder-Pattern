package de.slothsoft.builderpattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.core.manipulation.StubUtility;
import org.eclipse.jdt.internal.corext.CorextMessages;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationMessages;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.codemanipulation.GetterSetterUtil;
import org.eclipse.jdt.internal.corext.codemanipulation.IRequestQuery;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.dom.ModifierRewrite;
import org.eclipse.jdt.internal.corext.refactoring.util.RefactoringASTParser;
import org.eclipse.jdt.internal.corext.util.CodeFormatterUtil;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.Resources;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.text.edits.TextEdit;

import de.slothsoft.builderpattern.FieldInfo.Type;

public final class MethodGeneratorOperation implements IWorkspaceRunnable {

	/** The empty strings constant */
	private static final String[] EMPTY_STRINGS = new String[0];

	private final MethodGeneratorSettings methodGeneratorSettings;
	private final CodeGenerationSettings settings;
	private final CompilationUnit compilationUnit;

	private boolean skipAllExisting;
	private IRequestQuery skipExistingQuery = MethodGeneratorUtil.skipReplaceQuery();

	/**
	 * Creates a new add getter / builder / setter operation.
	 *
	 * @param methodGeneratorSettings the method generator info
	 * @param settings the code generation settings
	 */

	public MethodGeneratorOperation(MethodGeneratorSettings methodGeneratorSettings, CodeGenerationSettings settings) {
		this.methodGeneratorSettings = methodGeneratorSettings;
		this.settings = settings;
		this.compilationUnit = new RefactoringASTParser(ASTProvider.SHARED_AST_LEVEL).parse(methodGeneratorSettings
				.getType().getCompilationUnit(), true);
	}

	@Override
	public final void run(IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			List<FieldInfo> generatedFieldInfos = getGeneratedFieldInfos();
			monitor.setTaskName(CodeGenerationMessages.AddGetterSetterOperation_description);
			monitor.beginTask("", generatedFieldInfos.size()); //$NON-NLS-1$

			IType type = this.methodGeneratorSettings.getType();
			final ICompilationUnit unit = type.getCompilationUnit();
			final ASTRewrite astRewrite = ASTRewrite.create(this.compilationUnit.getAST());
			ListRewrite listRewriter = createListRewriter(type, astRewrite);

			this.skipAllExisting = (this.skipExistingQuery == null);

			for (FieldInfo generatedFieldInfo : generatedFieldInfos) {
				generateField(generatedFieldInfo, listRewriter, astRewrite);
				monitor.worked(1);
				if (monitor.isCanceled()) throw new OperationCanceledException();
			}
			applyEdit(unit, astRewrite.rewriteAST(), false, SubMonitor.convert(monitor).split(1));
		} finally {
			monitor.done();
		}
	}

	static void applyEdit(ICompilationUnit cu, TextEdit edit, boolean save, IProgressMonitor monitor)
			throws CoreException {
		IFile file = (IFile) cu.getResource();
		if (!save || !file.exists()) {
			cu.applyTextEdit(edit, monitor);
		} else {
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			monitor.beginTask(CorextMessages.JavaModelUtil_applyedit_operation, 2);
			try {
				IStatus status = Resources.makeCommittable(file, null);
				if (!status.isOK()) {
					throw new CoreException(status);
				}
				cu.applyTextEdit(edit, SubMonitor.convert(monitor).split(1));
				cu.save(SubMonitor.convert(monitor).split(1), true);
			} finally {
				monitor.done();
			}
		}
	}

	private List<FieldInfo> getGeneratedFieldInfos() {
		List<FieldInfo> result = new ArrayList<>(Arrays.asList(this.methodGeneratorSettings.getFields()));
		result.sort(this.methodGeneratorSettings.getSortOrder().getComparator());
		return result;
	}

	private ListRewrite createListRewriter(IType type, ASTRewrite astRewrite) throws CoreException {
		if (type.isAnonymous()) {
			final ClassInstanceCreation creation = (ClassInstanceCreation) ASTNodes.getParent(
					NodeFinder.perform(this.compilationUnit, type.getNameRange()), ClassInstanceCreation.class);
			if (creation != null) {
				final AnonymousClassDeclaration declaration = creation.getAnonymousClassDeclaration();
				if (declaration != null) return astRewrite.getListRewrite(declaration,
						AnonymousClassDeclaration.BODY_DECLARATIONS_PROPERTY);
			}
		} else {
			final AbstractTypeDeclaration declaration = (AbstractTypeDeclaration) ASTNodes.getParent(
					NodeFinder.perform(this.compilationUnit, type.getNameRange()), AbstractTypeDeclaration.class);
			if (declaration != null) return astRewrite.getListRewrite(declaration,
					declaration.getBodyDeclarationsProperty());
		}
		throw new CoreException(new Status(IStatus.ERROR, JavaUI.ID_PLUGIN, IStatus.ERROR,
				CodeGenerationMessages.AddGetterSetterOperation_error_input_type_not_found, null));
	}

	private void generateField(FieldInfo generatedFieldInfo, ListRewrite listRewriter, ASTRewrite astRewrite)
			throws OperationCanceledException, CoreException {
		switch (generatedFieldInfo.type) {
		case GETTER:
			generateGetterMethod(generatedFieldInfo.getField(), listRewriter);
			break;
		case SETTER:
			generateSetterMethod(generatedFieldInfo.getField(), astRewrite, listRewriter);
			break;
		case BUILDER:
			generateBuilderMethod(generatedFieldInfo.getField(), astRewrite, listRewriter);
			break;
		}
	}

	/**
	 * Generates a new getter method for the specified field
	 *
	 * @param field the field
	 * @param rewrite the list rewrite to use
	 * @throws CoreException if an error occurs
	 * @throws OperationCanceledException if the operation has been cancelled
	 */

	private void generateGetterMethod(final IField field, final ListRewrite rewrite) throws CoreException,
			OperationCanceledException {
		final IType type = field.getDeclaringType();
		final String name = GetterSetterUtil.getGetterName(field, null);
		final IMethod existing = JavaModelUtil.findMethod(name, EMPTY_STRINGS, false, type);
		if (existing == null || !querySkipExistingMethods(existing)) {
			IJavaElement sibling = null;
			if (existing != null) {
				sibling = StubUtility.findNextSibling(existing);
				removeExistingAccessor(existing, rewrite);
			} else {
				sibling = this.methodGeneratorSettings.position;
			}
			ASTNode insertion = getNodeToInsertBefore(rewrite, sibling);
			addNewAccessor(type, field, GetterSetterUtil.getGetterStub(field, name, this.settings.createComments,
					this.methodGeneratorSettings.visibility | (field.getFlags() & Flags.AccStatic)), rewrite, insertion);
		}
	}

	/**
	 * Evaluates the insertion position of a new node.
	 *
	 * @param listRewrite The list rewriter to which the new node will be added
	 * @param sibling The Java element before which the new element should be added.
	 * @return the AST node of the list to insert before or null to insert as last.
	 * @throws JavaModelException thrown if accessing the Java element failed
	 */

	public static ASTNode getNodeToInsertBefore(ListRewrite listRewrite, IJavaElement sibling) throws JavaModelException {
		if (sibling instanceof IMember) {
			ISourceRange sourceRange= ((IMember) sibling).getSourceRange();
			if (sourceRange == null) {
				return null;
			}
			int insertPos= sourceRange.getOffset();

			List<? extends ASTNode> members= listRewrite.getOriginalList();
			for (int i= 0; i < members.size(); i++) {
				ASTNode curr= members.get(i);
				if (curr.getStartPosition() >= insertPos) {
					return curr;
				}
			}
		}
		return null;
}
	
	/**
	 * Generates a new setter method for the specified field
	 * 
	 * @param field the field
	 * @param astRewrite the AST rewrite to use
	 * @param rewrite the list rewrite to use
	 * @throws CoreException if an error occurs
	 * @throws OperationCanceledException if the operation has been cancelled
	 */

	private void generateSetterMethod(final IField field, ASTRewrite astRewrite, final ListRewrite rewrite)
			throws CoreException, OperationCanceledException {
		final IType type = field.getDeclaringType();
		final String name = GetterSetterUtil.getSetterName(field, null);
		final IMethod existing = JavaModelUtil.findMethod(name, new String[] { field.getTypeSignature() }, false, type);
		if (existing == null || !querySkipExistingMethods(existing)) {
			IJavaElement sibling = null;
			if (existing != null) {
				sibling = StubUtility.findNextSibling(existing);
				removeExistingAccessor(existing, rewrite);
			} else {
				sibling = this.methodGeneratorSettings.position;
			}
			ASTNode insertion = getNodeToInsertBefore(rewrite, sibling);
			addNewAccessor(type, field, GetterSetterUtil.getSetterStub(field, name, this.settings.createComments,
					this.methodGeneratorSettings.visibility | (field.getFlags() & Flags.AccStatic)), rewrite, insertion);
			if (Flags.isFinal(field.getFlags())) {
				ASTNode fieldDecl = ASTNodes.getParent(NodeFinder.perform(this.compilationUnit, field.getNameRange()),
						FieldDeclaration.class);
				if (fieldDecl != null) {
					ModifierRewrite.create(astRewrite, fieldDecl).setModifiers(0, Modifier.FINAL, null);
				}
			}
		}
	}

	/**
	 * Generates a new builder method for the specified field
	 * 
	 * @param field the field
	 * @param astRewrite the AST rewrite to use
	 * @param rewrite the list rewrite to use
	 * @throws CoreException if an error occurs
	 * @throws OperationCanceledException if the operation has been cancelled
	 */

	private void generateBuilderMethod(final IField field, ASTRewrite astRewrite, final ListRewrite rewrite)
			throws CoreException, OperationCanceledException {
		final IType type = field.getDeclaringType();
		final String name = BuilderPatternUtil.getBuilderName(field.getElementName());
		final IMethod existing = JavaModelUtil.findMethod(name, new String[] { type.getElementName() }, false, type);
		if (existing == null || !querySkipExistingMethods(existing)) {
			IJavaElement sibling = null;
			if (existing != null) {
				sibling = StubUtility.findNextSibling(existing);
				removeExistingAccessor(existing, rewrite);
			} else {
				sibling = this.methodGeneratorSettings.position;
			}
			ASTNode insertion = getNodeToInsertBefore(rewrite, sibling);
			boolean hasSetter = hasSetter(field);
			BuilderPatternSettings builderSettings = new BuilderPatternSettings(field).builderName(name)
					.hasSetter(hasSetter).addComments(this.settings.createComments)
					.flags(this.methodGeneratorSettings.visibility | (field.getFlags() & Flags.AccStatic));
			addNewAccessor(type, field, BuilderPatternUtil.getBuilderStub(builderSettings), rewrite, insertion);
			if (Flags.isFinal(field.getFlags())) {
				ASTNode fieldDecl = ASTNodes.getParent(NodeFinder.perform(this.compilationUnit, field.getNameRange()),
						FieldDeclaration.class);
				if (fieldDecl != null) {
					ModifierRewrite.create(astRewrite, fieldDecl).setModifiers(0, Modifier.FINAL, null);
				}
			}
		}
	}

	private boolean hasSetter(IField field) throws JavaModelException {
		if (shouldCreate(field, FieldInfo.Type.SETTER)) return true;
		final String name = GetterSetterUtil.getSetterName(field, null);
		return JavaModelUtil.findMethod(name, new String[] { field.getTypeSignature() }, false,
				field.getDeclaringType()) != null;
	}

	private boolean shouldCreate(IField field, Type type) {
		for (FieldInfo info : this.methodGeneratorSettings.fields) {
			if (info.field == field && info.type == type) return true;
		}
		return false;
	}

	/**
	 * Adds a new accessor for the specified field.
	 *
	 * @param type the type
	 * @param field the field
	 * @param contents the contents of the accessor method
	 * @param rewrite the list rewrite to use
	 * @param insertion the insertion point
	 * @throws JavaModelException if an error occurs
	 */
	private void addNewAccessor(final IType type, final IField field, final String contents, final ListRewrite rewrite,
			final ASTNode insertion) throws JavaModelException {
		final String delimiter = StubUtility.getLineDelimiterUsed(type);
		final MethodDeclaration declaration = (MethodDeclaration) rewrite.getASTRewrite().createStringPlaceholder(
				CodeFormatterUtil.format(CodeFormatter.K_CLASS_BODY_DECLARATIONS, contents, 0, delimiter,
						field.getJavaProject()), ASTNode.METHOD_DECLARATION);
		if (insertion != null) {
			rewrite.insertBefore(declaration, insertion, null);
		} else {
			rewrite.insertLast(declaration, null);
		}
	}

	/**
	 * Returns the scheduling rule for this operation.
	 *
	 * @return the scheduling rule
	 */

	final ISchedulingRule getSchedulingRule() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * Queries the user whether to skip existing methods.
	 *
	 * @param method the method in question
	 * @return <code>true</code> to skip existing methods, <code>false</code> otherwise
	 * @throws OperationCanceledException if the operation has been cancelled
	 */

	private boolean querySkipExistingMethods(final IMethod method) throws OperationCanceledException {
		if (!this.skipAllExisting) {
			switch (this.skipExistingQuery.doQuery(method)) {
			case IRequestQuery.CANCEL:
				throw new OperationCanceledException();
			case IRequestQuery.NO:
				return false;
			case IRequestQuery.YES_ALL:
				this.skipAllExisting = true;
			}
		}
		return true;
	}

	/**
	 * Removes an existing accessor method.
	 *
	 * @param accessor the accessor method to remove
	 * @param rewrite the list rewrite to use
	 * @throws JavaModelException if an error occurs
	 */

	private void removeExistingAccessor(final IMethod accessor, final ListRewrite rewrite) throws JavaModelException {
		final MethodDeclaration declaration = (MethodDeclaration) ASTNodes.getParent(
				NodeFinder.perform(rewrite.getParent().getRoot(), accessor.getNameRange()), MethodDeclaration.class);
		if (declaration != null) {
			rewrite.remove(declaration, null);
		}
	}

	public IRequestQuery getSkipExistingQuery() {
		return this.skipExistingQuery;
	}

	public void setSkipExistingQuery(IRequestQuery skipExistingQuery) {
		this.skipExistingQuery = skipExistingQuery;
	}

}
