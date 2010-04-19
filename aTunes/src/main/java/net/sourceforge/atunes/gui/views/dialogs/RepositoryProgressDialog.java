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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomModalDialog;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.RepositoryLoadCancelAction;
import net.sourceforge.atunes.kernel.actions.RepositoryLoadInBackgroundAction;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The repository progress dialog.
 * 
 * @author fleax
 */
public final class RepositoryProgressDialog extends AbstractCustomModalDialog {

    private static class GlassPaneMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            // Nothing to do
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // Nothing to do
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // Nothing to do
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // Nothing to do
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // Nothing to do
        }
    }

    private static final long serialVersionUID = -3071934230042256578L;

    private JLabel pictureLabel;
    private JLabel label;
    private JLabel progressLabel;
    private JLabel separatorLabel;
    private JLabel totalFilesLabel;
    private JProgressBar progressBar;
    private JLabel folderLabel;
    private JLabel remainingTimeLabel;
    private JButton cancelButton;
    private JButton backgroundButton;
    private MouseListener listener = new GlassPaneMouseListener();

    /**
     * Instantiates a new repository progress dialog.
     * 
     * @param parent
     *            the parent
     */
    public RepositoryProgressDialog(JFrame parent) {
        super(parent, 400, 150, false);
        setContent(getContent());
        GuiUtils.applyComponentOrientation(this);
        backgroundButton.setVisible(false);
        cancelButton.setVisible(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        new RepositoryProgressDialog(null).setVisible(true);
    }

    /**
     * Activate glass pane.
     */
    public void activateGlassPane() {
        ((JFrame) getParent()).getGlassPane().setVisible(true);
        ((JFrame) getParent()).getGlassPane().addMouseListener(listener);
    }

    /**
     * Deactivate glass pane.
     */
    public void deactivateGlassPane() {
        ((JFrame) getParent()).getGlassPane().removeMouseListener(listener);
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getContent() {
        JPanel panel = new JPanel(new GridBagLayout());
        pictureLabel = new JLabel(Images.getImage(Images.APP_LOGO_90));
        label = new JLabel(StringUtils.getString(I18nUtils.getString("LOADING"), "..."));
        progressLabel = new JLabel();
        separatorLabel = new JLabel(" / ");
        totalFilesLabel = new JLabel();
        progressBar = new JProgressBar();
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        folderLabel = new JLabel(" ");
        remainingTimeLabel = new JLabel(" ");
        backgroundButton = new CustomButton(Actions.getAction(RepositoryLoadInBackgroundAction.class));
        cancelButton = new CustomButton(Actions.getAction(RepositoryLoadCancelAction.class));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(backgroundButton);
        buttonsPanel.add(cancelButton);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 5;
        c.insets = new Insets(0, 20, 0, 0);
        panel.add(pictureLabel, c);
        c.gridheight = 1;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 20, 0, 20);
        c.anchor = GridBagConstraints.WEST;
        panel.add(label, c);
        c.gridx = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(5, 0, 0, 3);
        c.anchor = GridBagConstraints.EAST;
        panel.add(progressLabel, c);
        c.gridx = 3;
        c.insets = new Insets(5, 0, 0, 0);
        panel.add(separatorLabel, c);
        c.gridx = 4;
        c.insets = new Insets(5, 0, 0, 20);
        panel.add(totalFilesLabel, c);
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 20, 5, 20);
        c.anchor = GridBagConstraints.CENTER;
        panel.add(progressBar, c);
        c.gridy = 2;
        c.insets = new Insets(0, 20, 0, 20);
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.WEST;
        panel.add(folderLabel, c);
        c.gridy = 3;
        c.insets = new Insets(0, 20, 10, 20);
        panel.add(remainingTimeLabel, c);
        c.gridy = 4;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(buttonsPanel, c);
        return panel;
    }

    /**
     * Gets the folder label.
     * 
     * @return the folder label
     */
    public JLabel getFolderLabel() {
        return folderLabel;
    }

    /**
     * Gets the label.
     * 
     * @return the label
     */
    public JLabel getLabel() {
        return label;
    }

    /**
     * Gets the progress bar.
     * 
     * @return the progress bar
     */
    public JProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * Gets the progress label.
     * 
     * @return the progress label
     */
    public JLabel getProgressLabel() {
        return progressLabel;
    }

    /**
     * Gets the remaining time label.
     * 
     * @return the remaining time label
     */
    public JLabel getRemainingTimeLabel() {
        return remainingTimeLabel;
    }

    /**
     * Gets the total files label.
     * 
     * @return the total files label
     */
    public JLabel getTotalFilesLabel() {
        return totalFilesLabel;
    }

    /**
     * Enable buttons
     * 
     * @param enabled
     * 
     */
    public void setButtonsEnabled(boolean enabled) {
        cancelButton.setEnabled(enabled);
        backgroundButton.setEnabled(enabled);
    }

    /**
     * Show buttons
     * 
     * @param visible
     * 
     */
    public void setButtonsVisible(boolean visible) {
        cancelButton.setVisible(visible);
        backgroundButton.setVisible(visible);
    }

    @Override
    public void setVisible(final boolean b) {
        setLocationRelativeTo(getParent());
        super.setVisible(b);
    }

    public void showProgressDialog() {
        setTitle(I18nUtils.getString("PLEASE_WAIT"));
        setVisible(true);
        activateGlassPane();
        setButtonsVisible(true);
        setButtonsEnabled(true);
    }

    public void hideProgressDialog() {
        setVisible(false);
        deactivateGlassPane();
        //setButtonsVisible(false);
        //setButtonsEnabled(true);
    }
}
