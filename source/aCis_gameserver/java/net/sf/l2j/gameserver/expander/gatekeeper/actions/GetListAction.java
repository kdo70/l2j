package net.sf.l2j.gameserver.expander.gatekeeper.actions;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.enums.TeleportType;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.gatekeeper.data.xml.LocationsData;
import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.LocationHolder;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.container.player.custom.helpers.Str;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

public class GetListAction extends Action {
    protected final GetMenuAction _getMenuAction = new GetMenuAction();
    protected final String _itemTemplate = "data/html/script/feature/gatekeeper/item.htm";
    protected final String _dialogTemplate = "data/html/script/feature/gatekeeper/list.htm";
    protected final String _activeColor = "color=B09878";
    protected final int _minItemPerList = 14;
    protected final int _heightIndentPerItem = 20;

    public void execute(Player player, Npc npc, int listId, String parentAction) {
        final Map<Integer, LocationHolder> list = LocationsData.getInstance().getList(listId);
        if (list == null) {
            Str.sendMsg(player, "Выбранная вами локация недоступна для телепорта");

            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final StringBuilder locations = new StringBuilder();

        int heightIndent = 0;
        int visibleCount = 0;

        Comparator<LocationHolder> comparator = Comparator.comparing(LocationHolder::getId, Comparator.naturalOrder());
        if (listId == 20) {
            comparator = Comparator.comparing(LocationHolder::getTeleportCount, Comparator.reverseOrder());
        }

        for (LocationHolder locationHolder : list.values().stream().sorted(comparator).toList()) {
            if (locationHolder.getType() == TeleportType.NOBLE && !player.isNoble()) {
                continue;
            }

            StringUtil.append(locations, getTemplateItem(locationHolder, listId));

            visibleCount++;
        }

        if (visibleCount < _minItemPerList) {
            heightIndent += _heightIndentPerItem * (_minItemPerList - visibleCount);
        }

        final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());

        html.setFile(_dialogTemplate);

        html.replace("%npcTitle%", npc.getTitle());
        html.replace("%npcName%", npc.getName());

        html.replace("%menu%", _getMenuAction.execute());

        html.replace("%towns%", Objects.equals(parentAction, "Towns") ? _activeColor : "");
        html.replace("%villages%", Objects.equals(parentAction, "Villages") ? _activeColor : "");
        html.replace("%popular%", Objects.equals(parentAction, "Popular") ? _activeColor : "");
        html.replace("%recommended%", Objects.equals(parentAction, "Recommended") ? _activeColor : "");

        html.replace("%list%", locations.toString());
        html.replace("%heightIndent%", heightIndent);
        html.replace("%parentAction%", Objects.equals(parentAction, "Popular") ? "Towns" : parentAction);

        player.sendPacket(html);
    }

    private String getTemplateItem(LocationHolder locationHolder, int listId) {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(_itemTemplate));
            String template = reader.readLine();

            Item item = ItemData.getInstance().getTemplate(locationHolder.getPriceId());
            StringTokenizer tokenizer = new StringTokenizer(item.getName());
            String itemName = tokenizer.nextToken();

            template = template.replace("%name%", locationHolder.getName());
            template = template.replace("%listId%", String.valueOf(listId));
            template = template.replace("%id%", String.valueOf(locationHolder.getId()));
            template = template.replace("%point%", String.valueOf(locationHolder.getPoint()));
            template = template.replace("%priceCount%", String.valueOf(locationHolder.getPriceCount()));
            template = template.replace("%itemName%", itemName);

            return template;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
