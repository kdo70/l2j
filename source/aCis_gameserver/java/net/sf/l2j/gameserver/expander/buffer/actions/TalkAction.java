package net.sf.l2j.gameserver.expander.buffer.actions;

import net.sf.l2j.gameserver.expander.buffer.calculators.BuffPriceCalculator;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.container.player.custom.helpers.Str;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class TalkAction extends Action {
    protected final String _folder = "data/html/script/feature/buffer/";
    protected final BuffPriceCalculator _calculator = new BuffPriceCalculator();

    public void execute(Player player, Npc npc) {
        final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
        final int price = _calculator.execute(player);

        html.setFile(_folder + npc.getNpcId() + _htmlExt);

        html.replace("%npcTitle%", npc.getTitle());
        html.replace("%npcName%", npc.getName());
        html.replace("%price%", Str.number(price));

        player.sendPacket(html);
    }
}
