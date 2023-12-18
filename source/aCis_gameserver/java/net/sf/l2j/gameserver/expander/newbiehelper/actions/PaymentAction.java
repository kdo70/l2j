package net.sf.l2j.gameserver.expander.newbiehelper.actions;

import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;

public class PaymentAction extends Action {
    public boolean execute(Player player, Npc npc, int itemId, int count) {
        return player.destroyItemByItemId("NewbieBuffer", itemId, count, npc, true);
    }
}
