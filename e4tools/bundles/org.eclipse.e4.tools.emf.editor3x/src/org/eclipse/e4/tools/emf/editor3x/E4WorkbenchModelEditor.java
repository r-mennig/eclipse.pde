/*******************************************************************************
 * Copyright (c) 2010 BestSolution.at and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 ******************************************************************************/
package org.eclipse.e4.tools.emf.editor3x;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.tools.compat.parts.DIEditorPart;
import org.eclipse.e4.tools.emf.ui.common.IModelResource.ModelListener;
import org.eclipse.e4.tools.emf.ui.internal.wbm.ApplicationModelEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.texteditor.FindReplaceAction;
import org.osgi.framework.FrameworkUtil;

@SuppressWarnings("restriction")
public class E4WorkbenchModelEditor extends
DIEditorPart<ApplicationModelEditor> {
	private UndoAction undoAction;
	private RedoAction redoAction;

	private final ModelListener listener = new ModelListener() {

		@Override
		public void dirtyChanged() {
			firePropertyChange(PROP_DIRTY);
		}

		@Override
		public void commandStackChanged() {

		}
	};

	public E4WorkbenchModelEditor() {
		super(ApplicationModelEditor.class, COPY | CUT | PASTE);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		setPartName(getEditorInput().getName());
	}

	@Override
	protected void makeActions() {
		super.makeActions();
		undoAction = new UndoAction(getComponent().getModelProvider());
		undoAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_UNDO);

		redoAction = new RedoAction(getComponent().getModelProvider());
		redoAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_REDO);

		FindReplaceAction findReplaceAction = new FindReplaceAction(
				Platform.getResourceBundle(FrameworkUtil.getBundle(getClass())),
				"find_replace_action_", this); //$NON-NLS-1$
		findReplaceAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_FIND_AND_REPLACE);

		getEditorSite().getActionBars().setGlobalActionHandler(
				ActionFactory.UNDO.getId(), undoAction);
		getEditorSite().getActionBars().setGlobalActionHandler(
				ActionFactory.REDO.getId(), redoAction);
		getEditorSite().getActionBars().setGlobalActionHandler(ActionFactory.FIND.getId(), findReplaceAction);
	}

	@Override
	public void dispose() {
		if (undoAction != null) {
			undoAction.dispose();
		}

		if (redoAction != null) {
			redoAction.dispose();
		}

		if (listener != null && getComponent() != null && getComponent().getModelProvider() != null) {
			getComponent().getModelProvider().removeModelListener(listener);
		}

		super.dispose();
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		ApplicationModelEditor component = getComponent();
		if (component != null) {
			T adapted = Adapters.adapt(component, adapter);
			if (adapted != null) {
				return adapted;
			}
		}
		return super.getAdapter(adapter);
	}
}
