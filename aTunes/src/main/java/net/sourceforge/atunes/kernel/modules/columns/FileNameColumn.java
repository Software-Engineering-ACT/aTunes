/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.columns;

import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.AudioObject;

public class FileNameColumn extends AbstractColumn {

    
    private static final long serialVersionUID = -6243616734204965925L;

    public FileNameColumn() {
        super("FILE", String.class);
        setWidth(250);
        setVisible(false);
    }

    @Override
    protected int ascendingCompare(AudioObject ao1, AudioObject ao2) {
        return ((AudioFile) ao1).getFile().getName().compareTo(((AudioFile) ao2).getFile().getName());
    }

    @Override
    public Object getValueFor(AudioObject audioObject) {
        if (audioObject instanceof Radio) {
            return "";
        }
        if (audioObject instanceof PodcastFeedEntry) {
            return "";
        }
        return ((AudioFile) audioObject).getFile().getName();
    }

}
