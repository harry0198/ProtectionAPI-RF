package me.harry0198.protectionapi.protection;

import me.harry0198.protectionapi.UniversalRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface Protection {

    /**
     * Checks if RegionProvider is enabled
     * @return Success or Failure
     */
    boolean isEnabled();

    /**
     * Gets the name of the RegionProvider
     * @return Name of RegionProvider
     */
    String getName();

    /**
     * Gets all Regions from RegionProvider
     * @return Collection of Regions
     */
    Collection<UniversalRegion> getRegions();

    /**
     * Gets all regions from RegionProvider in specified world
     * @param world World to query
     * @return Collection of all regions in world
     */
    Collection<UniversalRegion> getRegions(World world);

    /**
     * Does the plugin support polygon shapes as regions
     * @return If implementation supports polygon region shape
     */
    boolean supportsPolygonRegions();

    /**
     * Gets a list of the player's claims
     * @param player Player's claims to get
     * @return Collection of Player's Claims
     */
    Collection<UniversalRegion> getPlayerClaims(Player player);

    /**
     * Gets a list of player's claims in specified world
     * @param world World to query
     * @param player Player's claims to get
     * @return Collection of Player's Claims
     */
    Collection<UniversalRegion> getPlayerClaims(World world, Player player);

    /**
     * Gets Intersecting regions
     * @param regionCollection Collection of Regions to Query
     * @return Collection of intersecting regions
     */
    Collection<UniversalRegion> getIntersectingRegions(Collection<UniversalRegion> regionCollection);

    /**
     * Checks if region is intersecting with any other regions
     * @param region UniversalRegion to check against
     * @return If is intersecting
     */
    boolean isRegionIntersecting(UniversalRegion region);

}