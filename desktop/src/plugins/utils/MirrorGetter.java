/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plugins.utils;

import marvin.image.MarvinImageMask;

/**
 *
 * @author Marek
 */
public class MirrorGetter implements BoundGetter {
    private boolean[][] mask = null;
    int[] matrix;
    int width, height;

    public MirrorGetter(int[] matrix, int width) {
        this.matrix = matrix;
        this.width = width;
        this.height = matrix.length/width;
    }

    public MirrorGetter(int[] matrix, int width, MarvinImageMask mask) {
        this.matrix = matrix;
        this.width = width;
        this.height = matrix.length/width;

        this.mask = mask.getMaskArray();
    }

    public int get(int x, int y) {
        if (x < 0)
            x *= -1;
        if (y < 0)
            y *= -1;
        if (!masked(x, y)) {
            if (masked(-x, y)) x = -x;
            if (masked(x, -y)) y = -y;
            if (masked(-x, -y)) {
                x = -x;
                y = -y;
            }
        }
        if (x >= width)
            x = width - (x - width) - 1;
        if (y >= height)
            y = height - (y - height) - 1;
        if (!masked(x, y)) {
            int endXFlipped = width - (x - width) - 1;
            int endYFlipped = height - (y - height) - 1;
            if (masked(endXFlipped, y)) x = endXFlipped;
            if (masked(x, endYFlipped)) y = endYFlipped;
            if (masked(endXFlipped, endYFlipped)) {
                x = endXFlipped;
                y = endYFlipped;
            }
        }

        if (!masked(x, y) || x < 0 || y < 0 || x >= width || y >= height)
            throw new IndexOutOfBoundsException("Index (x=" + x + ";y=" + y + ") inaccessible");

        return matrix[x + width*y];
    }

    private boolean masked(int x, int y) {
        return mask == null || (x > 0 && y > 0 && x <= width && y <= height && mask[x][y]);
    }
}
//345384