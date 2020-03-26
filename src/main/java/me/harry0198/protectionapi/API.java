package me.harry0198.protectionapi;

import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

public class API {

    final private ProtectionAPI main;

    API(ProtectionAPI main){
        this.main = main;
    }

    /**
     * Returns if a region intersects with an existing region
     *
     * @param region UniversalRegion
     * @return True if intersects false if not.
     */
//    public boolean regionIntersects(UniversalRegion region){
////
////        // Because WorldGuard does not have any events and is a commonly used plugin. A separate check is done here so we don't have many errors.
////        // Note: This is a one-off as it is such a commonly used plugin. Other plugins will not get this feature and must include their own events to listen into.
////        for (BridgeInterface ui : ProtectionAPI.getInstance().getActivePlugins()){
////            if (ui.intersects(region)) return true;
////        }
////
////        for (UniversalRegion nearestRegion : getNearestRegions(region)) {
////            if (isCuboidOverlapping(nearestRegion, region.getMin(), region.getMax()))
////                return true;
////
////        }
////        return false;
////    }
////
////    /**
////     * Is point within octane
////     *
////     * @param vector Point
////     * @param octane Octane
////     * @return True if between axis, false if not.
////     */
////    public boolean isPointBetweenOctane(Vector vector, Octane octane){
////        return octane.isBetweenAxis(vector);
////    }
////
////    /**
////     * Adds a UniversalRegion to the regionList
////     *
////     * @param region UniversalRegion object
////     */
////    public void addUniversalRegion(UniversalRegion region) {
////        main.getRegionList().add(region);
////        main.getTree(region.getWorld()).insert(region);
////    }
////
////    /**
////     * Returns ArrayList of UniversalRegions
////     *
////     * @return List of regions
////     */
////    public List<UniversalRegion> getUniversalRegions(){
////        return main.getRegionList();
////    }
////
////    /**
////     * Attempts to remove a UniversalRegion object from list
////     *
////     * @param region UniversalRegion
////     * @return if successful.
////     */
////    public boolean removeUniversalRegion(UniversalRegion region) {
////        return main.removeFromRegionList(region);
////    }
////
////    /**
////     * Attempts to find a matching region
////     *
////     * @param region UniversalRegion
////     * @return UniversalRegion object if found. Null if not or more than one was found.
////     */
////    public UniversalRegion getSpecificMatchingRegion(UniversalRegion region) {
////
////        List<UniversalRegion> applicableRegions = getMatchingRegionBounds(region);
////
////        if (applicableRegions.size() == 0) return null;
////
////        // If only one of region w/ same min point. Skip other checks & remove.
////        if (applicableRegions.size() == 1) {
////            return applicableRegions.get(0);
////        }
////
////        // Narrow search further
////        List<UniversalRegion> selectedRegion = applicableRegions.stream()
////                .filter(r -> r.getMembers().containsAll(region.getMembers()))
////                .filter(r -> r.getOwners().containsAll(region.getOwners()))
////                .filter(r -> r.getClaimPluginName().equalsIgnoreCase(region.getClaimPluginName()))
////                .collect(Collectors.toList());
////
////        // If can find only one claim return. Otherwise null
////        if (selectedRegion.size() == 1){
////            return applicableRegions.get(0);
////
////        }
////
////        return null;
////    }
////
////    /**
////     * Gets a list of all regions that have the same boundary corners
////     *
////     * @param region UniversalRegion
////     * @return List of matching region boundaries as object
////     */
////    public List<UniversalRegion> getMatchingRegionBounds(UniversalRegion region) {
////        return getUniversalRegions().stream()
////                .filter(a -> a.getMin().equals(region.getMin()))
////                .filter(a -> a.getMax().equals(region.getMax()))
////                .filter(a -> a.getWorld().equals(region.getWorld()))
////                .collect(Collectors.toList());
////    }

    /**
     * Gets nearest regions by searching via octree
     *
     * @param region UniversalRegion object
     * @return A list of UniversalRegions that were nearby
     */
    public List<UniversalRegion> getNearestRegions(UniversalRegion region){
        return Octree.nearestRegions(main.getTree(region.getWorld()), region);
    }

    /**
     * Checks if a UniversalRegion overlaps two points
     *
     * @param oct UniversalRegion object
     * @param min Bottom corner to check
     * @param max Top corner to check
     * @return True if cuboid overlaps at any point
     */
    public static boolean isCuboidOverlapping(UniversalRegion oct, Vector min, Vector max) {

        Vector min1 = oct.getMin();
        Vector max2 = oct.getMax();

        boolean t =  isCuboidOverlapping(
                new Vector (min1.getBlockX(), min1.getBlockY(), min1.getBlockZ()),
                new Vector (max2.getBlockX(), max2.getBlockY(), max2.getBlockZ()),
                min,
                max
        );
        return t;
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
