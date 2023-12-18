package net.sf.l2j.gameserver.expander.newbiehelper.conditions;

import net.sf.l2j.gameserver.expander.common.conditions.Condition;
import net.sf.l2j.gameserver.expander.newbiehelper.data.xml.BuffsByClassData;
import net.sf.l2j.gameserver.expander.newbiehelper.model.holder.BuffHolder;
import net.sf.l2j.gameserver.model.actor.Player;

public class AvailableBuffCondition extends Condition {
    protected final int _playerMinLvl = BuffsByClassData.getInstance().getMinLvl();
    protected final int _playerMaxLvl = BuffsByClassData.getInstance().getMaxLvl();

    public boolean execute(Player player) {
        final int playerLvl = player.getStatus().getLevel();

        return playerLvl >= _playerMinLvl && playerLvl <= _playerMaxLvl && !player.isSubClassActive();
    }

    public boolean execute(Player player, BuffHolder buffHolder) {
        final int playerLvl = player.getStatus().getLevel();

        return playerLvl >= buffHolder.getMinLvl() && playerLvl <= buffHolder.getMaxLvl() && !player.isSubClassActive();
    }
}
