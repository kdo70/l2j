package net.sf.l2j.gameserver.expander.buffer.actions;

import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.gameserver.expander.buffer.calculators.BuffPriceCalculator;
import net.sf.l2j.gameserver.expander.buffer.conditions.NeedPayCondition;
import net.sf.l2j.gameserver.expander.buffer.conditions.VisibleBuffCondition;
import net.sf.l2j.gameserver.expander.buffer.data.xml.BuffsCommonData;
import net.sf.l2j.gameserver.expander.buffer.model.holder.BuffHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.container.player.custom.helpers.Str;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;

import java.util.List;

public class ApplyBuffAction extends Action {
    protected final NeedPayCondition _needPayCondition = new NeedPayCondition();
    protected final VisibleBuffCondition _visibleBuffCondition = new VisibleBuffCondition();
    protected final BuffPriceCalculator _buffPriceCalculator = new BuffPriceCalculator();
    protected final PaymentAction _paymentAction = new PaymentAction();
    protected List<BuffHolder> _buffHolderList = BuffsCommonData.getInstance().getBuffs();
    protected static final GetListAction _getListAction = new GetListAction();

    public void execute(Player player, Npc npc, int index, int page) {
        final BuffHolder buffHolder = _buffHolderList.get(index);
        final int price = _buffPriceCalculator.execute(player, buffHolder);
        final int buffLvl = buffHolder.getSkill().getLevel();

        if (!_visibleBuffCondition.execute(player, buffHolder)) {
            Str.sendMsg(player, "Сейчас неподходящее время для этого действия, доступно только ночью");
            player.sendPacket(ActionFailed.STATIC_PACKET);

            return;
        }

        if (_needPayCondition.execute(player) && !_paymentAction.execute(player, npc, buffHolder.getPriceId(), price)) {
            Str.sendMsg(player, SystemMessageId.YOU_NOT_ENOUGH_ADENA);
            player.sendPacket(ActionFailed.STATIC_PACKET);

            return;
        }

        int visualSkillId = buffHolder.getSkill().getId();
        if (visualSkillId == 4699 || visualSkillId == 4702) {
            visualSkillId = visualSkillId == 4699 ? 4700 : 4703;
        }

        MagicSkillUse packet = new MagicSkillUse(npc, player, visualSkillId, buffLvl, 1000, 0);
        player.broadcastPacket(packet);
        ThreadPool.schedule(() -> buffHolder.getSkill().getEffects(npc, player), 1000);

        _getListAction.execute(player, npc, page);
    }
}
