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

package net.sourceforge.atunes.kernel.modules.process;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.views.dialogs.ProgressDialog;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.model.LocalAudioObject;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FileUtils;

public abstract class AbstractAudioFileTransferProcess extends AbstractProcess {

    /**
     * The files to be transferred.
     */
    private Collection<LocalAudioObject> filesToTransfer;

    /**
     * List of files transferred. Used if process is canceled to delete these
     * files
     */
    private List<File> filesTransferred;

    /**
     * The dialog used to show the progress of this process
     */
    private ProgressDialog progressDialog;

    /**
     * User selection if an error occurs while transferring
     */
    private String userSelectionWhenErrors = null;

    protected AbstractAudioFileTransferProcess(Collection<LocalAudioObject> collection) {
        this.filesToTransfer = collection;
        this.filesTransferred = new ArrayList<File>();
        setOwner(GuiHandler.getInstance().getFrame().getFrame());
    }

    @Override
    protected long getProcessSize() {
        // Get size of files
        long totalBytes = 0;
        for (LocalAudioObject file : this.filesToTransfer) {
            totalBytes = totalBytes + file.getFile().length();
        }
        return totalBytes;
    }

    @Override
    protected ProgressDialog getProgressDialog() {
        if (progressDialog == null) {
            // Use a TransferProgressDialog
            progressDialog = GuiHandler.getInstance().getNewTransferProgressDialog(getProgressDialogTitle(), getOwner());
            progressDialog.setInfoText(getProgressDialogInformation());
            progressDialog.setCurrentProgress(0);
            progressDialog.setProgressBarValue(0);
            progressDialog.addCancelButtonActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cancelProcess();
                    progressDialog.disableCancelButton();
                }
            });
        }
        return progressDialog;
    }

    @Override
    protected boolean runProcess() {
        boolean errors = false;
        File destination = new File(getDestination());
        long bytesTransferred = 0;
        boolean ignoreAllErrors = false;
        addInfoLog(StringUtils.getString("Transferring ", this.filesToTransfer.size(), " files to ", destination));
        for (Iterator<LocalAudioObject> it = this.filesToTransfer.iterator(); it.hasNext() && !isCanceled();) {
        	LocalAudioObject file = it.next();
            final List<Exception> thrownExceptions = new ArrayList<Exception>();
            File transferredFile = transferAudioFile(destination, file, thrownExceptions);
            filesTransferred.add(transferredFile);
            if (!thrownExceptions.isEmpty()) {
                for (Exception e : thrownExceptions) {
                    addErrorLog(e);
                }
                if (!ignoreAllErrors) {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                userSelectionWhenErrors = (String) GuiHandler.getInstance()
                                        .showMessage(StringUtils.getString(I18nUtils.getString("ERROR"), ": ", thrownExceptions.get(0).getMessage()), I18nUtils.getString("ERROR"),
                                                JOptionPane.ERROR_MESSAGE,
                                                new String[] { I18nUtils.getString("IGNORE"), I18nUtils.getString("IGNORE_ALL"), I18nUtils.getString("CANCEL") });
                            }
                        });
                    } catch (InterruptedException e1) {
                        // Do nothing
                    } catch (InvocationTargetException e1) {
                        // Do nothing
                    }
                    if (I18nUtils.getString("IGNORE").equals(userSelectionWhenErrors)) {
                        // Do nothing, let execution continue
                    } else if (I18nUtils.getString("IGNORE_ALL").equals(userSelectionWhenErrors)) {
                        // Don't display more error messages
                        ignoreAllErrors = true;
                    } else if (I18nUtils.getString("CANCEL").equals(userSelectionWhenErrors)) {
                        // Only in this case set errors to true to force refresh in other case
                        errors = true;

                        // Don't continue
                        break;
                    }
                }
            }
            // Add size to bytes transferred
            bytesTransferred += file.getFile().length();
            setCurrentProgress(bytesTransferred);
        }
        addInfoLog("Transfer process done");
        return !errors;
    }

    @Override
    protected void runCancel() {
        // Remove all transferred files
        for (File f : this.filesTransferred) {
            f.delete();
        }
    }

    /**
     * Return destination of all files to be transferred
     * 
     * @return
     */
    protected abstract String getDestination();

    /**
     * Transfers a file to a destination
     * 
     * @param destination
     * @param file
     * @param list
     *            to add exceptions thrown
     * @return
     * @throws IOException
     */
    protected File transferAudioFile(File destination, LocalAudioObject file, List<Exception> thrownExceptions) {
        String destDir = getDirectory(file, destination, false);
        String newName = getName(file, false);
        File destFile = new File(StringUtils.getString(destDir, SystemProperties.FILE_SEPARATOR, newName));

        try {
            // Now that we (supposedly) have a valid filename write file
            FileUtils.copyFile(file.getFile(), destFile);
        } catch (IOException e) {
            thrownExceptions.add(e);
        }
        return destFile;
    }

    /**
     * @return the filesTransferred
     */
    public List<File> getFilesTransferred() {
        return filesTransferred;
    }

    /**
     * Returns directory structure using import export folder pattern
     * 
     * @param song
     * @param destination
     * @param isMp3Device
     * @return
     */
    public String getDirectory(LocalAudioObject song, File destination, boolean isMp3Device) {
        return getDirectory(song, destination, isMp3Device, ApplicationState.getInstance().getImportExportFolderPathPattern());
    }

    /**
     * Prepares the directory structure in which the song will be written.
     * 
     * @param song
     *            Song to be written
     * @param destination
     *            Destination path
     * @param isMp3Device
     * 
     * @param pattern
     * 
     * @return Returns the directory structure with full path where the file
     *         will be written
     */
    protected String getDirectory(LocalAudioObject song, File destination, boolean isMp3Device, String pattern) {
        String songRelativePath = "";
        if (pattern != null) {
            songRelativePath = FileNameUtils.getValidFolderName(FileNameUtils.getNewFolderPath(pattern, song), isMp3Device);
        }
        return StringUtils.getString(destination.getAbsolutePath(), SystemProperties.FILE_SEPARATOR, songRelativePath);
    }

    /**
     * Returns a valid name to transfer the file using import export file name
     * pattern
     * 
     * @param file
     * @param isMp3Device
     * @return
     */
    public String getName(LocalAudioObject file, boolean isMp3Device) {
        return getName(file, isMp3Device, ApplicationState.getInstance().getImportExportFileNamePattern());
    }

    /**
     * Returns a valid name to transfer the file given as argument. It applies
     * pattern replace given as argument
     * 
     * @param file
     * @param isMp3Device
     * @param pattern
     * @return
     */
    protected String getName(LocalAudioObject file, boolean isMp3Device, String pattern) {
        String newName;
        if (pattern != null) {
            newName = FileNameUtils.getNewFileName(pattern, file);
        } else {
            newName = FileNameUtils.getValidFileName(file.getFile().getName().replace("\\", "\\\\").replace("$", "\\$"), isMp3Device);
        }
        return newName;
    }

}
