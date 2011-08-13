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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class EditTitlesDialog.
 */
public final class EditTitlesDialog extends AbstractCustomDialog {

    private static final long serialVersionUID = -7937735545263913179L;

    /** The table. */
    private JTable table;

    /** The retrieve from amazon. */
    private JButton retrieveTitles;

    /** The ok button. */
    private JButton okButton;

    /** The cancel button. */
    private JButton cancelButton;

    /**
     * Instantiates a new edits the titles dialog.
     * 
     * @param owner
     *            the owner
     */
    public EditTitlesDialog(JFrame owner) {
        super(owner, 500, 400, true, true);
        add(getContent());
    }

    /**
     * Gets the cancel button.
     * 
     * @return the cancel button
     */
    public JButton getCancelButton() {
        return cancelButton;
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getContent() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        table = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTable();
        table.setOpaque(false);
        JScrollPane scrollPane = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableScrollPane(table);
        retrieveTitles = new JButton(I18nUtils.getString("GET_TITLES"));
        okButton = new JButton(I18nUtils.getString("OK"));
        cancelButton = new JButton(I18nUtils.getString("CANCEL"));

        GridBagConstraints c = new GridBagConstraints();

        JPanel auxPanel = new JPanel(new GridBagLayout());
        auxPanel.setOpaque(false);
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        auxPanel.add(retrieveTitles, c);
        c.gridx = 1;
        c.weightx = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 0, 0, 10);
        auxPanel.add(okButton, c);
        c.gridx = 2;
        c.insets = new Insets(0, 0, 0, 0);
        auxPanel.add(cancelButton, c);

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 10, 10);
        panel.add(scrollPane, c);

        c.gridy = 1;
        c.weighty = 0;
        c.insets = new Insets(0, 20, 10, 20);
        panel.add(auxPanel, c);

        return panel;
    }

    /**
     * Gets the ok button.
     * 
     * @return the ok button
     */
    public JButton getOkButton() {
        return okButton;
    }

    /**
     * Gets the retrieve from amazon.
     * 
     * @return the retrieve from amazon
     */
    public JButton getRetrieveTitles() {
        return retrieveTitles;
    }

    /**
     * Gets the table.
     * 
     * @return the table
     */
    public JTable getTable() {
        return table;
    }

}
