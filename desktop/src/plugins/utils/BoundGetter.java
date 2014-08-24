package plugins.utils;

/**
 * Created by Marek on 2014-07-08.
 * interface for classes which tells how to handle border out of border pixels
 */
public interface BoundGetter {
    int get(int x, int y);
}
