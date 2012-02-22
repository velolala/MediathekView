/*
 * MediathekView
 * Copyright (C) 2008 W. Xaver
 * W.Xaver[at]googlemail.com
 * http://zdfmediathk.sourceforge.net/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package mediathek.gui.dialogEinstellungen;

import mediathek.gui.dialog.*;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import mediathek.Daten;
import mediathek.Konstanten;
import mediathek.Log;
import mediathek.daten.DDaten;
import mediathek.gui.PanelVorlage;

public class PanelImportFilmliste extends PanelVorlage {

    public boolean ok = false;
    public String ziel;
    private boolean export = true;
    private boolean istDatei; // Datei oder URL

    public PanelImportFilmliste(DDaten d, boolean eexport, boolean ddatei) {
        super(d);
        initComponents();
        export = eexport;
        istDatei = ddatei;
        init();
    }

//    @Override
//    public void neuLaden() {
//        init();
//    }
    private void init() {
        if (export) {
            // export
            jTextFieldPfad.setText(Daten.system[Konstanten.SYSTEM_EXPORT_DATEI_NR]);
            jTextFieldPfad.getDocument().addDocumentListener(new BeobTextFeld(Konstanten.SYSTEM_EXPORT_DATEI_NR));
        } else {
            // import
            jButtonExportieren.setText("Importieren");
            if (istDatei) {
                jTextFieldPfad.setText(Daten.system[Konstanten.SYSTEM_IMPORT_DATEI_NR]);
                jTextFieldPfad.getDocument().addDocumentListener(new BeobTextFeld(Konstanten.SYSTEM_IMPORT_DATEI_NR));
            } else {
                // import url
                jLabelDatei.setText("URL: ");
                jButtonExportPfad.setVisible(false);
//                jTextFieldPfad.setText(Daten.system[Konstanten.SYSTEM_IMPORT_URL_DIALOG_NR]);
//                jTextFieldPfad.getDocument().addDocumentListener(new BeobTextFeld(Konstanten.SYSTEM_IMPORT_URL_DIALOG_NR));
            }
        }
        jButtonExportieren.addActionListener(new BeobImportExport());
        jButtonExportPfad.addActionListener(new BeobPfad());
    }

    private void filmeImportieren() {
        if (jTextFieldPfad.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Keine Datei angegeben", "Pfad", JOptionPane.INFORMATION_MESSAGE);
        } else {
            try {
//////////                if (istDatei) {
//////////                    daten.filmeLaden.filmeImportDatei(daten.system[Konstanten.SYSTEM_IMPORT_DATEI_NR], istDatei);
//////////                } else {
//////////                    daten.filmeLaden.filmeImportDatei(daten.system[Konstanten.SYSTEM_IMPORT_URL_NR], istDatei);
//////////                }
            } catch (Exception ex) {
                Log.fehlerMeldung("PanelExportImportDateiUrl.filmeImportieren", ex);
            }
        }
    }

    private void filmeExportieren() {
        int ret = -1;
        String d = Daten.system[Konstanten.SYSTEM_EXPORT_DATEI_NR];
        if (d.equals("")) {
            JOptionPane.showMessageDialog(null, "Keine Datei angegeben", "Pfad", JOptionPane.INFORMATION_MESSAGE);
        } else {
            try {
                if (new File(d).exists()) {
                    ret = JOptionPane.showConfirmDialog(null, "Datei:  " + "\"" + d + "\"" + "  existiert bereits", "Überschreiben?",
                            JOptionPane.YES_NO_OPTION);
                } else {
                    ret = JOptionPane.OK_OPTION;
                }
                if (ret == JOptionPane.OK_OPTION) {
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//////////////                    daten.ioXmlSchreiben.filmeSchreiben(d, daten.listeFilme);
                }
            } catch (Exception ex) {
                Log.fehlerMeldung("PanelExportImportDateiUrl.filmeExportieren", ex);
            }
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jTextFieldPfad = new javax.swing.JTextField();
        jButtonExportPfad = new javax.swing.JButton();
        jButtonExportieren = new javax.swing.JButton();
        jLabelDatei = new javax.swing.JLabel();

        jPanel4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jButtonExportPfad.setText(":::");

        jButtonExportieren.setText("Exportieren");

        jLabelDatei.setText("Datei:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelDatei)
                .addGap(18, 18, 18)
                .addComponent(jTextFieldPfad, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonExportPfad)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonExportieren)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jTextFieldPfad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelDatei)
                    .addComponent(jButtonExportieren)
                    .addComponent(jButtonExportPfad))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonExportPfad, jButtonExportieren, jTextFieldPfad});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonExportPfad;
    private javax.swing.JButton jButtonExportieren;
    private javax.swing.JLabel jLabelDatei;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField jTextFieldPfad;
    // End of variables declaration//GEN-END:variables

    private class BeobTextFeld implements DocumentListener {

        int nr;

        public BeobTextFeld(int n) {
            nr = n;
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            tusEinfach(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            tusEinfach(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            tusEinfach(e);
        }

        void tusEinfach(DocumentEvent e) {
            Daten.system[nr] = jTextFieldPfad.getText();
            ddaten.setGeaendertPanel();
        }
    }

    private class BeobPfad implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int returnVal;
            JFileChooser chooser = new JFileChooser();
            if (!jTextFieldPfad.getText().equals("")) {
                chooser.setCurrentDirectory(new File(jTextFieldPfad.getText()));
            }
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setFileHidingEnabled(false);
            returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    jTextFieldPfad.setText(chooser.getSelectedFile().getAbsolutePath());
                } catch (Exception ex) {
                    Log.fehlerMeldung("PanelExportImportDateiUrl.BeobImport", ex);
                }
            }
        }
    }

    private class BeobImportExport implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (export) {
                filmeExportieren();
            } else {
                filmeImportieren();
            }
        }
    }
}