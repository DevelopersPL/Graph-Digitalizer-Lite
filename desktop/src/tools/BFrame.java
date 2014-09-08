/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import CSV.CSVWriter;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import marvin.gui.MarvinImagePanel;
import marvin.image.MarvinImage;
import marvin.image.MarvinImageMask;
import marvin.io.MarvinImageIO;
import marvin.plugin.MarvinImagePlugin;
import marvin.util.MarvinPluginHistory;
import plugins.HistogramStretching;
import plugins.searching_series.SimpleSeries;

/**
 *
 * @author Comarch
 */
public class BFrame extends javax.swing.JFrame {

    // GUI
    protected JButton buttonShowHistory;
    protected JButton buttonApply;
    private boolean simpleSeries = false;
    private boolean selectGraphMask = false;
    private boolean selectLegendMask = false;
    // Marvin Objects
    protected MarvinPluginHistory history;
    protected MarvinImagePlugin tempPlugin;
    protected MarvinImage originalImage, zoomImage;
    protected MarvinImage resultImage;
    protected MarvinImagePanel imagePanelOriginal,
            imagePanelNew, imagePanelZoom;
    protected DefaultListModel listModel;
    protected java.util.List PointList;
    // Mask
    protected MarvinImageMask graphMask;
    protected Point[] graphMaskCoords;
    protected Point[] legendMaskCoords;
    protected boolean[][] graphMaskArray;
    private boolean Xaxis;
    private boolean Yaxis;
    private Point Xpoint1;
    private Point Xpoint2;
    private Point Ypoint1;
    private Point Ypoint2;
    private double DistX;
    private double DistY;
    private int Xvalue;
    private int Yvalue;

    public BFrame() {
        initComponents();
        Xaxis = false;
        Yaxis = false;
        Xpoint1 = null;
        Xpoint2 = null;
        Ypoint1 = null;
        Ypoint2 = null;

    }

    public BFrame(String filename) {
        initComponents();
        imagePanelOriginal = new MarvinImagePanel();
        originalImage = MarvinImageIO.loadImage(filename);
        originalImage.resize(jImagePanel.getWidth(), jImagePanel.getHeight());
        imagePanelOriginal.setSize(jImagePanel.getWidth(), jImagePanel.getHeight());
        imagePanelOriginal.setImage(originalImage);

        JSplitPane sp1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, jDataPanel, jImagePanel);
        sp1.setOneTouchExpandable(true);
        sp1.setDividerLocation(150);
        add(sp1);

        jImagePanel.add(imagePanelOriginal);

        imagePanelZoom = new MarvinImagePanel();
        imagePanelZoom.setSize(jZoomPanel.getWidth(), jZoomPanel.getHeight());
        jZoomPanel.add(imagePanelZoom);
        //zoomImage = originalImage.clone();
        //zoomImage.setDimension(jZoomPanel.getWidth(), jZoomPanel.getHeight());

