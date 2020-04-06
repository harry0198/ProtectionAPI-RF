package com.haroldstudios.protectionapi;

import com.google.common.collect.ImmutableList;
import com.haroldstudios.protectionapi.components.Region3D;
import com.haroldstudios.protectionapi.plugins.*;
import com.haroldstudios.protectionapi.protection.Protection;
import org.apache.commons.collections4.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ProtectionAPI extends JavaPlugin implements CommandExecutor {

    //TODO
    //IslandWorld
    //PreciousStones
    //LandLord
    //Residence
    //Kingdoms
    //Bentobox

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        long time = System.currentTimeMillis();
        api.getAllRegions();
        System.out.println("ms:" + (System.currentTimeMillis() - time));
        long time2 = System.currentTimeMillis();
        System.out.println(new Region3D(Bukkit.getWorld("world"), new Vector(50,0,50), new Vector(100,150,200)).getIntersectingRegions(api.getAllRegions()));
        System.out.println("ms:" + (System.currentTimeMillis() - time2));
        return false;
    }

    private API api;

    @Override
    public void onEnable() {

        this.getCommand("protection").setExecutor(this);
        saveDefaultConfig();

        api = new API(this, ImmutableList.copyOf(hook()));
    }

    @Override
    public void onDisable(){

    }

    public static Octree createOctree(World world) {

        // Set Octree range to world size
        final WorldBorder wb = world.getWorldBorder();
        final double radius = wb.getSize() / 2;
        final Location center = wb.getCenter();

        return new Octree(1, world, new Octane(
                new Vector(center.getX() - radius, 0, center.getZ() - radius),
                new Vector(center.getX() + radius, 256, center.getZ() + radius)));
    }

    private List<Protection> hook() {
        List<Protection> protectionList = new ArrayList<>();
        CollectionUtils.addIgnoreNull(protectionList, hookProtection("WorldGuard", Protection_WorldGuard.class, "com.sk89q.worldedit.WorldEdit", "com.sk89q.worldguard.WorldGuard"));
        CollectionUtils.addIgnoreNull(protectionList, hookProtection("GriefPrevention", Protection_GriefPrevention.class, "me.ryanhamshire.GriefPrevention.GriefPrevention"));
        CollectionUtils.addIgnoreNull(protectionList, hookProtection("RedProtect", Protection_RedProtect.class, "br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect"));
        //TODO Valid package?
        CollectionUtils.addIgnoreNull(protectionList, hookProtection("PlotSquared", Protection_PlotSquared.class, "com.github.intellectualsites.plotsquared.plot.object.Plot"));
        CollectionUtils.addIgnoreNull(protectionList, hookProtection("PlotSquared", Protection_Towny.class, "com.palmergames.bukkit.towny.Towny"));

        return protectionList;
    }

    private Protection hookProtection(String name, Class<? extends Protection> hookClass, String...packages) {
        try {
            if (packagesExists(packages)) {
                Protection prot = hookClass.getConstructor(Plugin.class).newInstance(this);
                info(String.format("%s found: %s", name, prot.isEnabled() ? "Loaded" : "Waiting"));
                return prot;
            }
        } catch (Exception e) {
            severe(String.format("There was an error hooking %s - check to make sure you're using a compatible version!", name));
        }
        return null;
    }

    /**
     * Determines if all packages in a String array are within the Classpath
     * This is the best way to determine if a specific plugin exists and will be
     * loaded. If the plugin package isn't loaded, we shouldn't bother waiting
     * for it!
     * @param packages String Array of package names to check
     * @return Success or Failure
     */
    private static boolean packagesExists(String...packages) {
        try {
            for (String pkg : packages) {
                Class.forName(pkg);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Log any message to console with any level.
     *
     * @param level the log level to log on.
     * @param msg   the message to log.
     */
    private void log(Level level, String msg) {
        getLogger().log(level, msg);
    }

    /**
     * Log a message to console on INFO level.
     *
     * @param msg the msg you want to log.
     */
    public void info(String msg) {
        log(Level.INFO, msg);
    }

    /**
     * Log a message to console on WARNING level.
     *
     * @param msg the msg you want to log.
     */
    public void warn(String msg) {
        log(Level.WARNING, msg);
    }

    /**
     * Log a message to console on SEVERE level.
     *
     * @param msg the msg you want to log.
     */
    public void severe(String msg) {
        log(Level.SEVERE, msg);
    }

    /**
     * Getter for the API
     * @return API instance
     */
    public static API getInstance() {
        return getPlugin(ProtectionAPI.class).getApi();
    }
    public API getApi() { return this.api; }


}
