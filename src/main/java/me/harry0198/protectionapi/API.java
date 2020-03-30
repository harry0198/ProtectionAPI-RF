package me.harry0198.protectionapi;

import com.google.common.collect.ImmutableList;
import me.harry0198.protectionapi.components.UniversalRegion;
import me.harry0198.protectionapi.protection.Protection;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class API {

    private final ProtectionAPI plugin;
    private ImmutableList<Protection> protectionPlugins;

    API(ProtectionAPI main, ImmutableList<Protection> protectionPlugins){
        this.plugin = main;
        this.protectionPlugins = protectionPlugins;

    }

    public ImmutableList<Protection> getProtectionPlugins() {
        return protectionPlugins;
    }

    @Nullable
    public Protection getProtectionPlugin(String plugin) {
        for (Protection pl : protectionPlugins) {
            if (pl.getName().equalsIgnoreCase(plugin)) return pl;
        }
        return null;
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

    public List<UniversalRegion> getAllRegions() {
        List<UniversalRegion> reg = new ArrayList<>();
        protectionPlugins.forEach(pl -> reg.addAll(pl.getRegions()));
        return reg;
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
