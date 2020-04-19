package com.haroldstudios.protectionapi;

import com.haroldstudios.protectionapi.components.UniversalRegion;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class Octree {

    private static final int MAX_JUMPS = 17;
    private static final int MAX_CAPACITY = 2;
    private final Octane bounds;
    private final int jumps; //how many stages down the tree?

    private final List<Node> nodeList = new ArrayList<>();
    private final World world;


    private Octree frontNorthWest,
            frontNorthEast,
            frontSouthWest,
            frontSouthEast,
            backNorthWest,
            backNorthEast,
            backSouthWest,
            backSouthEast = null;


    public Octree(int jumps, World world, Octane octane){

        this.world = world;

        this.bounds = octane;
        this.jumps = jumps;

    }

    public void subDivide() {

        int jump = jumps + 1;
        if (jump > MAX_JUMPS) return;

        Vector min = this.bounds.getMin();
        Vector max = this.bounds.getMax();

        // Middle of it all
        int xOffset = min.getBlockX() + (max.getBlockX() - min.getBlockX()) / 2;
        int yOffset = min.getBlockY() + (max.getBlockY() - min.getBlockY()) / 2;
        int zOffset = min.getBlockZ() + (max.getBlockZ() - min.getBlockZ()) / 2;

        backNorthWest = new Octree(jump, world, new Octane(
                new Vector(min.getBlockX(), yOffset, zOffset),
                new Vector(xOffset, max.getBlockY(), max.getBlockZ())));// Max corner

        frontNorthWest = new Octree(jump, world, new Octane(
                new Vector( min.getBlockX(), yOffset, min.getBlockZ()), // Minimum corner
                new Vector( xOffset, max.getBlockY(), zOffset)));// Max corner

        backNorthEast = new Octree(jump, world, new Octane(
                new Vector( xOffset, yOffset, zOffset), // Minimum corner
                max));// Max corner

        frontNorthEast = new Octree(jump, world, new Octane(
                new Vector( xOffset, yOffset, min.getBlockZ()), // Minimum corner
                new Vector( max.getBlockX(), max.getBlockY(), zOffset)));// Max corner

        backSouthEast = new Octree(jump, world, new Octane(
                new Vector( xOffset, min.getBlockY(), zOffset), // Minimum corner
                new Vector( max.getBlockX(), yOffset, zOffset)));// Max corner

        frontSouthEast = new Octree(jump, world, new Octane(
                new Vector( xOffset, min.getBlockY(), min.getBlockZ()), // Minimum corner
                new Vector( max.getBlockX(), yOffset, zOffset)));// Max corner

        backSouthWest = new Octree(jump, world, new Octane(
                        new Vector(min.getBlockX(), min.getBlockY(), zOffset), // Minimum corner
                new Vector( xOffset, yOffset, max.getBlockZ())));// Max corner

        frontSouthWest = new Octree(jump, world, new Octane(
                min, // Minimum corner
                new Vector( xOffset, yOffset, zOffset)));// Max corner

    }

    /**
     * Insert the region based off of min and max points
     *
     * @param region UniversalRegion object to insert
     */
    public void insert(UniversalRegion region) {

        Vector minLocation = region.getMinPoint();
        Vector maxLocation = region.getMaxPoint();

        if (!API.isCuboidOverlapping(bounds, minLocation, maxLocation)) return;

        Node node = new Node(region);
        if (nodeList.size() < MAX_CAPACITY) {
            nodeList.add(node);
            return;
        }


        if (frontNorthEast == null){
            subDivide();
        }



        if (API.isCuboidOverlapping(frontNorthEast.bounds, minLocation, maxLocation)) {
            frontNorthEast.insert(region);
        }
        if (API.isCuboidOverlapping(backNorthEast.bounds, minLocation, maxLocation)) {
                backNorthEast.insert(region);
        }

        if (API.isCuboidOverlapping(frontSouthEast.bounds, minLocation, maxLocation)) {
                frontSouthEast.insert(region);
        }

        if (API.isCuboidOverlapping(backSouthEast.bounds, minLocation, maxLocation)) {
            backSouthEast.insert(region);
        }

        if (API.isCuboidOverlapping(frontSouthWest.bounds, minLocation, maxLocation)) {
                frontSouthWest.insert(region);
        }

        if (API.isCuboidOverlapping(backSouthWest.bounds, minLocation, maxLocation)) {
                backSouthWest.insert(region);
        }

        if (API.isCuboidOverlapping(frontNorthWest.bounds, minLocation, maxLocation)) {
                frontNorthWest.insert(region);
        }

        if (API.isCuboidOverlapping(backNorthWest.bounds, minLocation, maxLocation)) {
            backNorthWest.insert(region);
        }
    }

    private static final List<Octree> tmpOctreeList = new ArrayList<>();

    private static void dfs(Octree tree, UniversalRegion region) {
        if (tree == null)
            return;

        if (isBoundsInOctane(tree.bounds, region)) {
            dfs(tree.backNorthEast, region);
            dfs(tree.frontNorthEast, region);
            dfs(tree.backNorthWest, region);
            dfs(tree.frontNorthWest, region);
            dfs(tree.backSouthEast, region);
            dfs(tree.frontSouthEast, region);
            dfs(tree.backSouthWest, region);
            dfs(tree.frontSouthWest, region);
        }

        tmpOctreeList.add(tree);
    }

    /**
     * Find the nearest regions using the octree
     *
     * @param region UniversalRegion object
     * @return A list of the nearest universalregions
     */
    public List<UniversalRegion> nearestRegions(UniversalRegion region) {

        dfs(this, region);

        List<Octree> octreeList = tmpOctreeList.stream().distinct().collect(Collectors.toList());

        List<UniversalRegion> r = new ArrayList<>();

        if (octreeList.size() != 0) {
            octreeList.forEach(e -> e.nodeList.forEach(f -> r.add(f.region)));
            return r;
        }

        return Collections.emptyList();
    }

    public static List<UniversalRegion> getIntersectingRegions(Octree tree) {

    }

    public static boolean isBoundsInOctane(Octane octane, UniversalRegion region) {
        if (octane == null) {
            return false;
        }

        return API.isCuboidOverlapping(octane, region.getMinPoint(), region.getMaxPoint());

    }
    static class Node {
        public Vector minLocation,
                maxLocation;
        public UniversalRegion region;

        Node(UniversalRegion region) {
            this.region = region;
            this.minLocation = region.getMinPoint();
            this.maxLocation = region.getMaxPoint();
        }
    }
}
