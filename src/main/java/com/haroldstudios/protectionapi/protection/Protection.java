package com.haroldstudios.protectionapi.protection;

import com.haroldstudios.protectionapi.components.UniversalRegion;
import org.bukkit.World;

import java.util.Collection;

/*
    This is an interface for the individual modules - main API methods in API
 */
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
     * Defaults to async thread
     * @return Collection of Regions
     */
    Collection<UniversalRegion> getRegions();

    /**
     * Gets all regions from RegionProvider in specified world (often more efficient)
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
     * Gets the plugin's instance from the original provider
     * @return Provider plugin instance
     */
    Object getExternalInstance();
}
