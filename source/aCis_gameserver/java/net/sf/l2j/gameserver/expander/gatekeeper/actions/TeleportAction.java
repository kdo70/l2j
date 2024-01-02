package net.sf.l2j.gameserver.expander.gatekeeper.actions;

import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.expander.gatekeeper.data.dto.GatekeeperData;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.gatekeeper.calculators.PriceCalculator;
import net.sf.l2j.gameserver.expander.gatekeeper.conditions.NeedPayCondition;
import net.sf.l2j.gameserver.expander.gatekeeper.data.xml.LocationsData;
import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.LocationHolder;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.expander.helpers.Str;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;

public class TeleportAction extends Action {
    protected final NeedPayCondition _needPayCondition = new NeedPayCondition();
    protected final PriceCalculator _priceCalculator = new PriceCalculator();
    protected final PaymentAction _paymentAction = new PaymentAction();

    public void execute(Player player, Npc npc, GatekeeperData data) {
        final LocationHolder location = LocationsData.getInstance().getLocation(data.getLocationId());
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

        final int price = _priceCalculator.execute(player, location);
        if (_needPayCondition.execute(location) || _paymentAction.execute(player, npc, location.getPriceId(), price)) {
            location.setTeleportCount(location.getTeleportCount() + 1);
            player.teleportTo(location, 20);
        }
    }
}
