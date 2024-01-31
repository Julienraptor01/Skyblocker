package de.hysky.skyblocker.skyblock.dwarven;

import de.hysky.skyblocker.SkyblockerMod;
import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.utils.Utils;
import de.hysky.skyblocker.utils.scheduler.Scheduler;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;

public class CrystalsHud {
    public static final MinecraftClient client = MinecraftClient.getInstance();

    protected static final Identifier MAP_TEXTURE = new Identifier(SkyblockerMod.NAMESPACE, "textures/gui/crystals_map.png"); 

    private static final Identifier MAP_ICON = new Identifier("textures/map/map_icons.png");

    private static final String[] SMALL_LOCATIONS = new String[] {"Fairy Grotto","King","Corleone"};

    public static boolean visible = false;






    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("skyblocker")
                .then(ClientCommandManager.literal("hud")
                        .then(ClientCommandManager.literal("crystals")
                                .executes(Scheduler.queueOpenScreenCommand(CrystalsHudConfigScreen::new))))));

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            if (!SkyblockerConfigManager.get().locations.dwarvenMines.crystalsHud.enabled
                    || client.options.playerListKey.isPressed()
                    || client.player == null
                    || !visible) {
                return;
            }
            render(context, SkyblockerConfigManager.get().locations.dwarvenMines.crystalsHud.x,
                    SkyblockerConfigManager.get().locations.dwarvenMines.crystalsHud.y);
        });
    }

    public static IntIntPair getDimForConfig() {
        return IntIntPair.of(62, 62);
    }

    public static void render( DrawContext context, int hudX, int hudY) {
        //draw map texture
        context.
                drawTexture(MAP_TEXTURE,hudX,hudY,0,0,62,62,62,62);
        //if enabled add waypoint locations to map
        if (SkyblockerConfigManager.get().locations.dwarvenMines.crystalsHud.showLocations){
            Map<String,CrystalsWaypoint> ActiveWaypoints=  SkyblockerConfigManager.get().locations.dwarvenMines.crystalsWaypoints.ActiveWaypoints;
            for (CrystalsWaypoint waypoint : ActiveWaypoints.values()){
                Color waypointColor = waypoint.category.color;
                Pair<Integer, Integer> renderPos  = transformLocation(waypoint.pos.getX(),waypoint.pos.getZ());
                int locationSize  = SkyblockerConfigManager.get().locations.dwarvenMines.crystalsHud.locationSize;
                if (Arrays.asList(SMALL_LOCATIONS).contains(waypoint.name.getString())){//if small location half the location size
                    locationSize = locationSize/2;
                }
                //fill square of size locationSize around the coordinates of the location
                context.fill(hudX+renderPos.first()-locationSize/2,hudY+renderPos.second()-locationSize/2,hudX+renderPos.first()+locationSize/2,hudY+renderPos.second()+locationSize/2,waypointColor.getRGB());
            }
        }
        //draw player on map
        if (client.player == null || client.getNetworkHandler() == null) {
            return;
        }
        //get player location
        double playerX = client.player.getX();
        double playerZ = client.player.getZ();
        double facing = client.player.getYaw();
        Pair<Integer, Integer> renderPos  = transformLocation(playerX,playerZ);
        //draw marker on map
        context.
                drawTexture(MAP_ICON,hudX+renderPos.first()-2,hudY+renderPos.second()-2,58,2,4,4,128,128);

        //todo add direction and scale (can not work out how to rotate)

    }
    private static Pair<Integer, Integer> transformLocation(double x, double z){
        //converts an x and z to a location on the map
        int transformedX = (int)((x-202)/621 * 62);
        int transformedY = (int)((z -202)/621 * 62);
        transformedX = Math.max(0, Math.min(62, transformedX));
        transformedY = Math.max(0, Math.min(62, transformedY));

        return  Pair.of(transformedX,transformedY);
    }

    public static void update() {
        if (client.player == null || client.getNetworkHandler() == null || !SkyblockerConfigManager.get().locations.dwarvenMines.crystalsHud.enabled) {
            visible = false;
            return;
        }
        //get if the player is in the crystals
        visible = Utils.isInCrystals();


    }

}