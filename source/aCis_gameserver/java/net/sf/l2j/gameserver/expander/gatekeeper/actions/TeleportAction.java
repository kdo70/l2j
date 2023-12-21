package net.sf.l2j.gameserver.expander.gatekeeper.actions;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.gatekeeper.data.xml.LocationsData;
import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.LocationHolder;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.container.player.custom.helpers.Str;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;

import java.util.Map;

public class TeleportAction extends Action {
    protected final PaymentAction _paymentAction = new PaymentAction();

    public void execute(Player player, Npc npc, int listId, int locationId) {
        final Map<Integer, LocationHolder> list = LocationsData.getInstance().getList(listId);
        if (list == null) {
            Str.sendMsg(player, "Выбранная вами локация недоступна для телепорта");

            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final LocationHolder location = list.get(locationId);
        if (location == null) {
            Str.sendMsg(player, "Выбранная локация недоступна для телепорта");

            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (location.getCastleId() != 0) {
            final Castle castle = CastleManager.getInstance().getCastleById(location.getCastleId());
            if (castle != null && castle.getSiege().isInProgress()) {
                Str.sendMsg(player, "Вы не можете телепортироваться в локацию, которая находится в осаде");

                player.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
        }

        final boolean isNotNeedPay = Config.FREE_TELEPORT || location.getPriceCount() == 0;
        if (isNotNeedPay || _paymentAction.execute(player, npc, location.getPriceId(), location.getPriceCount())) {
            player.teleportTo(location, 20);
        }
    }
}
