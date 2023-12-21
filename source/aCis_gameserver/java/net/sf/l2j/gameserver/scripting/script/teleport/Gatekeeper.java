package net.sf.l2j.gameserver.scripting.script.teleport;

import net.sf.l2j.gameserver.expander.gatekeeper.actions.GetListAction;
import net.sf.l2j.gameserver.expander.gatekeeper.actions.GetMenuListAction;
import net.sf.l2j.gameserver.expander.gatekeeper.actions.TeleportAction;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.container.player.custom.helpers.Str;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.scripting.Quest;

import java.util.Objects;
import java.util.StringTokenizer;

public class Gatekeeper extends Quest {
    protected final GetMenuListAction _getMenuListAction = new GetMenuListAction();
    protected final TeleportAction _teleportAction = new TeleportAction();
    protected final GetListAction _getListAction = new GetListAction();
    private static final int _townsId = 1;
    private static final int _villagesId = 2;

    public Gatekeeper() {
        super(-1, "gatekeeper");

        addTalkId(30256);
        addFirstTalkId(30256);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        if (player.getTarget() != npc) {
            Str.sendMsg(player, "Поговорите с " + npc.getName());

            player.sendPacket(ActionFailed.STATIC_PACKET);
            return super.onAdvEvent(event, npc, player);
        }

        final StringTokenizer command = new StringTokenizer(event, " ");
        final String action = command.hasMoreTokens() ? command.nextToken() : null;
        final int listId = command.hasMoreTokens() ? Integer.parseInt(command.nextToken()) : 0;
        final int locationId = command.hasMoreTokens() ? Integer.parseInt(command.nextToken()) : 0;
        final String parentAction = command.hasMoreTokens() ? command.nextToken() : null;

        switch (Objects.requireNonNull(action)) {
            case "Towns" -> _getMenuListAction.execute(player, npc, _townsId, action);
            case "Villages" -> _getMenuListAction.execute(player, npc, _villagesId, action);
            case "Teleport" -> _teleportAction.execute(player, npc, listId, locationId);
            case "List" -> _getListAction.execute(player, npc, listId, parentAction);
            default -> player.sendPacket(ActionFailed.STATIC_PACKET);
        }

        return super.onAdvEvent(event, npc, player);
    }

    @Override
    public String onFirstTalk(Npc npc, Player player) {
        _getMenuListAction.execute(player, npc, _townsId, "Towns");

        return super.onFirstTalk(npc, player);
    }
}