package net.sf.l2j.gameserver.expander.gatekeeper.actions;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.gatekeeper.data.xml.LocationsData;
import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.LocationHolder;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class GetMenuListAction extends Action {
    protected final String _dialogTemplate = "data/html/script/feature/gatekeeper/towns.htm";
    protected final String _itemTemplate = "data/html/script/feature/gatekeeper/townItem.htm";

    protected final String _activeColor = "color=B09878";

    protected final int _minItemPerList = 10;
    protected final int _heightIndentPerItem = 20;

    public void execute(Player player, Npc npc, int listId, String action) {
        final List<LocationHolder> list = LocationsData.getInstance().getList(listId);

        final StringBuilder locations = new StringBuilder();
        int heightIndent = 0;

        int visibleCount = 0;
        for (LocationHolder location : list) {
            StringUtil.append(locations, getTemplateItem(location));

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

        html.replace("%towns%", Objects.equals(action, "Towns") ? _activeColor : "");
        html.replace("%villages%", Objects.equals(action, "Villages") ? _activeColor : "");
        html.replace("%popular%", Objects.equals(action, "Popular") ? _activeColor : "");
        html.replace("%recommended%", Objects.equals(action, "Recommended") ? _activeColor : "");
        html.replace("%heightIndent%", heightIndent);

        player.sendPacket(html);
    }

    private String getTemplateItem(LocationHolder location) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(_itemTemplate));
            String templateItem = reader.readLine();

            templateItem = templateItem.replace("%childId%", String.valueOf(location.getChildId()));
            templateItem = templateItem.replace("%name%", String.valueOf(location.getName()));
            templateItem = templateItem.replace("%lvl%", String.valueOf(location.getLvl()));
            templateItem = templateItem.replace("%locationId%", String.valueOf(location.getId()));
            templateItem = templateItem.replace("%desc%", String.valueOf(location.getDesc()));

            return templateItem;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
