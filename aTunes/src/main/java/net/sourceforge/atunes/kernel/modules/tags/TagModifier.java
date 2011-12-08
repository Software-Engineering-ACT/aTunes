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

package net.sourceforge.atunes.kernel.modules.tags;

import java.io.IOException;
import java.util.Collection;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.LocalAudioObjectFormat;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.reference.PictureTypes;

/**
 * Class for writing tags to an audio file. We only use JAudiotagger for it. In
 * general, for writing a complete tag, call setInfo.
 * 
 * In general all methods in this class should test if audio file is writable.
 * This is done calling method AudioFile.setWritable which sets write
 * permissions to underlying file if necessary.
 * 
 * @author sylvain
 */
final class TagModifier {

    /**
     * Delete tags.
     * 
     * @param file
     *            the file
     */
    public void deleteTags(ILocalAudioObject file) {
        try {
            // Be sure file is writable before setting info
            FileUtils.setWritable(file.getFile());
            org.jaudiotagger.audio.AudioFileIO.delete(org.jaudiotagger.audio.AudioFileIO.read(file.getFile()));
        } catch (IOException e) {
        	reportWriteError(file, e);
        } catch (CannotReadException e) {
        	reportWriteError(file, e);
		} catch (CannotWriteException e) {
        	reportWriteError(file, e);
		} catch (TagException e) {
        	reportWriteError(file, e);
		} catch (ReadOnlyFileException e) {
        	reportWriteError(file, e);
		} catch (InvalidAudioFrameException e) {
        	reportWriteError(file, e);
		} catch (Exception e) {
			// We must catch any other exception to avoid throw exceptions outside this method
			reportWriteError(file, e);
		}
    }

    /**
     * Refresh after tag modify.
     * @param audioFilesEditing
     * @param playListHandler
     * @param playerHandler
     */
    public void refreshAfterTagModify(final Collection<ILocalAudioObject> audioFilesEditing, IPlayListHandler playListHandler, IPlayerHandler playerHandler) {
        SwingUtilities.invokeLater(new RefreshTagAfterModifyRunnable(audioFilesEditing, playListHandler, playerHandler));
    }

    /**
     * Writes album to tag.
     * 
     * @param file
     *            File to which the tag should be written
     * @param album
     *            Album of file
     */
    public void setAlbum(ILocalAudioObject file, final String album) {
    	modifyTag(file, new AlbumTagModification(album));
    }

    /**
     * Writes genre to tag.
     * 
     * @param file
     *            File to which the tag should be written
     * @param genre
     *            Genre of file
     */
    public void setGenre(ILocalAudioObject file, final String genre) {
    	modifyTag(file, new GenreTagModification(genre));
    }
    
    /**
     * Writes tag to audiofile using JAudiotagger.
     * @param file
     * @param tag
     * @param localAudioObjectValidator
     */
    public void setInfo(ILocalAudioObject file, ITag tag, ILocalAudioObjectValidator localAudioObjectValidator) {
        setInfo(file, tag, false, null, localAudioObjectValidator);
    }

