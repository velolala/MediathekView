/*    
 *    MediathekView
 *    Copyright (C) 2008   W. Xaver
 *    W.Xaver[at]googlemail.com
 *    http://zdfmediathk.sourceforge.net/
 *    
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mediathek.gui.dialog;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import mediathek.controller.Log;
import mediathek.daten.Daten;
import mediathek.file.GetFile;
import mediathek.res.GetIcon;
import mediathek.tool.CellRendererMediaDB;
import mediathek.tool.DirOpenAction;
import mediathek.tool.EscBeenden;
import mediathek.tool.FilenameUtils;
import mediathek.tool.Filter;
import mediathek.tool.GuiFunktionen;
import mediathek.tool.HinweisKeineAuswahl;
import mediathek.tool.ListenerMediathekView;
import mediathek.tool.MVConfig;
import mediathek.tool.MVMediaDB;
import mediathek.tool.MVMessageDialog;
import mediathek.tool.OpenPlayerAction;
import mediathek.tool.TModel;

public class DialogMediaDB extends javax.swing.JDialog {

    private final TModel modelFilm = MVMediaDB.getModel();
    private final JFrame parent;
    private boolean init = false;

    public DialogMediaDB(JFrame pparent) {
        super(pparent, false);
        initComponents();
        this.parent = pparent;
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                beenden();
            }
        });
        this.setTitle("Mediensammlung durchsuchen");
        ListenerMediathekView.addListener(new ListenerMediathekView(ListenerMediathekView.EREIGNIS_MEDIA_DB_START, DialogMediaDB.class.getSimpleName()) {
            @Override
            public void ping() {
                // neue DB suchen
                setIndex(false);
                jLabelSum.setText("0");
            }
        });
        ListenerMediathekView.addListener(new ListenerMediathekView(ListenerMediathekView.EREIGNIS_MEDIA_DB_STOP, DialogMediaDB.class.getSimpleName()) {
            @Override
            public void ping() {
                // neue DB liegt vor
                setIndex(true);
                jLabelSum.setText(Daten.mVMediaDB.getSizeFileArray() + "");
                search();
            }
        });
        final CellRendererMediaDB cellRenderer = new CellRendererMediaDB();
        jTableFilm.setDefaultRenderer(Object.class, cellRenderer);
        jTableFilm.setModel(modelFilm);
        jTableFilm.addMouseListener(new BeobMausTabelle());

        progress.setVisible(false);
        progress.setIndeterminate(true);
        progress.setMaximum(0);
        progress.setMinimum(0);
        progress.setValue(0);

        jTextFieldTitle.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Daten.mVMediaDB.searchFiles(modelFilm, jTextFieldTitle.getText());
            }
        });
        jTextFieldTitle.getDocument().addDocumentListener(new BeobDoc());

        jButtonIndex.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Daten.mVMediaDB.makeIndex();
            }
        });

        jButtonHelp.setIcon(GetIcon.getProgramIcon("help_16.png"));
        jButtonHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DialogHilfe(parent, true, new GetFile().getHilfeSuchen(GetFile.PFAD_HILFETEXT_DIALOG_MEDIA_DB)).setVisible(true);
            }
        });
        jButtonSearch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });
        jButtonBeenden.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                beenden();
            }
        });
        new EscBeenden(this) {
            @Override
            public void beenden_() {
                beenden();
            }
        };
        GuiFunktionen.setSize(MVConfig.SYSTEM_MEDIA_DB_DIALOG_GROESSE, this, parent);
    }

    @Override
    public void setVisible(boolean vis) {
        super.setVisible(vis);
        if (vis && Daten.mVConfig.get(MVConfig.SYSTEM_MEDIA_DB_PATH_MEDIA).isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Erst in den Einstellungen eine Mediensammlung einrichten.", "Mediensammlung leer!", JOptionPane.ERROR_MESSAGE);
        }
        if (!init) {
            // beim ersten anzeigen den Index bauen
            Daten.mVMediaDB.makeIndex();
            init = true;
        }
    }

    public final void setVis() {
        this.setVisible(Boolean.parseBoolean(Daten.mVConfig.get(MVConfig.SYSTEM_MEDIA_DB_DIALOG_ANZEIGEN)));
        ListenerMediathekView.notify(ListenerMediathekView.EREIGNIS_DIALOG_MEDIA_DB, DialogMediaDB.class.getName());
    }

    public void setFilter(String titel) {
        titel = FilenameUtils.replaceLeerDateiname(titel); // mit den eingestellten Ersetzungen bearbeiten
        jTextFieldTitle.setText(titel);
    }

    private void search() {
        Daten.mVMediaDB.searchFiles(modelFilm, jTextFieldTitle.getText());
        jLabelSizeFound.setText(modelFilm.getRowCount() + "");
    }

    private void setIndex(boolean noIndex) {
        progress.setVisible(!noIndex);
        jTextFieldTitle.setEnabled(noIndex);
        jButtonSearch.setEnabled(noIndex);
        jButtonIndex.setEnabled(noIndex);
    }

    private void zielordnerOeffnen() {
        int row = jTableFilm.getSelectedRow();
        if (row >= 0) {
            String s = (String) jTableFilm.getModel().getValueAt(jTableFilm.convertRowIndexToModel(row), MVMediaDB.MEDIA_DB_PATH_NR);
            DirOpenAction.zielordnerOeffnen(parent, s);
        } else {
            new HinweisKeineAuswahl().zeigen(parent);
        }
    }

    private void filmAbspielen_() {
        int row = jTableFilm.getSelectedRow();
        if (row >= 0) {
            String file = (String) jTableFilm.getModel().getValueAt(jTableFilm.convertRowIndexToModel(row), MVMediaDB.MEDIA_DB_NAME_NR);
            String path = (String) jTableFilm.getModel().getValueAt(jTableFilm.convertRowIndexToModel(row), MVMediaDB.MEDIA_DB_PATH_NR);
            OpenPlayerAction.filmAbspielen(parent, path + File.separator + file);
        } else {
            new HinweisKeineAuswahl().zeigen(parent);
        }
    }

    private void filmLoeschen_() {
        String del = "";
        int row = jTableFilm.getSelectedRow();
        if (row < 0) {
            new HinweisKeineAuswahl().zeigen(parent);
            return;
        }
        try {
            String file = (String) jTableFilm.getModel().getValueAt(jTableFilm.convertRowIndexToModel(row), MVMediaDB.MEDIA_DB_NAME_NR);
            String path = (String) jTableFilm.getModel().getValueAt(jTableFilm.convertRowIndexToModel(row), MVMediaDB.MEDIA_DB_PATH_NR);
            del = path + File.separator + file;
            File delFile = new File(del);
            if (!delFile.exists()) {
                MVMessageDialog.showMessageDialog(parent, "Die Datei existiert nicht!", "Film löschen", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int ret = JOptionPane.showConfirmDialog(parent, delFile.getAbsolutePath(), "Film Löschen?", JOptionPane.YES_NO_OPTION);
            if (ret != JOptionPane.OK_OPTION) {
                return;
            }

            // und jetzt die Datei löschen
            Log.systemMeldung(new String[]{"Datei löschen: ", delFile.getAbsolutePath()});
            if (!delFile.delete()) {
                throw new Exception();
            }
        } catch (Exception ex) {
            MVMessageDialog.showMessageDialog(parent, "Konnte die Datei nicht löschen!", "Film löschen", JOptionPane.ERROR_MESSAGE);
            Log.fehlerMeldung(984512036, "Fehler beim löschen: " + del);
        }
    }

    private void beenden() {
        Daten.mVConfig.add(MVConfig.SYSTEM_MEDIA_DB_DIALOG_ANZEIGEN, Boolean.FALSE.toString());
        setVis();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jButtonBeenden = new javax.swing.JButton();
        jButtonHelp = new javax.swing.JButton();
        jTextFieldTitle = new javax.swing.JTextField();
        jButtonSearch = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableFilm = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabelSizeFound = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButtonIndex = new javax.swing.JButton();
        jLabelSum = new javax.swing.JLabel();
        progress = new javax.swing.JProgressBar();

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jButtonBeenden.setText("Ok");

        jButtonHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mediathek/res/programm/help_16.png"))); // NOI18N

        jButtonSearch.setText("Suchen");

        jTableFilm.setAutoCreateRowSorter(true);
        jTableFilm.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(jTableFilm);

        jLabel1.setText("Treffer:");

        jLabelSizeFound.setText("0");

        jLabel4.setText("Anzahl Medien gesamt:");

        jButtonIndex.setText("Index neu aufbauen");

        jLabelSum.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextFieldTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonHelp))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonIndex)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonBeenden, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelSizeFound))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelSum)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonSearch))
                    .addComponent(jButtonHelp))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabelSum))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabelSizeFound))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonIndex)
                    .addComponent(jButtonBeenden))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonHelp, jButtonSearch, jTextFieldTitle});

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBeenden;
    private javax.swing.JButton jButtonHelp;
    private javax.swing.JButton jButtonIndex;
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelSizeFound;
    private javax.swing.JLabel jLabelSum;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableFilm;
    private javax.swing.JTextField jTextFieldTitle;
    private javax.swing.JProgressBar progress;
    // End of variables declaration//GEN-END:variables

    private class BeobDoc implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e
        ) {
            tus();
        }

        @Override
        public void removeUpdate(DocumentEvent e
        ) {
            tus();
        }

        @Override
        public void changedUpdate(DocumentEvent e
        ) {
            tus();
        }

        private void tus() {
            Filter.checkPattern1(jTextFieldTitle);
            if (Boolean.parseBoolean(Daten.mVConfig.get(MVConfig.SYSTEM_MEDIA_DB_ECHTZEITSUCHE))) {
                search();
            }
        }
    }

    public class BeobMausTabelle extends MouseAdapter {

        private Point p;

        @Override
        public void mousePressed(MouseEvent arg0) {
            if (arg0.isPopupTrigger()) {
                showMenu(arg0);
            }
        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
            if (arg0.isPopupTrigger()) {
                showMenu(arg0);
            }
        }

        private void showMenu(MouseEvent evt) {
            p = evt.getPoint();
            int nr = jTableFilm.rowAtPoint(p);
            if (nr >= 0) {
                jTableFilm.setRowSelectionInterval(nr, nr);
            }
            JPopupMenu jPopupMenu = new JPopupMenu();

            // Film abspielen
            JMenuItem itemPlayerDownload = new JMenuItem("gespeicherten Film (Datei) abspielen");
            itemPlayerDownload.setIcon(GetIcon.getProgramIcon("film_start_16.png"));
            itemPlayerDownload.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    filmAbspielen_();
                }
            });
            jPopupMenu.add(itemPlayerDownload);

            // Film löschen
            JMenuItem itemDeleteDownload = new JMenuItem("gespeicherten Film (Datei) löschen");
            itemDeleteDownload.setIcon(GetIcon.getProgramIcon("film_del_16.png"));
            itemDeleteDownload.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    filmLoeschen_();
                }
            });
            jPopupMenu.add(itemDeleteDownload);

            // Zielordner öffnen
            JMenuItem itemOeffnen = new JMenuItem("Zielordner öffnen");
            itemOeffnen.setIcon(GetIcon.getProgramIcon("fileopen_16.png"));
            jPopupMenu.add(itemOeffnen);
            itemOeffnen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    zielordnerOeffnen();
                }
            });

            // ######################
            // Menü anzeigen
            jPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }

    }

}
