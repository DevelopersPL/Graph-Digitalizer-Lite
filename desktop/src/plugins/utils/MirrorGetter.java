/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plugins.utils;

/**
 *
 * @author Marek
 */
public class MirrorGetter implements BoundGetter {
    int[] matrix;
    int width, height;

    public MirrorGetter(int[] matrix, int width) {
        this.matrix = matrix;
        this.width = width;
        this.height = matrix.length/width;
    }
    
    
    
    public int get(int x, int y) {
        if (x < 0)
            x *= -1;
        if (y < 0)
            y *= -1;
        if (x >= width)
            x = width - (x - width) - 1;
        if (y >= height)
            y = height - (y - height) - 1;
        
        return matrix[x + width*y];
    }
}
//345384