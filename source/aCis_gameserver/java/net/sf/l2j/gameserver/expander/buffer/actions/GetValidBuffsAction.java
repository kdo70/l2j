package net.sf.l2j.gameserver.expander.buffer.actions;

import net.sf.l2j.gameserver.expander.buffer.data.xml.BuffsClassesData;
import net.sf.l2j.gameserver.expander.buffer.model.holder.BuffHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.model.actor.Player;

import java.util.List;

public class GetValidBuffsAction extends Action {
    public List<BuffHolder> execute(Player player) {
        final boolean playerIsMystic = player.isMystic();
        final int playerLvl = player.getStatus().getLevel();

        return BuffsClassesData.getInstance().getValidBuffs(playerIsMystic, playerLvl);
    }
}
