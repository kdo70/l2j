package net.sf.l2j.gameserver.expander.newbiehelper.actions;

import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.newbiehelper.calculators.BuffPriceCalculator;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.container.player.custom.helpers.Str;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class TalkAction extends Action {
    protected final String _dialogFolder = "data/html/script/feature/NewbieHelper/";
    protected final BuffPriceCalculator _buffPriceCalculator = new BuffPriceCalculator();

    public void execute(Player player, Npc npc) {
        final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
        final int price = _buffPriceCalculator.execute(player);

        html.setFile(_dialogFolder + npc.getNpcId() + _htmlExt);

        html.replace("%npcName%", npc.getName());
        html.replace("%price%", Str.number(price));

        player.sendPacket(html);
    }
}
