package net.nebulacraft.nebulacore.util.model;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerVehicle;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCamera;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShipManager implements PacketListener, Listener {


    public static Map<Player, Double> shipSpeedData = new HashMap<>();
    public static Map<Player, Double> shipAngleData = new HashMap<>();
    public static Map<Player, Integer> shipSpectate = new HashMap<>();
    boolean isMounted = false;
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        Player player = (Player) event.getPlayer();

        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
            isMounted = true;
            if (!ShipManager.shipAngleData.containsKey(player)) return;
            int entityid = ShipManager.shipSpectate.get(player);
            WrapperPlayClientSteerVehicle packet = new WrapperPlayClientSteerVehicle(event);

            double currentSpeed = ShipManager.shipSpeedData.get(player); // get the current speed
            double currentAngle = ShipManager.shipAngleData.get(player); // get the current angle change;

            float vehicleForward = packet.getForward();
            float vehicleSideways = packet.getSideways();

            if (vehicleForward > 0) { // We are trying to move forwards, so increase speed
                ShipManager.shipSpeedData.put(player, currentSpeed + 0.015); // The speed is quite sensitive so we only add 0.05.
            } else if (vehicleForward < 0) { // We are trying to move backwards, so decrease speed
                ShipManager.shipSpeedData.put(player, currentSpeed - 0.015);
            }

            if (vehicleSideways > 0) { // We are trying to move left, decrease angle
                ShipManager.shipAngleData.put(player, currentAngle - 0.3); // 360 degrees in a full circle, so 1 is not much compared to the speed.
            } else if (vehicleSideways < 0) { // We are trying to move right, increase angle
                ShipManager.shipAngleData.put(player, currentAngle + 0.3);
            }
if (isMounted){spectateEntity(player, entityid);}
        }
    }
    public static void spectateEntity(Player player, int entityid) {


        WrapperPlayServerCamera cameraPacket = new WrapperPlayServerCamera(entityid);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, cameraPacket);
    }
    @EventHandler
    public void onEntityDismount(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player player) {
            isMounted = false;
            if (!ShipManager.shipAngleData.containsKey(player)) return;

        }
    }
}
