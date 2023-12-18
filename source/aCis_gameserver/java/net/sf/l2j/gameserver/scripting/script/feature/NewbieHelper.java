package net.sf.l2j.gameserver.scripting.script.feature;

import net.sf.l2j.gameserver.expander.newbiehelper.actions.*;
import net.sf.l2j.gameserver.expander.newbiehelper.conditions.AvailableBuffCondition;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.container.player.custom.helpers.Str;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.scripting.Quest;

import java.util.Objects;
import java.util.StringTokenizer;

public class NewbieHelper extends Quest {
    protected static final TalkAction _talkAction = new TalkAction();
    protected static final AvailableBuffCondition _availableBuffCondition = new AvailableBuffCondition();
    protected static final GetListAction _getListAction = new GetListAction();
    protected static final ApplyBuffsAction _applyBuffsAction = new ApplyBuffsAction();
    protected static final ApplyBuffAction _applyBuffAction = new ApplyBuffAction();
    protected static final GetInfoAction _getInfoAction = new GetInfoAction();

    public NewbieHelper() {
        super(-1, "feature");

        addTalkId(31077);
        addFirstTalkId(31077);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        if (player.getTarget() != npc) {
            Str.sendMsg(player, npc.getName() + " должен быть вашей текущей целью");

            player.sendPacket(ActionFailed.STATIC_PACKET);
            return null;
        }

        if (!_availableBuffCondition.execute(player)) {
            return "guide_for_newbie002.htm";
        }

        final StringTokenizer command = new StringTokenizer(event, " ");
        final String action = command.hasMoreTokens() ? command.nextToken() : null;
        final int index = command.hasMoreTokens() ? Integer.parseInt(command.nextToken()) : 1;

        switch (Objects.requireNonNull(action)) {
            case "Talk" -> _talkAction.execute(player, npc);
            case "Buffs" -> _applyBuffsAction.execute(player, npc);
            case "List" -> _getListAction.execute(player, npc);
            case "Buff" -> _applyBuffAction.execute(player, npc, index);
            case "Info" -> _getInfoAction.execute(player, npc, index);
        }

        player.sendPacket(ActionFailed.STATIC_PACKET);

        return null;
    }

    @Override
    public String onFirstTalk(Npc npc, Player player) {
        _talkAction.execute(player, npc);

        return null;
    }
}
