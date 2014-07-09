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
        int a, r, g, b;
        int Y, Cb, Cr;
        boolean[][] mask = marvinImageMask.getMaskArray();

        for (int x = 0; x < marvinImage.getWidth(); x++) {
            for (int y = 0; y < marvinImage.getHeight(); y++) {
                if (!mask[x][y]) continue;

                a = marvinImage.getAlphaComponent(x, y);
                Y = marvinImage.getIntComponent0(x, y);
                Cb= marvinImage.getIntComponent1(x, y);
                Cr= marvinImage.getIntComponent2(x, y);

                r = (int) (Y                 + 1.403 * Cr);
                g = (int) (Y    - 0.344 * Cb - 0.714 * Cr);
                b = (int) (Y    + 1.77  * Cb);

                marvinImage2.setIntColor(x, y, a, r, g, b);
            }
        }
    }

    @Override
    public void load() {

    }
}
