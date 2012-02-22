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
package mediathek.controller.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.zip.ZipOutputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import mediathek.Daten;
import mediathek.Konstanten;
import mediathek.Log;
import mediathek.controller.filme.filmeImportieren.filmUpdateServer.DatenFilmUpdateServer;
import mediathek.controller.filme.filmeImportieren.filmUpdateServer.FilmUpdateServer;
import mediathek.daten.*;
import mediathek.importOld.DatenPgruppe__old;
import mediathek.importOld.Konstanten__old;
import mediathek.tool.GuiFunktionen;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

public class IoXmlLesen {

    ZipOutputStream zipOutputStream = null;
    BZip2CompressorOutputStream bZip2CompressorOutputStream = null;

    public void datenLesen(DDaten daten) {
        xmlDatenLesen(daten);
    }

    public static boolean einstellungenExistieren() {
        try {
            String datei;
            datei = Daten.getBasisVerzeichnis(false) + Konstanten.XML_DATEI;
            if (new File(datei).exists()) {
                return true;
            }
        } catch (Exception ex) {
        }
        return false;
    }

    public static DatenPgruppe[] importPgruppe(String datei, boolean log) {
        DatenPgruppe datenPgruppe = null;
        LinkedList<DatenPgruppe> liste = new LinkedList<DatenPgruppe>();
        try {
            int event;
            XMLInputFactory inFactory = XMLInputFactory.newInstance();
            inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
            XMLStreamReader parser;
            InputStreamReader in;
            in = new InputStreamReader(new FileInputStream(datei), Konstanten.KODIERUNG_UTF);
            parser = inFactory.createXMLStreamReader(in);
            while (parser.hasNext()) {
                event = parser.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    //String t = parser.getLocalName();
                    if (parser.getLocalName().equals(DatenPgruppe.PROGRAMMGRUPPE)) {
                        datenPgruppe = new DatenPgruppe();
                        if (!get(parser, event, DatenPgruppe.PROGRAMMGRUPPE, DatenPgruppe.PROGRAMMGRUPPE_COLUMN_NAMES, datenPgruppe.arr, false)) {
                            datenPgruppe = null;
                        } else {
                            liste.add(datenPgruppe);
                        }
                    } else if (parser.getLocalName().equals(Konstanten__old.PROGRAMMGRUPPE_BUTTON)) {
                        DatenPgruppe__old datenPgruppe__old = new DatenPgruppe__old();
                        if (!get(parser, event, Konstanten__old.PROGRAMMGRUPPE_BUTTON, Konstanten__old.PROGRAMMGRUPPE_COLUMN_NAMES, datenPgruppe__old.arr, false)) {
                            datenPgruppe = null;
                        } else {
                            datenPgruppe = datenPgruppe__old.getNewVersion();
                            liste.add(datenPgruppe);
                        }
                    } else if (parser.getLocalName().equals(DatenProg.PROGRAMM)) {
                        if (datenPgruppe != null) {
                            DatenProg datenProg = new DatenProg();
                            if (get(parser, event, DatenProg.PROGRAMM, DatenProg.PROGRAMM_COLUMN_NAMES, datenProg.arr, false)) {
                                datenPgruppe.addProg(datenProg);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            if (log) {
                Log.fehlerMeldung("IoXml.importPgruppe", ex);
            }
            return null;
        }
        if (liste.size() == 0) {
            return null;
        } else {
            return liste.toArray(new DatenPgruppe[0]);
        }
    }

    public static DatenPgruppe[] importPgruppeText(String text, boolean log) {
        DatenPgruppe datenPgruppe = null;
        LinkedList<DatenPgruppe> liste = new LinkedList<DatenPgruppe>();
        try {
            int event;
            XMLInputFactory inFactory = XMLInputFactory.newInstance();
            inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
            XMLStreamReader parser;
            InputStreamReader in = new InputStreamReader(new ByteArrayInputStream(text.getBytes()));
            parser = inFactory.createXMLStreamReader(in);
            while (parser.hasNext()) {
                event = parser.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    //String t = parser.getLocalName();
                    if (parser.getLocalName().equals(DatenPgruppe.PROGRAMMGRUPPE)) {
                        datenPgruppe = new DatenPgruppe();
                        if (!get(parser, event, DatenPgruppe.PROGRAMMGRUPPE, DatenPgruppe.PROGRAMMGRUPPE_COLUMN_NAMES, datenPgruppe.arr, false)) {
                            datenPgruppe = null;
                        } else {
                            liste.add(datenPgruppe);
                        }
                    } else if (parser.getLocalName().equals(Konstanten__old.PROGRAMMGRUPPE_BUTTON)) {
                        DatenPgruppe__old datenPgruppe__old = new DatenPgruppe__old();
                        if (!get(parser, event, Konstanten__old.PROGRAMMGRUPPE_BUTTON, Konstanten__old.PROGRAMMGRUPPE_COLUMN_NAMES, datenPgruppe__old.arr, false)) {
                            datenPgruppe = null;
                        } else {
                            datenPgruppe = datenPgruppe__old.getNewVersion();
                            liste.add(datenPgruppe);
                        }
                    } else if (parser.getLocalName().equals(DatenProg.PROGRAMM)) {
                        if (datenPgruppe != null) {
                            DatenProg datenProg = new DatenProg();
                            if (get(parser, event, DatenProg.PROGRAMM, DatenProg.PROGRAMM_COLUMN_NAMES, datenProg.arr, false)) {
                                datenPgruppe.addProg(datenProg);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            if (log) {
                Log.fehlerMeldung("IoXml.importPgruppe", ex);
            }
            return null;
        }
        if (liste.size() == 0) {
            return null;
        } else {
            return liste.toArray(new DatenPgruppe[0]);
        }
    }

    // ##############################
    // private
    // ##############################
    private void xmlDatenLesen(DDaten ddaten) {
        try {
            String datei;
            datei = Daten.getBasisVerzeichnis(false) + Konstanten.XML_DATEI;
            if (new File(datei).exists()) {
                //nur wenn die Datei schon existiert
                int event;
                XMLInputFactory inFactory = XMLInputFactory.newInstance();
                inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
                XMLStreamReader parser;
                InputStreamReader in;
                DatenPgruppe datenPgruppe = null;
                in = new InputStreamReader(new FileInputStream(datei), Konstanten.KODIERUNG_UTF);
                parser = inFactory.createXMLStreamReader(in);
                while (parser.hasNext()) {
                    event = parser.next();
                    if (event == XMLStreamConstants.START_ELEMENT) {
                        //String t = parser.getLocalName();
                        if (parser.getLocalName().equals(Konstanten.SYSTEM)) {
                            //System
                            get(parser, event, Konstanten.SYSTEM, Konstanten.SYSTEM_COLUMN_NAMES, DDaten.system);
                        } else if (parser.getLocalName().equals(DatenPgruppe.PROGRAMMGRUPPE)) {
                            //Programmgruppen
                            datenPgruppe = new DatenPgruppe();
                            if (get(parser, event, DatenPgruppe.PROGRAMMGRUPPE, DatenPgruppe.PROGRAMMGRUPPE_COLUMN_NAMES, datenPgruppe.arr)) {
                                ddaten.listePgruppe.add(datenPgruppe);
                            }
                        } else if (parser.getLocalName().equals(DatenProg.PROGRAMM)) {
                            DatenProg datenProg = new DatenProg();
                            if (get(parser, event, DatenProg.PROGRAMM, DatenProg.PROGRAMM_COLUMN_NAMES, datenProg.arr)) {
                                datenPgruppe.addProg(datenProg);
                            }
                            //ende Programgruppen
                        } else if (parser.getLocalName().equals(DatenAbo.ABO)) {
                            //Abo
                            DatenAbo datenAbo = new DatenAbo();
                            if (get(parser, event, DatenAbo.ABO, DatenAbo.ABO_COLUMN_NAMES, datenAbo.arr)) {
                                ddaten.listeAbo.addAbo(datenAbo);
                            }
                        } else if (parser.getLocalName().equals(DatenDownload.DOWNLOAD)) {
                            //Downloads
                            DatenDownload d = new DatenDownload();
                            if (get(parser, event, DatenDownload.DOWNLOAD, DatenDownload.DOWNLOAD_COLUMN_NAMES, d.arr)) {
                                ddaten.listeDownloads.add(d);
                            }
                        } else if (parser.getLocalName().equals(DatenBlacklist.BLACKLIST)) {
                            //Blacklist
                            ListeBlacklist blacklist = ddaten.listeBlacklist;
                            DatenBlacklist datenBlacklist = new DatenBlacklist();
                            if (get(parser, event, DatenBlacklist.BLACKLIST, DatenBlacklist.BLACKLIST_COLUMN_NAMES, datenBlacklist.arr)) {
                                blacklist.add(datenBlacklist);
                            }
                        } else if (parser.getLocalName().equals(FilmUpdateServer.FILM_UPDATE_SERVER)) {
                            //Filmliste update
                            DatenFilmUpdateServer datenFilmUpdateServer = new DatenFilmUpdateServer();
                            if (get(parser, event, FilmUpdateServer.FILM_UPDATE_SERVER, FilmUpdateServer.FILM_UPDATE_SERVER_COLUMN_NAMES, datenFilmUpdateServer.arr)) {
                                DDaten.filmeLaden.getListeFilmUpdateServer(false).addWithCheck(datenFilmUpdateServer);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.fehlerMeldung("IoXml.xmlDatenLesen", ex);
        } finally {
            //ListeFilmUpdateServer aufbauen
            DDaten.filmeLaden.getListeFilmUpdateServer(false).sort();
        }
    }

    private static boolean get(XMLStreamReader parser, int event, String xmlElem, String[] xmlNames, String[] strRet) {
        return get(parser, event, xmlElem, xmlNames, strRet, true);
    }

    private static boolean get(XMLStreamReader parser, int event, String xmlElem, String[] xmlNames, String[] strRet, boolean log) {
        boolean ret = true;
        int maxElem = strRet.length;
        for (int i = 0; i < maxElem; ++i) {
            strRet[i] = "";
        }
        try {
            while (parser.hasNext()) {
                event = parser.next();
                if (event == XMLStreamConstants.END_ELEMENT) {
                    if (parser.getLocalName().equals(xmlElem)) {
                        break;
                    }
                }
                if (event == XMLStreamConstants.START_ELEMENT) {
                    for (int i = 0; i < maxElem; ++i) {
                        if (parser.getLocalName().equals(xmlNames[i])) {
                            strRet[i] = parser.getElementText();
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ret = false;
            if (log) {
                Log.fehlerMeldung("IoXmlLesen.get", ex);
            }
        }
        return ret;
    }
}