        imagePanelOriginal.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (null != e.getPropertyName()) {
                    switch (e.getPropertyName()) {
                        case "mouseX":
                        case "mouseY":
                            int x = 0,
                             y = 0;

                            if (imagePanelOriginal.getMousePosition() != null) {

                                if (imagePanelOriginal.getMousePosition().x <= (jZoomPanel.getWidth() / 2)) {
                                    x = 1;
                                } else if (imagePanelOriginal.getMousePosition().x >= (imagePanelOriginal.getWidth() - 5 - (jZoomPanel.getWidth() / 2))) {
                                    x = imagePanelOriginal.getWidth() - 5 - (jZoomPanel.getWidth() / 2) - 30;
                                } else {
                                    x = imagePanelOriginal.getMousePosition().x - (jZoomPanel.getWidth() / 2);
                                }

                                if (imagePanelOriginal.getMousePosition().y <= (jZoomPanel.getHeight() / 2)) {
                                    y = 1;
                                } else if (imagePanelOriginal.getMousePosition().y >= imagePanelOriginal.getHeight() - (jZoomPanel.getHeight() / 2)) {
                                    y = imagePanelOriginal.getHeight() - (jZoomPanel.getHeight() / 2) - 25;
                                } else {
                                    y = imagePanelOriginal.getMousePosition().y - (jZoomPanel.getHeight() / 2);
                                }

                                //System.out.println("x: " + x + "    y: " + y );
                                //System.out.println("width: " + (imagePanelOriginal.getImage().getWidth()-(jZoomPanel.getWidth()/2)));
                                zoomImage = imagePanelOriginal.getImage().clone().crop(x + 35, y + 35, jZoomPanel.getWidth() - 70, jZoomPanel.getHeight() - 70);
                                zoomImage.drawLine((zoomImage.getWidth() / 2), (zoomImage.getHeight() / 2) - 2, (zoomImage.getWidth() / 2), (zoomImage.getHeight() / 2) + 2, Color.red);
                                zoomImage.drawLine((zoomImage.getWidth() / 2) - 2, (zoomImage.getHeight() / 2), (zoomImage.getWidth() / 2) + 2, (zoomImage.getHeight() / 2), Color.red);
                                imagePanelZoom.setImage(zoomImage);
                                imagePanelZoom.update();

                            }
                            break;
                        case "clicked":
                            if (simpleSeries) {
                                Point p = new Point((Point) e.getNewValue());
                                resultImage = originalImage.clone();
                                SimpleSeries tmpPlugin = new SimpleSeries();
                                //tempPlugin = new SimpleSeries();
                                int r, g, b;
                                r = originalImage.getIntComponent0(p.x, p.y);
                                g = originalImage.getIntComponent1(p.x, p.y);
                                b = originalImage.getIntComponent2(p.x, p.y);
                                tmpPlugin.setAttribute("color", new Color(r, g, b));

                                String s = txtSampling.getText();
                                if (s.equals("")) {
                                    s = "0";
                                }

                                tmpPlugin.setAttribute("sample", Integer.parseInt(s));
                                // przetworzenie zdjęcia
                                tmpPlugin.process(resultImage, resultImage, graphMask);
                                PointList = recalculateAxis(tmpPlugin.pointList, Xpoint1, DistY, DistX, Xvalue, Yvalue);
                                listModel = new DefaultListModel();
                                for (int i = 0; i < PointList.size(); i++) {
                                    Point tmp = (Point) PointList.get(i);
                                    String txt = "X: " + tmp.x + " Y: " + tmp.y;
                                    listModel.addElement(txt);
                                    //System.out.print("a");
                                }
                                jPointList.setModel(listModel);
                                //jPointList = new JList(listModel);
                                //zaktualizowanie obrazka
                                resultImage.update();

                                imagePanelOriginal.setImage(resultImage);
                                simpleSeries = false;
                            } else if (Xaxis) {
                                if (Xpoint1 == null) {
                                    Xpoint1 = (Point) e.getNewValue();
                                } else {
                                    Xpoint2 = (Point) e.getNewValue();
                                    DistX = Math.sqrt(Math.pow((Xpoint2.x - Xpoint1.x), 2) + Math.pow((Xpoint2.y - Xpoint1.y), 2));
                                    Xaxis = false;
                                    Xvalue = Integer.parseInt(JOptionPane.showInputDialog("Podaj wartość przedziałki X:"));
                                }
                            } else if (Yaxis) {
                                if (Ypoint1 == null) {
                                    Ypoint1 = (Point) e.getNewValue();
                                } else {
                                    Ypoint2 = (Point) e.getNewValue();
                                    DistY = Math.sqrt(Math.pow((Ypoint2.x - Ypoint1.x), 2) + Math.pow((Ypoint2.y - Ypoint1.y), 2));
                                    Yaxis = false;
                                    Yvalue = Integer.parseInt(JOptionPane.showInputDialog("Podaj wartość przedziałki Y:"));
                                }
                            }
                            break;
                        case "mask":
                            if (selectGraphMask) {
                                selectGraphMask = false;
                                jButton2.setEnabled(true);

                                graphMaskCoords = (Point[]) e.getNewValue();

                                int w = (int) graphMaskCoords[1].getX() - (int) graphMaskCoords[0].getX();
                                int h = (int) graphMaskCoords[1].getY() - (int) graphMaskCoords[0].getY();
                                graphMask = new MarvinImageMask(originalImage.getWidth(), originalImage.getHeight(), (int) graphMaskCoords[0].getX(), (int) graphMaskCoords[0].getY(), w, h);

                            } else if (selectLegendMask) {
                                selectLegendMask = false;
                                jButton7.setEnabled(true);

                                legendMaskCoords = (Point[]) e.getNewValue();
                                graphMaskArray = graphMask.getMaskArray();

                                for (int xM = (int) legendMaskCoords[0].getX(); xM <= (int) legendMaskCoords[1].getX(); xM++) {
                                    for (int yM = (int) legendMaskCoords[0].getY(); yM <= (int) legendMaskCoords[1].getY(); yM++) {
                                        if (graphMaskArray[xM][yM]) {
                                            graphMask.removePoint(xM, yM);
                                        }
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        });


        /*  Ukrycie komponentow (na pozniej kiedy nie bedziemy na poczatku wczytywac zdjecia odrazu)
         int a = jMenuButtons.getComponentCount();
         for (int i=0;i<a;i++)
         jMenuButtons.getComponent(i).setEnabled(false);
         */
        setVisible(true);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private java.util.List recalculateAxis(java.util.List inPoints, Point Xpoint1, double DistY, double DistX, int Xvalue, int Yvalue) {

        for (int i = 0; i < inPoints.size(); i++) {
            Point tmp = (Point) inPoints.get(i);
            
            tmp.x -= (int) Xpoint1.getX();
            tmp.x *= (int) Xvalue;
            tmp.x /= (int) DistX;
            
            tmp.y -= (int) Xpoint1.getY(); 
            tmp.y *= (int) Yvalue;
            tmp.y /= (int) DistY;
            tmp.y *= -1;
            
        }

        return inPoints;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jMenuButtons = new javax.swing.JPanel();
        OpenFile = new javax.swing.JButton();
        SaveFile = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jZoomPanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        txtSampling = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jDataPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPointList = new javax.swing.JList();
        jCSVExport = new javax.swing.JButton();
        jImagePanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        MOpenFile = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        MSaveFile = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        jMenuItem1.setText("jMenuItem1");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jMenuButtons.setPreferredSize(new java.awt.Dimension(781, 105));

        OpenFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/folder.png"))); // NOI18N
        OpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenFileActionPerformed(evt);
            }
        });

        SaveFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save.png"))); // NOI18N
        SaveFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveFileActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/write.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search.png"))); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Oś X");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Oś Y");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout jZoomPanelLayout = new javax.swing.GroupLayout(jZoomPanel);
        jZoomPanel.setLayout(jZoomPanelLayout);
        jZoomPanelLayout.setHorizontalGroup(
            jZoomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 111, Short.MAX_VALUE)
        );
        jZoomPanelLayout.setVerticalGroup(
            jZoomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jButton1.setText("Zaznacz Tytuł");

        jLabel1.setText("Próbkowanie:");

        jButton2.setText("Zaznacz wykres");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton7.setText("Zaznacz legendę");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jMenuButtonsLayout = new javax.swing.GroupLayout(jMenuButtons);
        jMenuButtons.setLayout(jMenuButtonsLayout);
        jMenuButtonsLayout.setHorizontalGroup(
            jMenuButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jMenuButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jMenuButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jMenuButtonsLayout.createSequentialGroup()
                        .addComponent(OpenFile, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SaveFile, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSampling, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jMenuButtonsLayout.createSequentialGroup()
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7)))
                .addGap(30, 163, Short.MAX_VALUE)
                .addComponent(jZoomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        jMenuButtonsLayout.setVerticalGroup(
            jMenuButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jMenuButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jMenuButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jZoomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jMenuButtonsLayout.createSequentialGroup()
                        .addGroup(jMenuButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jMenuButtonsLayout.createSequentialGroup()
                                .addGroup(jMenuButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jMenuButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jMenuButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(OpenFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jButton4, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(SaveFile))
                                        .addGroup(jMenuButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(txtSampling, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel1)))
                                    .addComponent(jSeparator1))
                                .addGap(12, 12, 12))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jMenuButtonsLayout.createSequentialGroup()
                                .addComponent(jButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                        .addGroup(jMenuButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton5)
                            .addComponent(jButton6)
                            .addComponent(jButton1)
                            .addComponent(jButton2)
                            .addComponent(jButton7))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(jMenuButtons, java.awt.BorderLayout.PAGE_START);

        jDataPanel.setBackground(new java.awt.Color(204, 204, 204));
        jDataPanel.setPreferredSize(new java.awt.Dimension(150, 294));

        jScrollPane2.setViewportView(jPointList);

        jCSVExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/export.png"))); // NOI18N
        jCSVExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCSVExportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDataPanelLayout = new javax.swing.GroupLayout(jDataPanel);
        jDataPanel.setLayout(jDataPanelLayout);
        jDataPanelLayout.setHorizontalGroup(
            jDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
            .addComponent(jCSVExport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDataPanelLayout.setVerticalGroup(
            jDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDataPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCSVExport, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getContentPane().add(jDataPanel, java.awt.BorderLayout.LINE_START);

        javax.swing.GroupLayout jImagePanelLayout = new javax.swing.GroupLayout(jImagePanel);
        jImagePanel.setLayout(jImagePanelLayout);
        jImagePanelLayout.setHorizontalGroup(
            jImagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 631, Short.MAX_VALUE)
        );
        jImagePanelLayout.setVerticalGroup(
            jImagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 427, Short.MAX_VALUE)
        );

        getContentPane().add(jImagePanel, java.awt.BorderLayout.CENTER);

        MOpenFile.setText("Plik");

        jMenuItem2.setText("Otwórz");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        MOpenFile.add(jMenuItem2);

        MSaveFile.setText("Zapisz");
        MSaveFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MSaveFileActionPerformed(evt);
            }
        });
        MOpenFile.add(MSaveFile);

        jMenuBar1.add(MOpenFile);

        jMenu2.setText("Pomoc");
        jMenu2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu2MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        open();        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void OpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenFileActionPerformed
        open();        // TODO add your handling code here:
    }//GEN-LAST:event_OpenFileActionPerformed

    private void MSaveFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MSaveFileActionPerformed
        save();        // TODO add your handling code here:
    }//GEN-LAST:event_MSaveFileActionPerformed

    private void SaveFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveFileActionPerformed
        save();        // TODO add your handling code here:
    }//GEN-LAST:event_SaveFileActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
