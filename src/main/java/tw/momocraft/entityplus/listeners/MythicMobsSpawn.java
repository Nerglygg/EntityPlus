package tw.momocraft.entityplus.listeners;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import tw.momocraft.coreplus.api.CorePlusAPI;
import tw.momocraft.entityplus.handlers.ConfigHandler;
import tw.momocraft.entityplus.utils.entities.EntityMap;
import tw.momocraft.entityplus.utils.entities.EntityUtils;

import java.util.Map;

public class MythicMobsSpawn implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMythicMobsSpawn(MythicMobSpawnEvent e) {
        if (!ConfigHandler.getConfigPath().isSpawn()) {
            return;
        }
        Entity entity = e.getEntity();
        String entityType = e.getMobType().getInternalName();
        // To get entity properties.
        Map<String, EntityMap> entityProp = ConfigHandler.getConfigPath().getEntityProp().get(entityType);
        // Checking if the properties contains this type of entity.
        if (entityProp != null) {
            // Checking every groups of this entity.
            Location loc = entity.getLocation();
            Block block = loc.getBlock();
            EntityMap entityMap;
            boolean resFlag = ConfigHandler.getConfigPath().isSpawnResFlag();
            for (String groupName : entityProp.keySet()) {
                entityMap = entityProp.get(groupName);
                // Checking the spawn "biome".
                if (!CorePlusAPI.getUtilsManager().containIgnoreValue(block.getBiome().name(), entityMap.getBoimes(), entityMap.getIgnoreBoimes())) {
                    CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(),"Spawn", entityType, "Biome", "continue", groupName,
                            new Throwable().getStackTrace()[0]);
                    continue;
                }
                // Checking the spawn location is "liquid" or not.
                if (!CorePlusAPI.getUtilsManager().isLiquid(block, entityMap.getLiquid())) {
                    CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(),"Spawn", entityType, "Liquid", "continue", groupName,
                            new Throwable().getStackTrace()[0]);
                    continue;
                }
                // Checking the spawn time is "Day" or not.
                if (!CorePlusAPI.getUtilsManager().isDay(loc.getWorld().getTime(), entityMap.getDay())) {
                    CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(),"Spawn", entityType, "Day", "continue", groupName,
                            new Throwable().getStackTrace()[0]);
                    continue;
                }
                // Checking the spawn "location".
                if (!CorePlusAPI.getLocationManager().checkLocation(loc, entityMap.getLocMaps(), true)) {
                    CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(),"Spawn", entityType, "Location", "continue", groupName,
                            new Throwable().getStackTrace()[0]);
                    continue;
                }
                // Checking the "blocks" nearby the spawn location.
                if (!CorePlusAPI.getBlocksManager().checkBlocks(loc, entityMap.getBlocksMaps(), true)) {
                    CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(),"Spawn", entityType, "Blocks", "continue", groupName,
                            new Throwable().getStackTrace()[0]);
                    continue;
                }
                // Checking the spawn "Residence-Flag".
                if (!CorePlusAPI.getResidenceManager().checkFlag(null, loc, resFlag, "spawnbypass")) {
                    CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(),"Spawn", entityType, "Residence-Flag", "continue", groupName,
                            new Throwable().getStackTrace()[0]);
                    continue;
                }
                // Checking the spawn "chance".
                if (!CorePlusAPI.getUtilsManager().isRandChance(entityMap.getChance())) {
                    // If the creature spawn location has reach the maximum creature amount, it will cancel the spawn event.
                    if (entityMap.getLimit() != null) {
                        if (EntityUtils.checkLimit(entity, entityMap.getLimit())) {
                            // Add a tag for this creature.
                            //ConfigHandler.getConfigPath().getLivingEntityMap().putMap(entity.getUniqueId(), new Pair<>(entityType, groupName));
                            CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(),"Spawn", entityType, "Limit", "return", groupName,
                                    new Throwable().getStackTrace()[0]);
                            return;
                        }
                    } else {
                        // Add a tag for this creature.
                        //ConfigHandler.getConfigPath().getLivingEntityMap().putMap(entity.getUniqueId(), new Pair<>(entityType, groupName));
                        CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(),"Spawn", entityType, "Chance", "return", groupName,
                                new Throwable().getStackTrace()[0]);
                        return;
                    }
                }
                CorePlusAPI.getLangManager().sendFeatureMsg(ConfigHandler.getPrefix(),"Spawn", entityType, "Final", "cancel", groupName,
                        new Throwable().getStackTrace()[0]);
                e.setCancelled();
                return;
            }
        }
    }
}