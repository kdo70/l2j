package net.sf.l2j.gameserver.expander.buffer.calculators;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.expander.buffer.conditions.NeedPayCondition;
import net.sf.l2j.gameserver.expander.buffer.model.holder.BuffHolder;
import net.sf.l2j.gameserver.expander.common.calculators.Calculator;
import net.sf.l2j.gameserver.model.actor.Player;

public class BuffPriceCalculator extends Calculator {
    private static final int BASE_PRICE = Config.BUFFER_PRICE_PER_UNIT;
    private static final int FREE_PLAYER_LEVEL = Config.BUFFER_FREE_PLAYER_LVL;
    private static final int EXTRA_MUL_PRICE_ITEM_ID = Config.BUFFER_EXTRA_MUL_PRICE_ITEM_ID;
    private static final int MAX_PK_MUL = Config.BUFFER_MAX_PK_MUL;
    private final NeedPayCondition needPayCondition = new NeedPayCondition();

    public int execute(Player player, int count) {
        if (!needPayCondition.execute(player) || Config.FREE_BUFFER) {

            return 0;
        }

        int adjustedLevel = Math.max(player.getStatus().getLevel() - FREE_PLAYER_LEVEL, 1);
        int playerKills = Math.min(player.getPkKills(), MAX_PK_MUL);

        return BASE_PRICE * count * (adjustedLevel + playerKills);
    }

    public int execute(Player player, BuffHolder buffHolder) {
        if (!needPayCondition.execute(player) || Config.FREE_BUFFER) {

            return 0;
        }

        int price = buffHolder.getPriceCount();
        if (buffHolder.getPriceId() == EXTRA_MUL_PRICE_ITEM_ID) {
            int adjustedLevel = Math.max(player.getStatus().getLevel() - FREE_PLAYER_LEVEL, 1);
            int playerKills = Math.min(player.getPkKills(), MAX_PK_MUL);

            price *= adjustedLevel + playerKills;
        }

        return price;
    }
}
