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

import net.sourceforge.atunes.kernel.modules.tags.TagEditionOperations;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.LocalAudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Calls process to set lyrics automatically
 * 
 * @author fleax
 * 
 */
public class AutoSetLyricsAction extends AbstractActionOverSelectedObjects<LocalAudioObject> {

    private static final long serialVersionUID = 4778415252933283915L;

    AutoSetLyricsAction() {
        super(I18nUtils.getString("AUTO_SET_LYRICS"), LocalAudioObject.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("AUTO_SET_LYRICS"));
    }

    @Override
    protected void performAction(List<LocalAudioObject> objects) {
        TagEditionOperations.addLyrics(objects);
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return !rootSelected && !selection.isEmpty();
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        return !selection.isEmpty();
    }

}
