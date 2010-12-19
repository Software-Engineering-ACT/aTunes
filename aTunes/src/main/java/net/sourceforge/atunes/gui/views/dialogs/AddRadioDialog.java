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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomModalDialog;
import net.sourceforge.atunes.gui.views.controls.CustomTextField;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class AddRadioDialog.
 */
public final class AddRadioDialog extends AbstractCustomModalDialog {

    private static final long serialVersionUID = 7295438534550341824L;

    /** The radio. */
    private Radio radio;

    /** The name text field. */
    private JTextField nameTextField;

    /** The url text field. */
    private JTextField urlTextField;

    /** The label text field. */
    private JTextField labelTextField;

    /**
     * Instantiates a new adds the radio dialog.
     * 
     * @param owner
     *            the owner
     */
    public AddRadioDialog(JFrame owner) {
        super(owner, 500, 200, true);
        setTitle(I18nUtils.getString("ADD_RADIO"));
        setResizable(false);
        setContent(getContent());
        GuiUtils.applyComponentOrientation(this);
        enableDisposeActionWithEscapeKey();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String args[]) {
        new AddRadioDialog(null).setVisible(true);
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getContent() {
        JPanel panel = new JPanel(new GridBagLayout());

        JLabel nameLabel = new JLabel(I18nUtils.getString("NAME"));
        nameTextField = new CustomTextField();
        JLabel urlLabel = new JLabel(I18nUtils.getString("URL"));
        urlTextField = new CustomTextField();
        JLabel labelLabel = new JLabel(I18nUtils.getString("LABEL"));
        labelTextField = new CustomTextField();

        JButton okButton = new JButton(I18nUtils.getString("OK"));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                radio = new Radio(nameTextField.getText(), urlTextField.getText(), labelTextField.getText());
                AddRadioDialog.this.dispose();
            }
        });
        JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddRadioDialog.this.dispose();
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 10, 5, 10);
        panel.add(nameLabel, c);
        c.gridx = 2;
        c.weightx = 1;
        panel.add(nameTextField, c);
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0;
        panel.add(urlLabel, c);
        c.gridx = 2;
        c.weightx = 1;
        panel.add(urlTextField, c);
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 0;
        panel.add(labelLabel, c);
        c.gridx = 2;
        c.weightx = 1;
        panel.add(labelTextField, c);

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 2;
        c.fill = GridBagConstraints.NONE;
        c.weightx = -1;
        panel.add(new JLabel(Images.getImage(Images.RADIO)), c);

        JPanel auxPanel = new JPanel();
        auxPanel.add(okButton);
        auxPanel.add(cancelButton);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 3;
        c.insets = new Insets(0, 0, 0, 0);
        panel.add(auxPanel, c);

        return panel;
    }

    /**
     * Gets the radio.
     * 
     * @return the radio
     */
    public Radio getRadio() {
        return radio;
    }

}
