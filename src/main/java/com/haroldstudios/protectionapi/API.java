package com.haroldstudios.protectionapi;

import com.google.common.collect.ImmutableList;
import com.haroldstudios.protectionapi.protection.Protection;
import com.haroldstudios.protectionapi.components.UniversalRegion;
import jdk.internal.jline.internal.Nullable;
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class API {

    private final ImmutableList<Protection> protectionPlugins;

    API(ProtectionAPI main, ImmutableList<Protection> protectionPlugins){
        this.protectionPlugins = protectionPlugins;
    }

    public ImmutableList<Protection> getProtectionPlugins() {
        return protectionPlugins;
    }

    /**
     * Gets individual protection plugin and methods' interface
     * @param plugin Plugin name to get
     * @return Protection plugin's protectionAPI interface
     */
    @Nullable
    public Protection getProtectionPluginAsInterface(String plugin) {
        for (Protection pl : protectionPlugins) {
            if (pl.getName().equalsIgnoreCase(plugin)) return pl;
        }

        return null;
    }

    /**
     * Gets the plugin instance of the region provider inputted
     * @param plugin Plugin name to get instance of
     * @return Plugin instance
     */
    @Nullable
    public Object getProtectionPluginInstance(String plugin) {
        Protection pl = getProtectionPluginAsInterface(plugin);
        if (pl != null && pl.isEnabled())
            return pl.getExternalInstance();

        return null;
    }

    /**
     * Gets every region by the supported region providers
     * @return List of UniversalRegions
     */
    public List<UniversalRegion> getAllRegions() {
        List<UniversalRegion> reg = new ArrayList<>();
        protectionPlugins.forEach(pl -> {
            if (pl.isEnabled())
                reg.addAll(pl.getRegions());
        });
        return reg;
    }

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
