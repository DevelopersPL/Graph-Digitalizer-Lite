package plugins.convert;

import marvin.gui.MarvinAttributesPanel;
import marvin.image.MarvinImage;
import marvin.image.MarvinImageMask;
import marvin.plugin.MarvinAbstractImagePlugin;
import marvin.util.MarvinAttributes;

/**
 * Created by Marek on 2014-07-08.
 * Converts bitmap from RGB to YCrCb
 */
public class RGB2YCbCr extends MarvinAbstractImagePlugin {
    @Override
    public MarvinAttributesPanel getAttributesPanel() {
        return null;
    }

    @Override
    public void process(MarvinImage marvinImage, MarvinImage marvinImage2, MarvinAttributes marvinAttributes, MarvinImageMask marvinImageMask, boolean bool) {
        int count = marvinImage.getWidth() * marvinImage.getHeight();
        int r, g, b, c;
        int Y, Cb, Cr;

        int[] in = marvinImage.getIntColorArray(), out = marvinImage2.getIntColorArray();
        for (int i = 0; i < count; i++) {
            c = in[i];
            r = (c & 0xFF0000)>>16;
            g = (c & 0x00FF00)>>8;
            b = (c & 0x0000FF);

            Y =       (int) (+ 0.299    * r + 0.587    * g + 0.114    * b) & 0xFF;
            Cb= (int) (128   - 0.168736 * r - 0.331264 * g + 0.5      * b) & 0xFF;
            Cr= (int) (128   + 0.5      * r - 0.418688 * g - 0.081312 * b);

            out[i] = 0xFF000000&c | Y<<16 | Cb<<8 | Cr;
        }
    }

    @Override
    public void load() {

    }
}