//        preProcessing();
        new ImgModifyForm(this).setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        simpleSeries = true;
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jCSVExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCSVExportActionPerformed
        FileDialog fd = new FileDialog(this, "Zapisz", FileDialog.SAVE);
        fd.setVisible(true);
        String katalog = fd.getDirectory();

        String plik = fd.getFile();
        CSVWriter writer;
        try {
            writer = new CSVWriter(new FileWriter(katalog + plik));

            for (int i = 0; i < PointList.size(); i++) {
                Point tmp = (Point) PointList.get(i);
                String[] val = {"x:" + tmp.x + "y:" + tmp.y};
                writer.writeNext(val);
            }
            writer.close();

        } catch (IOException ex) {
            Logger.getLogger(BFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jCSVExportActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        selectGraphMask = true;
        jButton2.setEnabled(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        selectLegendMask = true;
        jButton7.setEnabled(false);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        Xaxis = true;
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        Yaxis = true;
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jMenu2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu2MouseClicked
        
        JOptionPane.showMessageDialog(this,
            "1.: Naciśnij przycisk OŚ X \n"
                    + "1a.: Naciśnij początek układu współrzędnych\n"
                    + "1b.: Naciśnij pierwszy punkt przedziału osi X\n"
                    + "2.: Naciśnij przycisk OS Y\n"
                    + "2a.: Naciśnij początek układu współrzędnych\n"
                    + "2b.: Naciśnij pierwszy punkt przedziału osi Y\n"
                    + "3.: Naciśnij przycisk Zaznacz wykres i zaznacz prostokątem obszar wykresu\n"
                    + "4.: Naciśnij przycisk Zaznacz legendę i zaznacz prostokątem obszar legendy\n"
                    + "5.: Naciśnij przycisk ołówka i zaznacz serię\n"
                    + "6.: Zapisz serię do pliku CSV klikając pod listą danych", "Pomoc", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenu2MouseClicked

    public void preProcessing() {
        resultImage = originalImage.clone();
        tempPlugin = new HistogramStretching();
        tempPlugin.setAttribute("hood", 15);
        tempPlugin.setAttribute("sigma", 15);
        tempPlugin.process(resultImage, resultImage);
        resultImage.update();

        imagePanelOriginal.setImage(resultImage);
    }

    public void processPlugins(MarvinImagePlugin[] plugins) {
        resultImage = originalImage.clone();

        for (MarvinImagePlugin plugin : plugins) {
            plugin.process(resultImage, resultImage);
        }
        resultImage.update();

        imagePanelOriginal.setImage(resultImage);
    }

    /**
     * @param args the command line arguments
     */
    public void open() {
        //System.out.print("dziala");
        FileDialog fd = new FileDialog(this, "Wczytaj", FileDialog.LOAD);
        // Ewentualnie: FileDialog fd =new FileDialog(a,"Zapisz",FileDialog.SAVE);
        fd.setVisible(true);
        String katalog = fd.getDirectory();
        String plik = fd.getFile();
        System.out.println("Ścieżka: " + katalog + plik);

        if (katalog != null && plik != null) {
            originalImage = MarvinImageIO.loadImage(katalog + plik);
            imagePanelOriginal.setImage(originalImage);
        }
    }

    public void save() {
        if (originalImage != null) {
            FileDialog fd = new FileDialog(this, "Zapisz", FileDialog.SAVE);

            fd.setVisible(true);
            String katalog = fd.getDirectory();
            String plik = fd.getFile();

            System.out.println("Ścieżka: " + katalog + plik);
            String filename = katalog + plik;
            MarvinImageIO.saveImage(originalImage, filename);

        }

    }

    //protected abstract void process();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu MOpenFile;
    private javax.swing.JMenuItem MSaveFile;
    private javax.swing.JButton OpenFile;
    private javax.swing.JButton SaveFile;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jCSVExport;
    private javax.swing.JPanel jDataPanel;
    private javax.swing.JPanel jImagePanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jMenuButtons;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JList jPointList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel jZoomPanel;
    private javax.swing.JTextField txtSampling;
    // End of variables declaration//GEN-END:variables

}
