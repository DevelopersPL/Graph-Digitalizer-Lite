/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plugins;

import marvin.gui.MarvinAttributesPanel;
import marvin.image.MarvinImage;
import marvin.image.MarvinImageMask;
import marvin.plugin.MarvinAbstractImagePlugin;
import marvin.util.MarvinAttributes;
import plugins.utils.BoundGetter;
import plugins.utils.MirrorGetter;

/**
 *
 * @author Marek
 */
public class Convolution extends MarvinAbstractImagePlugin {
    private final String name;

    public Convolution() {
        name = "Convolution";
    }
    
    public Convolution(String name) {
        this.name = name;
    }

    @Override
    public void load() {
    }

    @Override
    public MarvinAttributesPanel getAttributesPanel() {
        return null;
    }

    @Override
    public void process(MarvinImage mi, MarvinImage mi1, MarvinAttributes ma, MarvinImageMask mim, boolean bln) {
        int[] input = mi.getIntColorArray(), output = mi.getIntColorArray();
        int cWidth = mi.getWidth(), cHeight = mi.getHeight();
        double[] filter = (double[]) getAttribute("filter");
        Integer width = (Integer) getAttribute("width"),
                height = filter.length/width;
        boolean[][] mask = mim.getMaskArray();

        BoundGetter bg = new MirrorGetter(input, cWidth, mim);
        
        int xTranslation = width/2, yTranslation = height/2;
        int gray;
        for (int y = 0; y < cHeight; y++) {
            for (int x = 0; x < cWidth; x++) {
                if (mask != null && !mask[x][y]) continue;

                int index = x + cWidth*y;
                output[index] = 0;
                for (int fy = 0; fy < height; fy++) {
                    for (int fx = 0; fx < width; fx++) {
                        gray = (int) (filter[fx + fy * width] * (0xFF & bg.get(
                                                        x - xTranslation + fx, y - yTranslation + fy
                                                )));
                        output[index] += 0xFF000000 | gray<<16 | gray<<8 | gray;
                    }
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return name;
    }
}
