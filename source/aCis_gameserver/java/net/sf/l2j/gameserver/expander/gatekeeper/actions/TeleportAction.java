package net.sf.l2j.gameserver.expander.gatekeeper.actions;

import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.common.actions.SendMsgAction;
import net.sf.l2j.gameserver.expander.gatekeeper.calculators.PriceCalculator;
import net.sf.l2j.gameserver.expander.gatekeeper.conditions.NotNeedPayCondition;
import net.sf.l2j.gameserver.expander.gatekeeper.data.dto.GatekeeperActionDto;
import net.sf.l2j.gameserver.expander.gatekeeper.data.xml.LocationsData;
import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.LocationHolder;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;

public class TeleportAction extends Action {
    protected final NotNeedPayCondition _notNeedPayCondition = new NotNeedPayCondition();
    protected final PriceCalculator _priceCalculator = new PriceCalculator();
    protected final PaymentAction _paymentAction = new PaymentAction();

    public void execute(Player player, Npc npc, GatekeeperActionDto data) {
        LocationHolder location = LocationsData.getInstance().getLocation(data.getLocationId());

        if (location == null) {
            handleInvalidLocation(player, "Выбранная локация недоступна для телепорта");
            return;
        }

        if (isLocationInSiege(location)) {
            handleInvalidLocation(player, "Вы не можете телепортироваться в локацию, которая находится в осаде");
            return;
        }

        int price = _priceCalculator.execute(player, location);
        if (_notNeedPayCondition.execute(location) || _paymentAction.execute(player, npc, location.getPriceId(), price)) {
            handleTeleport(player, location);
        }
    }

    private void handleInvalidLocation(Player player, String message) {
        SendMsgAction.execute(player, message);
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    private boolean isLocationInSiege(LocationHolder location) {
        if (location.getCastleId() != 0) {
            Castle castle = CastleManager.getInstance().getCastleById(location.getCastleId());
            return castle != null && castle.getSiege().isInProgress();
        }
        return false;
    }

    private void handleTeleport(Player player, LocationHolder location) {
        location.setTeleportCount(location.getTeleportCount() + 1);
        player.teleportTo(location, 20);
    }
}
