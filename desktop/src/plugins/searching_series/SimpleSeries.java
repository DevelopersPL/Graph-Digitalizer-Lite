/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plugins.searching_series;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import marvin.gui.MarvinAttributesPanel;
import marvin.image.MarvinImage;
import marvin.image.MarvinImageMask;
import marvin.plugin.MarvinAbstractImagePlugin;
import marvin.util.MarvinAttributes;

/**
 *
 * @author MaciekG
 */
public class SimpleSeries extends MarvinAbstractImagePlugin {
    public List pointList;
    @Override
    public void load() {
    }

    @Override
    public MarvinAttributesPanel getAttributesPanel() {
        return null;
    }

    /**
     * Ta funkcja przetwarza zdjęcie jej kod należy zmieniać
     *
     * @param imageIn
     * @param imageOut
     * @param attributesOut
     * @param mask
     * @param previewMode
     */
    @Override
    public void process(
            MarvinImage imageIn,
            MarvinImage imageOut,
            MarvinAttributes attributesOut,
            MarvinImageMask mask,
            boolean previewMode) {
        
        Color color = (Color) getAttribute("color");
        int r, g, b, Sx, Sy;
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();
        
        int dr, dg, db;
        dr = r/5;
        dg = g/5;
        db = b/5;
        if(dr==0){dr = (db+dg)/2;};
        if(dg==0){dg = (dr+db/2);};
        if(db==0){db = (dr+dg)/2;};
        
         System.out.print("|");
         pointList = new ArrayList();
        
        for (int x = 0; x < imageIn.getWidth(); x++) {
            for (int y = 0; y < imageIn.getHeight(); y++) {
                if(
                (imageIn.getIntComponent0(x, y)> r-dr && imageIn.getIntComponent0(x, y)< r+dr) && 
                (imageIn.getIntComponent1(x, y)> g-dg && imageIn.getIntComponent1(x, y)< g+dg) &&
                (imageIn.getIntComponent2(x, y)> b-db && imageIn.getIntComponent2(x, y)< b+db)
                ){
                   
                    pointList.add(new Point(x,y));
                    imageOut.setIntColor(x, y, 255,0,0);
                    
                }

                
            }
        }
    }
    
    @Override
    public String toString() {
        return "Odwrócenie kolorów";
    }
}
