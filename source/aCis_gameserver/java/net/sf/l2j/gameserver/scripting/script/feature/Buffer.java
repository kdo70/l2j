package net.sf.l2j.gameserver.scripting.script.feature;

import net.sf.l2j.gameserver.expander.buffer.actions.*;
import net.sf.l2j.gameserver.expander.buffer.conditions.AvailableBuffCondition;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.container.player.custom.helpers.Str;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.scripting.Quest;

import java.util.Objects;
import java.util.StringTokenizer;

public class Buffer extends Quest {
    protected final TalkAction _talkAction = new TalkAction();
    protected final AvailableBuffCondition _availableBuffCondition = new AvailableBuffCondition();
    protected final GetListAction _getListAction = new GetListAction();
    protected final ApplyBuffsAction _applyBuffsAction = new ApplyBuffsAction();
    protected final ApplyBuffAction _applyBuffAction = new ApplyBuffAction();
    protected final GetInfoAction _getInfoAction = new GetInfoAction();
    protected final GetErrorMessageAction _getErrorMessageAction = new GetErrorMessageAction();

    public Buffer() {
        super(-1, "buffer");

        // TODO: Добавить идентификаторы
        addTalkId(31077);
        addFirstTalkId(31077);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        if (player.getTarget() != npc) {
            Str.sendMsg(player, "Поговорите с " + npc.getName());

            player.sendPacket(ActionFailed.STATIC_PACKET);
            return null;
        }

        if (!_availableBuffCondition.execute(player)) {
            Str.sendMsg(player, _getErrorMessageAction.execute(player));

            player.sendPacket(ActionFailed.STATIC_PACKET);
            return null;
        }

        final StringTokenizer command = new StringTokenizer(event, " ");
        final String action = command.hasMoreTokens() ? command.nextToken() : null;
        final int index = command.hasMoreTokens() ? Integer.parseInt(command.nextToken()) : 1;
        final int page = command.hasMoreTokens() ? Integer.parseInt(command.nextToken()) : 1;

        switch (Objects.requireNonNull(action)) {
            case "Talk" -> _talkAction.execute(player, npc);
            case "Buffs" -> _applyBuffsAction.execute(player, npc, page);
            case "List" -> _getListAction.execute(player, npc, page);
            case "Buff" -> _applyBuffAction.execute(player, npc, index, page);
            case "Info" -> _getInfoAction.execute(player, npc, index, page);
            default -> player.sendPacket(ActionFailed.STATIC_PACKET);
        }

        return super.onAdvEvent(event, npc, player);
    }

    @Override
    public String onFirstTalk(Npc npc, Player player) {
        if (!_availableBuffCondition.execute(player)) {
            Str.sendMsg(player, _getErrorMessageAction.execute(player));

            player.sendPacket(ActionFailed.STATIC_PACKET);
            return null;
        }

        _talkAction.execute(player, npc);

        return super.onFirstTalk(npc, player);
    }
}
