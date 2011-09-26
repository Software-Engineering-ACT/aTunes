/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.kernel.modules.context;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.lookandfeel.AbstractListCellRendererCode;
import net.sourceforge.atunes.gui.views.panels.ContextPanel;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IContextPanel;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IState;

class ContextController extends AbstractSimpleController<ContextPanel> {

	private IContextHandler contextHandler;
	
	private ILookAndFeel lookAndFeel;
	
	/**
	 * @param componentControlled
	 * @param state
	 * @param contextHandler
	 * @param lookAndFeel
	 */
	ContextController(ContextPanel componentControlled, IState state, IContextHandler contextHandler, ILookAndFeel lookAndFeel) {
		super(componentControlled, state);
		this.contextHandler = contextHandler;
		this.lookAndFeel = lookAndFeel;
		addBindings();
	}

	@Override
	public void addBindings() {
		if (lookAndFeel.customComboBoxRenderersSupported()) {
			getComponentControlled().getContextSelector().setRenderer(lookAndFeel.getListCellRenderer(new AbstractListCellRendererCode() {

				@Override
				public JComponent getComponent(JComponent superComponent, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					((JLabel)superComponent).setIcon(((IContextPanel)value).getIcon().getIcon(lookAndFeel.getPaintForColorMutableIcon(superComponent, isSelected || cellHasFocus)));
					((JLabel)superComponent).setText(((IContextPanel)value).getTitle());
					return superComponent;
				}
			}));
		}
	}

	/**
	 * Selects context tab
	 * @param selectedContextTab
	 */
	void setContextTab(String selectedContextTab) {
		getComponentControlled().setSelectedContextTab(selectedContextTab);
		contextHandler.contextPanelChanged();
	}
	
	/**
	 * Returns context tab
	 * @return
	 */
	IContextPanel getContextTab() {
		return getComponentControlled().getSelectedContextTab();
	}

	void updateContextTabs() {
		getComponentControlled().updateContextTabs();
	}

	void removeContextPanel(IContextPanel instance) {
		getComponentControlled().removeContextPanel(instance);
	}

	/**
	 * Adds context panels
	 * @param contextPanels
	 */
	void addContextPanels(List<IContextPanel> contextPanels) {
		for (IContextPanel panel : contextPanels) {
			getComponentControlled().addContextPanel(panel);
		}		
	}

	/**
	 * Enables listening for combo box selections by user
	 */
	void enableContextComboListener() {
		// Wait until initial context panel selection is done
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				getComponentControlled().getContextSelector().addItemListener(new ItemListener() {
					
					@Override
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							getComponentControlled().showContextPanel((IContextPanel)e.getItem());
							contextHandler.contextPanelChanged();
						}
		            }
		        });
			}
		});
		
	}

}
