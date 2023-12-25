package net.sf.l2j.gameserver.expander.gatekeeper.calculators;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.expander.common.calculators.Calculator;
import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.LocationHolder;
import net.sf.l2j.gameserver.model.actor.Player;

public class PriceCalculator extends Calculator {
    public int execute(Player player, LocationHolder locationHolder) {
        final int playerLvl = player.getStatus().getLevel();

        if (Config.FREE_BUFFER || locationHolder.getPriceCount() == 0) {

            return 0;
        }

        return locationHolder.getPriceCount() * playerLvl;
    }
}
