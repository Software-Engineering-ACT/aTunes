/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.actions;



import net.sourceforge.atunes.model.CachedIconFactory;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

public class ShowFoldersInNavigatorAction extends ActionWithColorMutableIcon {

    private static final long serialVersionUID = -3422236983060989235L;

    private INavigationHandler navigationHandler;
    
    private CachedIconFactory folderIcon;
    
    /**
     * @param folderIcon
     */
    public void setFolderIcon(CachedIconFactory folderIcon) {
		this.folderIcon = folderIcon;
	}
    
    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
    
    public ShowFoldersInNavigatorAction() {
        super(I18nUtils.getString("SHOW_FOLDERS"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("SHOW_FOLDERS"));
    }

    @Override
    protected void initialize() {
    	super.initialize();
        putValue(SELECTED_KEY, getState().getViewMode() == ViewMode.FOLDER);
    }
    
    @Override
    protected void executeAction() {
        if (getState().getViewMode() != ViewMode.FOLDER) {
            getState().setViewMode(ViewMode.FOLDER);
            navigationHandler.refreshCurrentView();
        }
    }
    
    @Override
    public IColorMutableImageIcon getIcon(final ILookAndFeel lookAndFeel) {
    	return folderIcon.getColorMutableIcon();
    }
}
