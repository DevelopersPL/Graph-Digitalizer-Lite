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
public class YCbCr2RGB extends MarvinAbstractImagePlugin {
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
            Y = (c & 0xFF0000)>>16;
            Cb= (c & 0x00FF00)>>8;
            Cr= (c & 0x0000FF);

            r = (int) (Y                 + 1.403 * Cr);
            g = (int) (Y    - 0.344 * Cb - 0.714 * Cr);
            b = (int) (Y    + 1.77  * Cb);

            out[i] = 0xFF000000&c | r<<16 | g<<8 | b;
        }
    }

    @Override
    public void load() {

    }
}
