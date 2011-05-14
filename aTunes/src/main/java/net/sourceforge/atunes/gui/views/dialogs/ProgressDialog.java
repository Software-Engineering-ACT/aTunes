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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.CustomFrame;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

public class ProgressDialog extends CustomFrame {

    private static final long serialVersionUID = 5792663290880992661L;

    /** The progress bar. */
    private JProgressBar progressBar;

    /** The image label. */
    private JLabel imagelabel;

    /** The info label. */
    private JLabel infoLabel;

    /** The received label. */
    private JLabel currentLabel;

    /** The separator label. */
    private JLabel separatorLabel;

    /** The total label. */
    private JLabel totalLabel;

    /** The cancel button. */
    private JButton cancelButton;

    /**
     * Instantiates a new transfer progress dialog.
     * 
     * @param title
     *            the title
     */
    public ProgressDialog(String title, Component owner) {
        super(title, 450, 150, owner);
        getRootPane().setWindowDecorationStyle(JRootPane.INFORMATION_DIALOG);
        add(getContent());
        setResizable(false);
        GuiUtils.applyComponentOrientation(this);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * Gets the conentent.
     * 
     * @return the conentent
     */
    private JPanel getContent() {
        JPanel panel = new JPanel(new GridBagLayout());
        progressBar = new JProgressBar();
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        progressBar.setStringPainted(true);
        imagelabel = new JLabel(Images.getImage(Images.APP_LOGO_90));
        infoLabel = new JLabel();
        currentLabel = new JLabel();
        separatorLabel = new JLabel(" / ");
        totalLabel = new JLabel();
        cancelButton = new JButton(I18nUtils.getString("CANCEL"));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 5;
        c.insets = new Insets(0, 20, 0, 0);
        panel.add(imagelabel, c);
        c.gridheight = 1;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 20, 0, 20);
        c.anchor = GridBagConstraints.WEST;
        panel.add(infoLabel, c);
        c.gridx = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(5, 0, 0, 3);
        c.anchor = GridBagConstraints.EAST;
        panel.add(currentLabel, c);
        c.gridx = 3;
        c.insets = new Insets(5, 0, 0, 0);
        panel.add(separatorLabel, c);
        c.gridx = 4;
        c.insets = new Insets(5, 0, 0, 20);
        panel.add(totalLabel, c);
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 20, 5, 20);
        c.anchor = GridBagConstraints.CENTER;
        panel.add(progressBar, c);
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(cancelButton, c);

        return panel;
    }

    /**
     * Sets the info text.
     * 
     * @param s
     *            the new info text
     */
    public void setInfoText(String s) {
        infoLabel.setText(s);
    }

    /**
     * Sets the progress bar value
     * 
     * @param value
     *            the new progress
     */
    public void setProgressBarValue(int value) {
        progressBar.setValue(value);
    }

    /**
     * Sets the byte progress.
     * 
     * @param value
     *            the new byte progress
     */
    public void setCurrentProgress(long value) {
        currentLabel.setText(Long.toString(value));
    }

    /**
     * Sets the total bytes.
     * 
     * @param value
     *            the new total bytes
     */
    public void setTotalProgress(long value) {
        totalLabel.setText(Long.toString(value));
    }

    /**
     * Adds the cancel button action listener.
     * 
     * @param a
     *            the a
     */
    public void addCancelButtonActionListener(ActionListener a) {
        cancelButton.addActionListener(a);
    }

    /**
     * Disables cancel button
     */
    public void disableCancelButton() {
        cancelButton.setEnabled(false);
    }

    /**
     * Overrides default icon
     * 
     * @param icon
     */
    public void setIcon(ImageIcon icon) {
        imagelabel.setIcon(icon);
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        ProgressDialog dialog = new ProgressDialog("test", null);
        dialog.setInfoText("testing");
        dialog.setTotalProgress(100);
        dialog.setCurrentProgress(20);
        dialog.setProgressBarValue(20);
        dialog.setVisible(true);
    }

    /**
     * @return the currentLabel
     */
    protected JLabel getCurrentLabel() {
        return currentLabel;
    }

    /**
     * @return the totalLabel
     */
    protected JLabel getTotalLabel() {
        return totalLabel;
    }
}
