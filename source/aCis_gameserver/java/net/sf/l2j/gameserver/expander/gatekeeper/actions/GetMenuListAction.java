package net.sf.l2j.gameserver.expander.gatekeeper.actions;

import net.sf.l2j.Config;
import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.gatekeeper.data.xml.MenuData;
import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.LocationHolder;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class GetMenuListAction extends Action {
    protected final GetMenuAction _getMenuAction = new GetMenuAction();
    protected final String _dialogTemplate = "data/html/script/feature/gatekeeper/towns.htm";
    protected final String _itemTemplate = "data/html/script/feature/gatekeeper/town-item.htm";
    protected final String _activeColor = "color=B09878";
    protected final int _minItemPerList = Config.TELEPORT_MENU_ITEM_PER_PAGE;
    protected final int _heightIndentPerItem = Config.TELEPORT_MENU_HEIGHT_INDENT_PER_ITEM;

    public void execute(Player player, Npc npc, int listId, String action) {
        final Map<Integer, LocationHolder> list = MenuData.getInstance().getList(listId);
        final StringBuilder locations = new StringBuilder();

        int heightIndent = 0;
        int visibleCount = 0;

        Comparator<LocationHolder> comparator = Comparator.comparing(LocationHolder::getId, Comparator.naturalOrder());
        for (LocationHolder location : list.values().stream().sorted(comparator).toList()) {
            StringUtil.append(locations, getTemplateItem(action, listId, location));

            visibleCount++;
        }

        if (visibleCount < _minItemPerList) {
            heightIndent += _heightIndentPerItem * (_minItemPerList - visibleCount);
        }

        final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());

        html.setFile(_dialogTemplate);

        html.replace("%npcTitle%", npc.getTitle());
        html.replace("%npcName%", npc.getName());

        html.replace("%list%", locations.toString());
        html.replace("%menu%", _getMenuAction.execute());

        html.replace("%towns%", Objects.equals(action, "Towns") ? _activeColor : "");
        html.replace("%villages%", Objects.equals(action, "Villages") ? _activeColor : "");
        html.replace("%popular%", Objects.equals(action, "Popular") ? _activeColor : "");
        html.replace("%recommended%", Objects.equals(action, "Recommended") ? _activeColor : "");

        html.replace("%heightIndent%", heightIndent);

        player.sendPacket(html);
    }

    private String getTemplateItem(String action, int listId, LocationHolder location) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(_itemTemplate));
            String templateItem = reader.readLine();

            templateItem = templateItem.replace("%listId%", String.valueOf(listId));
            templateItem = templateItem.replace("%id%", String.valueOf(location.getId()));
            templateItem = templateItem.replace("%childId%", String.valueOf(location.getChildId()));
            templateItem = templateItem.replace("%name%", String.valueOf(location.getName()));
            templateItem = templateItem.replace("%placeholder%", String.valueOf(location.getPlaceholder()));
            templateItem = templateItem.replace("%desc%", String.valueOf(location.getDesc()));
            templateItem = templateItem.replace("%parentAction%", action);

            return templateItem;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
