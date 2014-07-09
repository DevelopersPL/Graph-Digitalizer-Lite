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
        int a, r, g, b;
        int Y, Cb, Cr;
        boolean[][] mask = marvinImageMask.getMaskArray();

        for (int x = 0; x < marvinImage.getWidth(); x++) {
            for (int y = 0; y < marvinImage.getHeight(); y++) {
                if (!mask[x][y]) continue;

                a = marvinImage.getAlphaComponent(x, y);
                r = marvinImage.getIntComponent0(x, y);
                g = marvinImage.getIntComponent1(x, y);
                b = marvinImage.getIntComponent2(x, y);

                Y =       (int) (+ 0.299    * r + 0.587    * g + 0.114    * b) & 0xFF;
                Cb= (int) (128   - 0.168736 * r - 0.331264 * g + 0.5      * b) & 0xFF;
                Cr= (int) (128   + 0.5      * r - 0.418688 * g - 0.081312 * b);

                marvinImage2.setIntColor(x, y, a, Y, Cb, Cr);
            }
        }
    }

    @Override
    public void load() {

    }
}
