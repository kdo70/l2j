package net.sf.l2j.gameserver.expander.buffer.actions;

import net.sf.l2j.gameserver.expander.buffer.calculators.BuffPriceCalculator;
import net.sf.l2j.gameserver.expander.buffer.model.holder.BuffHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.helpers.Str;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.List;

public class TalkAction extends Action {
    protected final String _template = "data/html/script/feature/buffer/index.htm";
    protected final BuffPriceCalculator _calculator = new BuffPriceCalculator();
    protected final GetValidBuffsAction _getValidBuffsAction = new GetValidBuffsAction();

    public void execute(Player player, Npc npc) {
        final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
        final List<BuffHolder> validBuffs = _getValidBuffsAction.execute(player);
        final int price = _calculator.execute(player, validBuffs.size());

        html.setFile(_template);

        html.replace("%npcTitle%", npc.getTitle());
        html.replace("%npcName%", npc.getName());
        html.replace("%price%", Str.number(price));

        player.sendPacket(html);
    }
}
