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

package net.sourceforge.atunes.kernel.actions;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.repository.favorites.FavoritesHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.LocalAudioObject;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

public class SetFavoriteAlbumFromNavigatorAction extends AbstractActionOverSelectedObjects<LocalAudioObject> {

    private static final long serialVersionUID = 836910667009804037L;

    SetFavoriteAlbumFromNavigatorAction() {
        super(I18nUtils.getString("SET_FAVORITE_ALBUM"), LocalAudioObject.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("SET_FAVORITE_ALBUM"));
    }

    @Override
    protected void performAction(List<LocalAudioObject> objects) {
        FavoritesHandler.getInstance().toggleFavoriteAlbums(objects);
        NavigationHandler.getInstance().refreshNavigationTable();
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        if (selection.isEmpty()) {
            return false;
        }

        if (ApplicationState.getInstance().getViewMode() == ViewMode.FOLDER) {
            return false;
        }

        for (DefaultMutableTreeNode node : selection) {
            if (!(node.getUserObject() instanceof Album)) {
                return false;
            }
        }

        return true;
    }

}
