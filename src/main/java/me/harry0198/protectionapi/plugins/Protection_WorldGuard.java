package me.harry0198.protectionapi.plugins;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.harry0198.protectionapi.Octane;
import me.harry0198.protectionapi.Octree;
import me.harry0198.protectionapi.UniversalRegion;
import me.harry0198.protectionapi.protection.UniversalProtection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Protection_WorldGuard extends UniversalProtection {

    private final String name = "WorldGuard";
    private Plugin plugin;
    private WorldGuardPlugin worldGuard;
    private Logger log;

    public Protection_WorldGuard(Plugin plugin) {
        this.plugin = plugin;
        this.log = plugin.getLogger();
        getRegions();

        if (worldGuard == null) {
            Plugin worldGuard = plugin.getServer().getPluginManager().getPlugin(name);
            if (worldGuard != null && worldGuard.isEnabled()) {
                this.worldGuard = (WorldGuardPlugin) worldGuard;
                log.info(String.format("[ProtectionAPI] %s hooked.", name));
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    private Map<World, Collection<ProtectedRegion>> getAllProtectedRegions() {

        Map<World, Collection<ProtectedRegion>> regionMap = new HashMap<>();

        for (World world : this.plugin.getServer().getWorlds()) {
            RegionManager t = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
            if (t != null)
                regionMap.put(world, t.getRegions().values());
        }

        return regionMap;
    }

    private UniversalRegion unwrap(World world, ProtectedRegion region) {
        return new UniversalRegion.Builder(
                new Vector(region.getMinimumPoint().getBlockX(), region.getMinimumPoint().getBlockY(), region.getMinimumPoint().getBlockZ()),
                new Vector(region.getMaximumPoint().getBlockX(), region.getMaximumPoint().getBlockY(), region.getMaximumPoint().getBlockZ()),
                world).build();
    }


    @Override
    public Collection<UniversalRegion> getRegions() {
        Map<World, Collection<ProtectedRegion>> regionMap = getAllProtectedRegions();
        Collection<UniversalRegion> universalRegions = new ArrayList<>();

        regionMap.forEach((world, regionList) -> universalRegions.addAll(regionList.stream().map(region -> unwrap(world, region)).collect(Collectors.toList())));

        return universalRegions;
    }

    @Override
    public Collection<UniversalRegion> getRegions(World world) {
        Map<World, Collection<ProtectedRegion>> allRegions = getAllProtectedRegions();
        if (allRegions.get(world) == null) return Collections.emptyList();

        return allRegions.get(world).stream().map(region -> unwrap(world, region)).collect(Collectors.toList());
    }

    @Override
    public boolean supportsPolygonRegions() {
        return true;
    }

    @Override
    public Collection<UniversalRegion> getPlayerClaims(Player player) {
        return getRegions().stream().filter(region -> region.getMembers().contains(player.getUniqueId())).collect(Collectors.toList());
    }

    @Override
    public Collection<UniversalRegion> getPlayerClaims(World world, Player player) {
        return getPlayerClaims(player).stream().filter(region -> region.getWorld().equals(world)).collect(Collectors.toList());
    }

    @Override
    public Collection<UniversalRegion> getIntersectingRegions(Collection<UniversalRegion> regionCollection) {
        Map<World, List<UniversalRegion>> regionList = new HashMap<>();

        for (UniversalRegion region : regionCollection) {
            regionList.computeIfAbsent(region.getWorld(), k -> new ArrayList<>());
            regionList.get(region.getWorld()).add(region);
        }

        regionList.forEach(((world, universalRegions) -> {
            Octree tree = createTree(world);
            for (UniversalRegion universalRegion : universalRegions) {
                tree.insert(universalRegion);
            }
            //TODO get intersecting
        }));

        return null;
    }

    @Override
    public boolean isRegionIntersecting(UniversalRegion region) {

        com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(region.getWorld());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        Collection<ProtectedRegion> regionCollection = container.get(adaptedWorld).getRegions().values();

        BlockVector3 min = BlockVector3.at(region.getMin().getBlockX(), region.getMin().getBlockY(), region.getMin().getBlockZ());
        BlockVector3 max = BlockVector3.at(region.getMax().getBlockX(), region.getMax().getBlockY(), region.getMax().getBlockZ());

        ProtectedRegion reg = new ProtectedCuboidRegion("ProtectionAPI-tmp", min, max);

        List<ProtectedRegion> regList = reg.getIntersectingRegions(regionCollection);

        if (regList.size() > 0)
            return true;
        return false;
    }

    private Octree createTree(World world) {
        final WorldBorder wb = world.getWorldBorder();
        final double radius = wb.getSize()/2;
        final Location center = wb.getCenter();

        return new Octree(1, world, new Octane(
                new Vector(center.getX() - radius, 0, center.getZ() - radius),
                new Vector(center.getX() + radius, 256, center.getZ() + radius)));
    }
}
