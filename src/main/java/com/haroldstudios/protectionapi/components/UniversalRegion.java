package com.haroldstudios.protectionapi.components;

import com.haroldstudios.protectionapi.Octree;
import com.haroldstudios.protectionapi.ProtectionAPI;
import jdk.internal.jline.internal.Nullable;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.awt.geom.Area;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public abstract class UniversalRegion {

    private Vector min, max;
    private World world;
    private Collection<UUID> regionOwners;
    private Collection<UUID> regionMembers;
    private Collection<UUID> regionAdmins;
    private String regionProvider;
    private String welcomeMessage;
    private String farewellMessage;
    private Date dateCreated;
    private List<Flag> flags;
    private Priority priority;
    private Collection<UniversalRegion> childRegions;

    /**
     * Class constructor
     * @param world World region is based in
     */
    protected UniversalRegion(@NotNull World world) {
        this.world = world;
    }

    /**
     * Gets the Area Object of Protection Bounds
     * @return Area Object of bounds
     */
    public abstract Area getArea();

    /**
     * Gets the world the Protection is situated inside
     * @return World protection is inside of
     */
    public World getWorld() {
        return world;
    }

    /**
     * Get absolute minimum point
     * @return Minimum boundary
     */
    public Vector getMinPoint() {
        return this.min;
    }

    /**
     * Get absolute maximum point
     * @return Maximum boundary
     */
    public Vector getMaxPoint() {
        return this.max;
    }

    /**
     * Gets the owners of the region only
     * @return Collection of UUIDs of owners
     */
    public Collection<UUID> getRegionOwners() {
        return regionOwners == null ? Collections.emptyList() : regionOwners;
    }

    /**
     * Gets the admins of the region only
     * @return Collection of UUIDs of admins
     */
    public Collection<UUID> getRegionAdmins() {
        return regionAdmins == null ? Collections.emptyList() : regionAdmins;
    }

    /**
     * Gets the members of the region only
     * @return Collection of UUIDs of members
     */
    public Collection<UUID> getRegionMembers() {
        return regionMembers == null ? Collections.emptyList() : regionMembers;
    }

    /**
     * Gets the region provider
     * See providers at package {@link com.haroldstudios.protectionapi.plugins}
     * @return Region Provider as String
     */
    @Nullable
    public String getRegionProvider() {
        return regionProvider;
    }

    /**
     * Gets priority of region if set
     * @return Priority setting if set
     */
    @Nullable
    public Priority getPriority() {
        return priority;
    }

    /**
     * Gets the welcome message from the region provider if any
     * @return Welcome message
     */
    @Nullable
    public String getWelcomeMessages() {
        return welcomeMessage;
    }

    /**
     * Gets the farewell message from the region provider if any
     * @return Farewell message
     */
    @Nullable
    public String getFarewellMessage() {
        return this.farewellMessage;
    }

    /**
     * Gets the date region was originally created if supported
     * @return Date created if exists otherwise null
     */
    @Nullable
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * Checks if the region has a valid date ID
     * @return If region has date
     */
    public boolean supportsDateCreated() {
        return dateCreated != null;
    }

    /**
     * Gets the child regions, if any. These are only provided by the plugins
     * If the region happens to have a region inside of it. Unless explicitly registered it is ignored.
     * @return Collection of child regions
     */
    public Collection<UniversalRegion> getChildRegions() {
        return childRegions;
    }

    /**
     * Sets the min and max point
     * @param points List of points to compare
     */
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

    /**
     * Sets min point
     * @param vector Vector to set
     */
    protected void setMinPoint(Vector vector) {
        this.min = vector;
    }

    /**
     * Sets max point
     * @param vector Vector to set
     */
    protected void setMaxPoint(Vector vector) {
        this.max = vector;
    }

    /**
     * Sets the owners of the region
     * @param regionOwners List of UUIDs of owners
     * @return This Class
     */
    public UniversalRegion setRegionOwners(Collection<UUID> regionOwners) {
        this.regionOwners = regionOwners;
        return this;
    }

    /**
     * Sets Admins of the region
     * @param regionAdmins Lis of UUIDs of admins
     * @return This Class
     */
    public UniversalRegion setRegionAdmins(List<UUID> regionAdmins) {
        this.regionAdmins = regionAdmins;
        return this;
    }

    /**
     * Sets Members of the region
     * @param regionMembers List of UUIDs of members
     * @return This Class
     */
    public UniversalRegion setRegionMembers(Collection<UUID> regionMembers) {
        this.regionMembers = regionMembers;
        return this;
    }

    /**
     * Sets the region provider
     * @param regionProvider String format of region provider. Name provider under implementation of hook class #getName
     * @return This Class
     */
    public UniversalRegion setRegionProvider(String regionProvider) {
        this.regionProvider = regionProvider;
        return this;
    }

    /**
     * Sets the welcome message of the region
     * @param message Welcome message
     * @return This Class
     */
    public UniversalRegion setWelcomeMessages(String message) {
        this.welcomeMessage = message;
        return this;
    }

    /**
     * Sets the farewell message of the region
     * @param message Farewell message
     * @return This Class
     */
    public UniversalRegion setFarewellMessage(String message) {
        this.farewellMessage = message;
        return this;
    }

    /**
     * Sets the child regions of the region
     * @param regions UniversalRegion
     * @return This Class
     */
    public UniversalRegion setChildRegions(Collection<UniversalRegion> regions) {
        this.childRegions = regions;
        return this;
    }

    /**
     * Sets the date the region was originally created
     * @param date Date created
     * @return This Class
     */
    public UniversalRegion setDateCreated(Date date) {
        this.dateCreated = date;
        return this;
    }

    /**
     * Gets which regions intersect with this region from provided list
     * @param regions List of regions to compare against
     * @return Collection of regions that intersect
     */
    public Collection<UniversalRegion> getIntersectingRegions(Collection<UniversalRegion> regions) {

        List<UniversalRegion> intersectingRegions = new ArrayList<>();

        regions = regions.stream()
                .collect(Collectors.groupingBy(UniversalRegion::getWorld)).get(this.getWorld());


        // If lots of regions, use octree and check for nearest regions instead of running intersection detection on every region
        if (regions.size() > 15) {
            Octree tree = ProtectionAPI.createOctree(world);
            regions.forEach(tree::insert);
            List<UniversalRegion> nearestRegions = tree.nearestRegions(this);
            if (nearestRegions.size() < 1) return Collections.emptyList();
            regions = nearestRegions;
        }

        Area area = getArea();

        for (UniversalRegion region : regions) {
            if (intersects(region, area)) {
                intersectingRegions.add(region);
            }
        }

        return intersectingRegions;
    }

    /**
     * Checks if a UniversalRegion intersects with Area
     * @param region Universal Region to check against
     * @param thisArea Area to check against
     * @return If region intersects with area
     */
    public boolean intersects(UniversalRegion region, Area thisArea) {
        // By checking this we remove "unnecessary" heavier checks
        if (intersectsBoundingBox(region)) {
            Area testArea = region.getArea();
            testArea.intersect(thisArea);
            return !thisArea.isEmpty();
        } else {
            return false;
        }

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
        if (rMinP.getBlockZ() > max.getBlockZ()) return false;

        return true;
    }


}
