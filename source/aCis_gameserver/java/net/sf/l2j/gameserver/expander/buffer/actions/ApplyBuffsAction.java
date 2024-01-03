package net.sf.l2j.gameserver.expander.buffer.actions;

import net.sf.l2j.Config;
import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.gameserver.expander.buffer.calculators.BuffPriceCalculator;
import net.sf.l2j.gameserver.expander.buffer.conditions.NeedPayCondition;
import net.sf.l2j.gameserver.expander.buffer.model.holder.BuffHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.common.actions.SendMsgAction;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;

import java.util.List;

public class ApplyBuffsAction extends Action {
    protected final NeedPayCondition _needPayCondition = new NeedPayCondition();
    protected final PaymentAction _paymentAction = new PaymentAction();
    protected final GetValidBuffsAction _getValidBuffsAction = new GetValidBuffsAction();
    protected final BuffPriceCalculator _buffPriceCalculator = new BuffPriceCalculator();
    protected final int _priceItemId = Config.BUFFER_PRICE_ITEM_ID;
    protected static final GetListAction _getListAction = new GetListAction();

    public void execute(Player player, Npc npc, int page) {
        final List<BuffHolder> validBuffs = _getValidBuffsAction.execute(player);
        int price = _buffPriceCalculator.execute(player, validBuffs.size());

        if (_needPayCondition.execute(player) && !_paymentAction.execute(player, npc, _priceItemId, price)) {
            SendMsgAction.execute(player, SystemMessageId.YOU_NOT_ENOUGH_ADENA);
            player.sendPacket(ActionFailed.STATIC_PACKET);

            return;
        }

        player.broadcastPacket(new MagicSkillUse(npc, player, 1036, 1, 1000, 0));
        ThreadPool.schedule(() -> setBuffs(npc, player, validBuffs), 1000);

        _getListAction.execute(player, npc, page);
    }

    private void setBuffs(Npc npc, Player player, List<BuffHolder> validBuffs) {
        for (BuffHolder buff : validBuffs) {
            buff.getSkill().getEffects(npc, player);
        }
    }
}
