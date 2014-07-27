package mediathek;

import com.explodingpixels.macwidgets.HudWindow;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import mediathek.controller.starter.Start;
import mediathek.daten.Daten;
import mediathek.daten.DatenDownload;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.TimerTask;

/**
 * This class will manage and display the download bandwidth chart display.
 */
class MVBandwidthMonitor {
    private double counter = 0.0;
    private HudWindow hudWindow = null;
    private JCheckBoxMenuItem menuItem = null;
    private ITrace2D m_trace = new Trace2DLtd(300);

    /**
     * Timer for collecting sample data.
     */
    private java.util.Timer timer = new java.util.Timer(false);

    public MVBandwidthMonitor(JFrame parent, final JCheckBoxMenuItem menuItem) {
        this.menuItem = menuItem;
        hudWindow = new HudWindow("Bandbreite", parent);

        JDialog hudDialog = hudWindow.getJDialog();
        hudDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        hudDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                menuItem.setSelected(false);
            }
        });

        //setup chart display
        Chart2D chart = new Chart2D();
        chart.setOpaque(false);
        chart.setPaintLabels(false);
        chart.setUseAntialiasing(true);

        //setup trace point handling
        m_trace.setColor(Color.GREEN);
        m_trace.setName("KB/s");

        chart.addTrace(m_trace);

        IAxis x_achse = chart.getAxisX();
        x_achse.getAxisTitle().setTitle("");
        x_achse.setPaintScale(false);
        x_achse.setVisible(false);

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout(0, 0));
        panel.add(chart, BorderLayout.CENTER);
        hudWindow.setContentPane(panel);

        chart.removeAxisYLeft(chart.getAxisY());

        final Dimension dim = hudDialog.getSize();
        dim.height = 120;
        dim.width = 240;
        hudDialog.setSize(dim);
    }

    /**
     * Show/hide bandwidth display. Take also care about the used timer.
     */
    public void toggleVisibility() {
        final boolean isSelected = menuItem.isSelected();
        hudWindow.getJDialog().setVisible(isSelected);
        try {
            if (menuItem.isSelected()) {
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        double bandwidth = 0.0;
                        //only count running/active downloads and calc accumulated progress..
                        LinkedList<DatenDownload> activeDownloadList = Daten.listeDownloads.getListOfStartsNotFinished(Start.QUELLE_ALLE);
                        for (DatenDownload download : activeDownloadList) {
                            if (download.start != null && download.start.status == Start.STATUS_RUN) {
                                bandwidth += download.start.bandbreite;
                            }
                        }
                        activeDownloadList.clear();

                        if (bandwidth < 0.0)
                            bandwidth = 0.0;

                        if (bandwidth > 0.0)
                            bandwidth /= 1024.0; // convert to KByte

                        counter++;
                        m_trace.addPoint(counter, bandwidth);
                    }
                };
                timer.schedule(task, 0, 1000);
            } else {
                timer.cancel();
                timer.purge();
            }
        } catch (IllegalStateException ignored) {
        }
    }
}