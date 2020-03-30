package me.harry0198.protectionapi.components;

import me.harry0198.protectionapi.Octree;
import me.harry0198.protectionapi.ProtectionAPI;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.awt.geom.Area;
import java.util.*;
import java.util.stream.Collectors;

public abstract class UniversalRegion {

    private Vector min, max;
    private World world;

    public UniversalRegion(World world) {
        this.world = world;
    }

    public abstract Area getArea();

    public abstract Area2D getPlanePoints();

    public World getWorld() {
        return world;
    }

    public Vector getMinPoint() {
        return this.min;
    }

    public Vector getMaxPoint() {
        return this.max;
    }

    protected void setMinMaxPoint(List<Vector> points) {
        int minX = points.get(0).getBlockX();
        int minY = points.get(0).getBlockY();
        int minZ = points.get(0).getBlockZ();
        int maxX = minX;
        int maxY = minY;
        int maxZ = minZ;

        for (Vector vector : points) {
            int x = vector.getBlockX();
            int y  = vector.getBlockY();
            int z = vector.getBlockZ();

            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (z < minZ) minZ = z;

            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
            if (z > maxZ) maxZ = z;
        }

        setMaxPoint(new Vector(maxX, maxY, maxZ));
        setMinPoint(new Vector(minX, minY, minZ));
    }

    protected void setMinPoint(Vector vector) {
        this.min = vector;
    }

    protected void setMaxPoint(Vector vector) {
        this.max = vector;
    }

    public List<UniversalRegion> getIntersectingRegions(Collection<UniversalRegion> regions) {

        Map<World, List<UniversalRegion>> regionSplit = regions.stream()
                .collect(Collectors.groupingBy(UniversalRegion::getWorld));

        List<UniversalRegion> intersectingRegions = new ArrayList<>();

        for (World world : regionSplit.keySet()) {
            List<UniversalRegion> regionList = regionSplit.get(world);

            // If lots of regions, use octree and check for nearest regions instead of running intersection detection on every region
            if (regionList.size() > 15) {
                Octree tree = ProtectionAPI.createOctree(world);
                regionList.forEach(tree::insert);
                if (tree.nearestRegions(this).size() < 1) return Collections.emptyList();
            }

            Area area = getArea();

            for (UniversalRegion region : regions) {
                if (intersects(region, area)) {
                    intersectingRegions.add(region);
                }
            }
        }

        return intersectingRegions;
    }

    public boolean intersects(UniversalRegion region, Area areaToCheck) {
        // By checking this we remove "unnecessary" heavier checks
        if (intersectsBoundingBox(region)) {
            Area thisArea = region.getArea();
            thisArea.intersect(areaToCheck);
            return !thisArea.isEmpty();
        }

        return false;
    }

    protected boolean intersectsBoundingBox(UniversalRegion region) {
        // Readability
        Vector min = this.min;
        Vector max = this.max;
        Vector rMaxP = region.getMaxPoint();
        Vector rMinP = region.getMinPoint();

        if (rMaxP.getBlockX() < min.getBlockX()) return false;
        if (rMaxP.getBlockY() < min.getBlockY()) return false;
        if (rMaxP.getBlockZ() < min.getBlockZ()) return false;

        if (rMinP.getBlockX() > max.getBlockX()) return false;
        if (rMinP.getBlockY() > max.getBlockY()) return false;
        return rMinP.getBlockZ() <= max.getBlockZ();
    }


}
