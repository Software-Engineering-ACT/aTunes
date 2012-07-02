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

package net.sourceforge.atunes.kernel.modules.pattern;

import net.sourceforge.atunes.model.CDMetadata;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.commonjukebox.plugins.model.PluginApi;

/**
 * Defines patterns based on LocalAudioObject properties to get strings to be used in
 * file or folder names
 * 
 * @author fleax
 */
@PluginApi
public abstract class AbstractPattern {

    static final char PATTERN_NAME_FIRST_CHAR = '%';

    /**
     * Chars representing this pattern
     */
    private String pattern;

    /**
     * Name of this pattern
     */
    private String name;

    /**
     * <code>true</code> if this pattern can be used for massive pattern
     * recognition
     * 
     * For example: song title or track number are not massive patterns as it
     * only apply to one song at a time
     * 
     */
    private boolean massiveRecognitionPattern = false;

    /**
     * <code>true</code> if this pattern can be used for pattern recognition
     * 
     * For example: To be used for recognition application must be able to
     * assign a full attribute value from a pattern Fist artist char is not a
     * recognition pattern as it is not a complete attribute of a song
     * 
     */
    private boolean recognitionPattern = false;

    /**
     * Private constructor
     * 
     * @param patternChar
     * @param name
     * @param recognitionPattern
     * @param massiveRecognitionPattern
     */
    protected AbstractPattern(char patternChar, String name, boolean recognitionPattern, boolean massiveRecognitionPattern) {
        // Force upper case
        this.pattern = StringUtils.getString(PATTERN_NAME_FIRST_CHAR, patternChar).toUpperCase();
        this.name = name;
        this.recognitionPattern = recognitionPattern;
        this.massiveRecognitionPattern = massiveRecognitionPattern;
    }

    /**
     * @return
     */
    public final boolean isMassiveRecognitionPattern() {
		return massiveRecognitionPattern;
	}
    
    /**
     * @return
     */
    public final boolean isRecognitionPattern() {
		return recognitionPattern;
	}
    
    /**
     * @return the pattern
     */
    public final String getPattern() {
        return pattern;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @return the description of this pattern
     */
    public final String getDescription() {
        return I18nUtils.getString(name);
    }

    /**
     * Returns a string, result of apply this pattern to an audio file object
     * @param sourceString
     * @param audioFile
     * @return
     */
    public final String applyPattern(String sourceString, ILocalAudioObject audioFile) {
        if (!pattern.equals(Patterns.getAnyPattern().getPattern())) {
            return sourceString.replaceAll(pattern, getAudioFileStringValue(audioFile));
        }
        return sourceString;
    }

    /**
     * Returns a string, result of apply this pattern to a cd track
     * @param sourceString
     * @param metadata
     * @param trackNumber
     * @return
     */
    public final String applyPattern(String sourceString, CDMetadata metadata, int trackNumber) {
        if (!pattern.equals(Patterns.getAnyPattern().getPattern())) {
        	String replace = getCDMetadataStringValue(metadata, trackNumber);
        	if (replace == null) {
        		replace = "";
        	}
            return sourceString.replaceAll(pattern, replace);
        }
        return sourceString;
    }

    /**
     * Returns string value of an LocalAudioObject to do transformation
     * 
     * @param audioFile
     * @return
     */
    public abstract String getAudioFileStringValue(ILocalAudioObject audioFile);
    
    /**
     * Returns string value of an LocalAudioObject to do transformation
     * @param metadata
     * @param trackNumber
     * @return
     */
    public abstract String getCDMetadataStringValue(CDMetadata metadata, int trackNumber);

}
