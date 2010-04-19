/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action raises volume
 * 
 * @author fleax
 * 
 */
public class VolumeUpAction extends AbstractAction {

    private static final long serialVersionUID = 8731458163463902477L;

    VolumeUpAction() {
        super(I18nUtils.getString("VOLUME_UP"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('+'));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PlayerHandler.getInstance().volumeUp();
    }

}
