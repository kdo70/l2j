package net.sf.l2j.gameserver.expander.newbiehelper.conditions;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.expander.common.conditions.Condition;
import net.sf.l2j.gameserver.model.actor.Player;

public class NeedPayCondition extends Condition {
    protected final int _freeToLvl = Config.NEWBIE_BUFFER_FREE_PLAYER_LVL;

    public boolean execute(Player player) {
        final int playerLvl = player.getStatus().getLevel();

        return !Config.FREE_BUFFER && playerLvl > _freeToLvl;
    }
}