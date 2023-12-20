package net.sf.l2j.gameserver.expander.buffer.calculators;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.expander.buffer.model.holder.BuffHolder;
import net.sf.l2j.gameserver.expander.common.calculators.Calculator;
import net.sf.l2j.gameserver.model.actor.Player;

public class BuffPriceCalculator extends Calculator {
    protected final int _pricePerUnit = Config.BUFFER_PRICE_PER_UNIT;
    protected final int _freeToLvl = Config.BUFFER_FREE_PLAYER_LVL;
    protected final int _extraMulPriceItemId = 57;
    protected final int _minMpConsumeCount = 242;

    public int execute(Player player) {
        final int playerLvl = player.getStatus().getLevel();

        if (Config.FREE_BUFFER || playerLvl <= _freeToLvl) {

            return 0;
        }

        return _pricePerUnit * playerLvl;
    }

    public int execute(Player player, BuffHolder buffHolder) {
        if (execute(player) == 0) {

            return 0;
        }

        int playerLvl = player.getStatus().getLevel();
        int price = buffHolder.getPriceCount();

        if (buffHolder.getPriceId() == _extraMulPriceItemId) {
            int buffMpConsume = buffHolder.getSkill().getMpConsume();
            int mpCost = buffMpConsume == 0 ? _minMpConsumeCount : buffMpConsume;

            price *= mpCost;

            if (!Config.FREE_BUFFER) {
                playerLvl -= _freeToLvl;
            }

            price *= playerLvl;
        }

        return price;
    }
}
