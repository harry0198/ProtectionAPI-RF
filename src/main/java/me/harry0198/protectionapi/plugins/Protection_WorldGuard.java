package me.harry0198.protectionapi.plugins;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.harry0198.protectionapi.components.UniversalRegion;
import me.harry0198.protectionapi.protection.UniversalProtection;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Protection_WorldGuard extends UniversalProtection {

    private final String name = "WorldGuard";
    private Plugin plugin;
    private WorldGuardPlugin worldGuard;
    private Logger log;

    @SuppressWarnings("ConstantConditions")
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

    @Override
    public Collection<UniversalRegion> getRegions() {
        long current = System.currentTimeMillis();
        Collection<ProtectedRegion> regions = new ArrayList<>();
        for (World world : this.plugin.getServer().getWorlds()) {
            RegionManager t = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
            if (t != null)
                regions.addAll(t.getRegions().values());
        }
//
//        regions.stream().map(region -> new UniversalRegion.Builder(new Vector(region.getMinimumPoint().getBlockX(),region.getMinimumPoint().getBlockY(), region.getMinimumPoint().getBlockZ()),
//                new Vector(region.getMinimumPoint().getBlockX(),region.getMinimumPoint().getBlockY(), region.getMinimumPoint().getBlockZ()),
//                Bukkit.getWorld("world")).build()).collect(Collectors.toList());

        System.out.println(System.currentTimeMillis() - current);
        return null;
    }

    @Override
    public Collection<UniversalRegion> getRegions(World world) {
        return null;
    }

    @Override
    public boolean supportsPolygonRegions() {
        return true;
    }

    @Override
    public Collection<UniversalRegion> getPlayerClaims(Player player) {
        return null;
    }

    @Override
    public Collection<UniversalRegion> getPlayerClaims(World world, Player player) {
        return null;
    }

    @Override
    public Collection<UniversalRegion> getIntersectingRegions(Collection<UniversalRegion> regionCollection) {
        return null;
    }

    @Override
    public boolean isRegionIntersecting(UniversalRegion region) {
        return false;
    }
}
