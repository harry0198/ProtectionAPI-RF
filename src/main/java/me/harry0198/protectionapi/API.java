package me.harry0198.protectionapi;

import com.sun.xml.internal.bind.v2.TODO;
import me.harry0198.protectionapi.components.UniversalRegion;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class API {

    final private ProtectionAPI main;

    API(ProtectionAPI main){
        this.main = main;
    }

    /**
     * Is point within octane
     *
     * @param vector Point
     * @param octane Octane
     * @return True if between axis, false if not.
     */
    public boolean isPointBetweenOctane(Vector vector, Octane octane){
        return octane.isBetweenAxis(vector);
    }

    public Collection<UniversalRegion> getAllRegions() {
        return null;
    }


//        TODO
//    TODO Convert all loaded regions on startup -- every time called convert unconverted.
//    TODO Ex: Copy list of all regions from plugins and compare each time if converted - least resource usage
//    TODO


    /**
     * Checks if a cuboid overlaps another octane
     *
     * @param oct Octane to check for
     * @param min Bottom corner to check
     * @param max Top corner to check
     * @return true if overlapping / contained within octane
     */
    public static boolean isCuboidOverlapping(Octane oct, Vector min, Vector max) {

        Vector min1 = oct.getMin();
        Vector max2 = oct.getMax();

        return isCuboidOverlapping(
                new Vector (min1.getBlockX(), min1.getBlockY(), min1.getBlockZ()),
                new Vector (max2.getBlockX(), max2.getBlockY(), max2.getBlockZ()),
                min,
                max
        );
    }

    /**
     * Checks if a cuboid overlaps another cuboid
     *
     * @param min1 Bottom corner to check 1
     * @param max2 Top corner to check 1
     * @param min Bottom corner to check 2
     * @param max Bottom corner to check 2
     * @return true if is overlapping points
     */
    public static boolean isCuboidOverlapping(Vector min1, Vector max2, Vector min, Vector max) {

        if(!intersectsDimension(min.getBlockX(), max.getBlockX(), min1.getBlockX(), max2.getBlockX()))
            return false;

        if(!intersectsDimension(min.getBlockY(), max.getBlockY(), min1.getBlockY(), max2.getBlockY()))
            return false;

        if(!intersectsDimension(min.getBlockZ(), max.getBlockZ(),min1.getBlockZ(), max2.getBlockZ()))
            return false;

        return true;
    }

    private static boolean intersectsDimension(int aMin, int aMax, int bMin, int bMax) {
        return aMin <= bMax && aMax >= bMin;
    }


}