    /**
     * Writes tag to audio file using JAudiotagger.
     * @param file
     * @param tag
     * @param shouldEditCover
     * @param cover
     * @param localAudioObjectValidator
     */
    public void setInfo(ILocalAudioObject file, ITag tag, boolean shouldEditCover, byte[] cover, ILocalAudioObjectValidator localAudioObjectValidator) {

        String title = tag.getTitle();
        String album = tag.getAlbum();
        String artist = tag.getArtist();
        int year = tag.getYear();
        String comment = tag.getComment();
        String genre = tag.getGenre();
        String lyrics = tag.getLyrics();
        String composer = tag.getComposer();
        int track = tag.getTrackNumber();
        int discNumber = tag.getDiscNumber();
        String albumArtist = tag.getAlbumArtist();

        try {
            // Be sure file is writable before setting info
            FileUtils.setWritable(file.getFile());

            org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file.getFile());
            org.jaudiotagger.tag.Tag newTag = audioFile.getTagOrCreateAndSetDefault();

            if (localAudioObjectValidator.isOneOfTheseFormats(file.getFile().getName(), LocalAudioObjectFormat.MP3)) {
                org.jaudiotagger.audio.mp3.MP3File mp3file = (org.jaudiotagger.audio.mp3.MP3File) audioFile;
                if (mp3file.hasID3v1Tag() && !mp3file.hasID3v2Tag()) {
                    deleteTags(file);
                    audioFile = org.jaudiotagger.audio.AudioFileIO.read(file.getFile());
                    newTag = audioFile.getTagOrCreateAndSetDefault();
                }
            }

            editCover(file, shouldEditCover, cover, newTag);

            // Workaround for mp4 files - strings outside genre list might not be written otherwise
            if (localAudioObjectValidator.isOneOfTheseFormats(file.getFile().getName(), LocalAudioObjectFormat.MP4_1, LocalAudioObjectFormat.MP4_2)) {
                newTag.deleteField(FieldKey.GENRE);
            }

            setTagFields(file, title, album, artist, year, comment, genre, lyrics, composer, track, discNumber, albumArtist, newTag);

            audioFile.setTag(newTag);
            audioFile.commit();
        } catch (IOException e) {
            reportWriteError(file, e);
        } catch (CannotReadException e) {
            reportWriteError(file, e);
		} catch (TagException e) {
            reportWriteError(file, e);
		} catch (ReadOnlyFileException e) {
            reportWriteError(file, e);
		} catch (InvalidAudioFrameException e) {
            reportWriteError(file, e);
		} catch (CannotWriteException e) {
            reportWriteError(file, e);
		} catch (Exception e) {
			// We must catch any other exception to avoid throw exceptions outside this method
			reportWriteError(file, e);
		}
    }

	/**
	 * @param file
	 * @param title
	 * @param album
	 * @param artist
	 * @param year
	 * @param comment
	 * @param genre
	 * @param lyrics
	 * @param composer
	 * @param track
	 * @param discNumber
	 * @param albumArtist
	 * @param newTag
	 * @throws FieldDataInvalidException
	 */
	private void setTagFields(ILocalAudioObject file, String title,
			String album, String artist, int year, String comment,
			String genre, String lyrics, String composer, int track,
			int discNumber, String albumArtist, org.jaudiotagger.tag.Tag newTag)
			throws FieldDataInvalidException {
		setStringTagField(file, newTag, FieldKey.ALBUM, album);
		setStringTagField(file, newTag, FieldKey.ARTIST, artist);
		setStringTagField(file, newTag, FieldKey.COMMENT, comment);
		setStringTagField(file, newTag, FieldKey.GENRE, genre);
		setStringTagField(file, newTag, FieldKey.TITLE, title);
		setNumberTagField(file, newTag, FieldKey.YEAR, year);
		setNumberTagField(file, newTag, FieldKey.TRACK, track);
		if (discNumber <= 0) {
		    newTag.deleteField(FieldKey.DISC_NO);
		} else {
		    // only set the discnumber if we have a useful one. remove previous one to avoid errors
		    String discno = newTag.getFirst(FieldKey.DISC_NO);
		    if (!StringUtils.isEmpty(discno)) {
		    	newTag.deleteField(FieldKey.DISC_NO);
		    }
		    newTag.setField(FieldKey.DISC_NO, Integer.toString(discNumber));
		}
		setStringTagField(file, newTag, FieldKey.LYRICS, lyrics);
		setStringTagField(file, newTag, FieldKey.ALBUM_ARTIST, albumArtist);
		setStringTagField(file, newTag, FieldKey.COMPOSER, composer);
	}

	/**
	 * @param file
	 * @param shouldEditCover
	 * @param cover
	 * @param newTag
	 * @throws ImageReadException
	 * @throws IOException
	 * @throws FieldDataInvalidException
	 */
	private void editCover(ILocalAudioObject file, boolean shouldEditCover,
			byte[] cover, org.jaudiotagger.tag.Tag newTag)
			throws ImageReadException, IOException, FieldDataInvalidException {
		if (shouldEditCover) {
		    // Remove old images
		    try {
		        newTag.deleteArtworkField();
		    } catch (KeyNotFoundException e) {
		    	Logger.error(StringUtils.getString("Could not delte artwork field. File: ", file.getUrl(), " Error: ", e));
		    }

		    if (cover != null) {
		        Artwork artwork = new Artwork();
		        artwork.setBinaryData(cover);
		        artwork.setDescription("cover");
		        artwork.setPictureType(PictureTypes.DEFAULT_ID);
		        artwork.setLinked(false);
		        artwork.setMimeType(Sanselan.getImageInfo(cover).getMimeType());
		        newTag.setField(newTag.createField(artwork));
		    }
		}
	}

    private void setStringTagField(ILocalAudioObject file, org.jaudiotagger.tag.Tag tag, FieldKey fieldKey, String fieldValue) {
        if (fieldValue == null || fieldValue.isEmpty()) {
            // Delete tag field if value is empty
            tag.deleteField(fieldKey);
        } else {
            try {
                tag.setField(tag.createField(fieldKey, fieldValue));
            } catch (FieldDataInvalidException e) {
            	reportWriteFieldError(file, fieldKey, fieldValue, e);
            } catch (KeyNotFoundException e) {
            	reportWriteFieldError(file, fieldKey, fieldValue, e);
            }
        }
    }

    private void setNumberTagField(ILocalAudioObject file, org.jaudiotagger.tag.Tag tag, FieldKey fieldKey, int fieldValue) {
        if (fieldValue == -1) {
            tag.deleteField(fieldKey);
        } else {
            try {
                tag.setField(fieldKey, String.valueOf(fieldValue));
            } catch (KeyNotFoundException e) {
            	reportWriteFieldError(file, fieldKey, Integer.toString(fieldValue), e);
            } catch (FieldDataInvalidException e) {
            	reportWriteFieldError(file, fieldKey, Integer.toString(fieldValue), e);

            }
        }
    }

    /**
     * Sets the lyrics.
     * 
     * @param file
     *            the file
     * @param lyrics
     *            the lyrics
     */
    public void setLyrics(ILocalAudioObject file, final String lyrics) {
    	modifyTag(file, new LyricsTagModification(lyrics));
    }

    /**
     * Writes title name to tag.
     * 
     * @param file
     *            File to which the tag should be written
     * @param newTitle
     *            New title
     */
    public void setTitles(ILocalAudioObject file, final String newTitle) {
    	modifyTag(file, new TitleTagModification(newTitle));
    }

    /**
     * Sets track number on a file.
     * 
     * @param file
     *            File to which the tag should be written
     * @param track
     *            Track number
     */
    public void setTrackNumber(ILocalAudioObject file, final Integer track) {
    	modifyTag(file, new TrackNumberTagModification(track));
    }

    /**
     * Changes file with given modification
     * @param file
     * @param modification
     */
    private void modifyTag(ILocalAudioObject file, ITagModification modification) {
        // Be sure file is writable before setting info
        try {
            FileUtils.setWritable(file.getFile());
            org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file.getFile());
            org.jaudiotagger.tag.Tag newTag = audioFile.getTagOrCreateAndSetDefault();
            modification.modify(newTag);
            audioFile.commit();
        } catch (IOException e) {
            reportWriteError(file, e);
        } catch (CannotReadException e) {
            reportWriteError(file, e);
		} catch (TagException e) {
            reportWriteError(file, e);
		} catch (ReadOnlyFileException e) {
            reportWriteError(file, e);
		} catch (InvalidAudioFrameException e) {
            reportWriteError(file, e);
		} catch (CannotWriteException e) {
            reportWriteError(file, e);
		} catch (Exception e) {
			// We must catch any other exception to avoid throw exceptions outside this method
			reportWriteError(file, e);
		}
    }
    
    /**
     * Logs error while writing
     * @param file
     * @param e
     */
    private final void reportWriteError(ILocalAudioObject file, Exception e) {
        Logger.error(StringUtils.getString("Could not edit tag. File: ", file.getUrl(), " Error: ", e));
        Logger.error(e);
    }
    
    /**
     * Logs error while writing a field
     * @param file
     * @param fieldKey
     * @param fieldValue
     */
    private final void reportWriteFieldError(ILocalAudioObject file, FieldKey fieldKey, String fieldValue, Exception e) {
    	Logger.error(StringUtils.getString("Could not edit tag field ", fieldKey.name(), " with value \"", fieldValue, "\" for file: ", file.getUrl(), " Error: ", e));
    }
}
