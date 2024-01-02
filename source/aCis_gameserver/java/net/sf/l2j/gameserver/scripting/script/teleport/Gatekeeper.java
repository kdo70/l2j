package net.sf.l2j.gameserver.scripting.script.teleport;

import net.sf.l2j.gameserver.expander.gatekeeper.actions.GetListAction;
import net.sf.l2j.gameserver.expander.gatekeeper.actions.GetMenuListAction;
import net.sf.l2j.gameserver.expander.gatekeeper.actions.TeleportAction;
import net.sf.l2j.gameserver.expander.gatekeeper.data.dto.GatekeeperData;
import net.sf.l2j.gameserver.expander.gatekeeper.data.enums.MenuEnum;
import net.sf.l2j.gameserver.expander.helpers.Str;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.scripting.Quest;

import java.util.Objects;
import java.util.StringTokenizer;

public class Gatekeeper extends Quest {
    protected final GetMenuListAction _getMenuListAction = new GetMenuListAction();
    protected final TeleportAction _teleportAction = new TeleportAction();
    protected final GetListAction _getListAction = new GetListAction();

    public Gatekeeper() {
        super(-1, "gatekeeper");

        addTalkId(30256, 30080, 30320, 30059, 30899, 30848, 30177, 31275,
                31964, 31320, 30006, 30146, 30134, 30540, 30576, 30233);
        addFirstTalkId(30256, 30080, 30320, 30059, 30899, 30848, 30177,
                31275, 31964, 31320, 30006, 30146, 30134, 30540, 30576, 30233);
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
        final int page = command.hasMoreTokens() ? Integer.parseInt(command.nextToken()) : 1;

        final GatekeeperData gatekeeperData = new GatekeeperData(action, listId, locationId, parentAction, page);

        switch (Objects.requireNonNull(action)) {
            case "Towns" -> _getMenuListAction.execute(player, npc, MenuEnum.TOWNS.getId(), gatekeeperData);
            case "Villages" -> _getMenuListAction.execute(player, npc, MenuEnum.VILLAGES.getId(), gatekeeperData);
            case "Popular" -> _getListAction.execute(player, npc, MenuEnum.POPULAR.getId(), gatekeeperData);
            case "Recommended" -> _getMenuListAction.execute(player, npc, MenuEnum.RECOMMENDED.getId(), gatekeeperData);
            case "Teleport" -> _teleportAction.execute(player, npc, gatekeeperData);
            case "List" -> _getListAction.execute(player, npc, listId, gatekeeperData);
            default -> player.sendPacket(ActionFailed.STATIC_PACKET);
        }

        return super.onAdvEvent(event, npc, player);
    }

    @Override
    public String onFirstTalk(Npc npc, Player player) {
        final int townsId = MenuEnum.TOWNS.getId();
        final GatekeeperData data = new GatekeeperData("Towns", townsId, 0, "Towns", 1);

        _getMenuListAction.execute(player, npc, MenuEnum.TOWNS.getId(), data);

        return super.onFirstTalk(npc, player);
    }
}