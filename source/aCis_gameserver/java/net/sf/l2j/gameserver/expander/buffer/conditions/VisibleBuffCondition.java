package net.sf.l2j.gameserver.expander.buffer.conditions;

import net.sf.l2j.gameserver.expander.common.conditions.Condition;
import net.sf.l2j.gameserver.expander.buffer.model.holder.BuffHolder;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.taskmanager.GameTimeTaskManager;

public class VisibleBuffCondition extends Condition {
    protected final AvailableBuffCondition _availableBuffCondition = new AvailableBuffCondition();

    public boolean execute(Player player, BuffHolder buffHolder) {
        if (!_availableBuffCondition.execute(player, buffHolder)) {

            return false;
        }

        return !buffHolder.isOnlyNight() || buffHolder.isOnlyNight() && GameTimeTaskManager.getInstance().isNight();
    }
}