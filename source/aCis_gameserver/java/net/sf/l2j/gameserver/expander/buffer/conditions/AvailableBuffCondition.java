package net.sf.l2j.gameserver.expander.buffer.conditions;

import net.sf.l2j.gameserver.expander.buffer.data.xml.BuffsClassesData;
import net.sf.l2j.gameserver.expander.buffer.model.holder.BuffHolder;
import net.sf.l2j.gameserver.expander.common.conditions.Condition;
import net.sf.l2j.gameserver.model.actor.Player;

public class AvailableBuffCondition extends Condition {
    protected final int _playerMinLvl = BuffsClassesData.getInstance().getMinLvl();
    protected final int _playerMaxLvl = BuffsClassesData.getInstance().getMaxLvl();

    public boolean execute(Player player) {
        final int playerLvl = player.getStatus().getLevel();
        final boolean lvlCondition = playerLvl >= _playerMinLvl && playerLvl <= _playerMaxLvl;

        return lvlCondition && !player.isSubClassActive() && player.getKarma() == 0;
    }

    public boolean execute(Player player, BuffHolder buffHolder) {
        final int playerLvl = player.getStatus().getLevel();
        final boolean lvlCondition = playerLvl >= buffHolder.getMinLvl() && playerLvl <= buffHolder.getMaxLvl();

        return lvlCondition && !player.isSubClassActive() && player.getKarma() == 0;
    }
}
