package net.sf.l2j.gameserver.expander.gatekeeper.calculators;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.expander.common.calculators.Calculator;
import net.sf.l2j.gameserver.expander.gatekeeper.conditions.NotNeedPayCondition;
import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.LocationHolder;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.location.Location;

public class PriceCalculator extends Calculator {
    private static final int FREE_PLAYER_LEVEL = Config.FREE_TELEPORT_LVL;
    private static final int EXTRA_MUL_PRICE_ITEM_ID = Config.TELEPORT_EXTRA_MUL_PRICE_ITEM_ID;
    private static final int DISTANCE_THRESHOLD = Config.TELEPORT_DISTANNCE_TRASHHOLD;
    private static final int MAX_PK_MUL = Config.TELEPORT_MAX_PK_MUL;
    private final NotNeedPayCondition notNeedPayCondition = new NotNeedPayCondition();

    public int execute(Player player, LocationHolder location) {
        if (notNeedPayCondition.execute(location)) {
            return 0;
        }

        int adjustedLevel = Math.max(player.getStatus().getLevel() - FREE_PLAYER_LEVEL, 1);
        int playerKills = Math.min(player.getPkKills(), MAX_PK_MUL);

        int distanceMul = 1;
        if (location.getPriceId() == EXTRA_MUL_PRICE_ITEM_ID) {
            distanceMul = calculateDistanceMultiplier(player, location);
        }

        return location.getPriceCount() * (adjustedLevel + playerKills + distanceMul);
    }

    private int calculateDistanceMultiplier(Player player, LocationHolder location) {
        Location destination = new Location(location.getX(), location.getY(), location.getZ());
        double distance = player.distance3D(destination);
        return Math.max((int) (distance / DISTANCE_THRESHOLD), 1);
    }
}
