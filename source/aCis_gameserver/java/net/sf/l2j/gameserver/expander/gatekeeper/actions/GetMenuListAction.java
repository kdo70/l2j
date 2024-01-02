package net.sf.l2j.gameserver.expander.gatekeeper.actions;

import net.sf.l2j.Config;
import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.gatekeeper.data.dto.GatekeeperData;
import net.sf.l2j.gameserver.expander.gatekeeper.data.xml.MenuData;
import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.LocationHolder;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GetMenuListAction extends Action {
    protected final GetMenuAction _getMenuAction = new GetMenuAction();
    protected final String _dialogTemplate = "data/html/script/feature/gatekeeper/towns.htm";
    protected final String _itemTemplate = "data/html/script/feature/gatekeeper/town-item.htm";
    protected final String _paginationTemplate = "data/html/script/feature/gatekeeper/pagination.htm";
    protected final String _activeColor = "color=B09878";
    protected final int _itemPerPage = Config.TELEPORT_MENU_ITEM_PER_PAGE;
    protected final int _heightIndentPerItem = Config.TELEPORT_MENU_HEIGHT_INDENT_PER_ITEM;

    public void execute(Player player, Npc npc, int listId, GatekeeperData data) {
        final Map<Integer, LocationHolder> list = MenuData.getInstance().getList(listId);
        final StringBuilder locations = new StringBuilder();

        int currentPage = 1;
        int iteration = 0;
        int itemInPage = 0;
        boolean hasMore = false;
        int heightIndent = 0;

        Comparator<LocationHolder> comparator = Comparator.comparing(LocationHolder::getId, Comparator.naturalOrder());

        final int page = data.getPage();
        List<LocationHolder> filteredList = list.values().stream().sorted(comparator).toList();
        for (LocationHolder location : filteredList) {
            if (currentPage != page) {
                iteration++;

                if (iteration != _itemPerPage) {
                    continue;
                }

                currentPage++;
                iteration = 0;

                continue;
            }

            if (itemInPage == _itemPerPage) {
                hasMore = true;
                break;
            }

            StringUtil.append(locations, getTemplateItem(data.getAction(), listId, location));

            itemInPage++;
        }

        if (itemInPage < _itemPerPage) {
            heightIndent += _heightIndentPerItem * (_itemPerPage - itemInPage);
        }

        final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());

        html.setFile(_dialogTemplate);

        html.replace("%npcTitle%", npc.getTitle());
        html.replace("%npcName%", npc.getName());

        html.replace("%list%", locations.toString());
        html.replace("%menu%", _getMenuAction.execute());

        html.replace("%towns%", Objects.equals(data.getAction(), "Towns") ? _activeColor : "");
        html.replace("%villages%", Objects.equals(data.getAction(), "Villages") ? _activeColor : "");
        html.replace("%popular%", Objects.equals(data.getAction(), "Popular") ? _activeColor : "");
        html.replace("%recommended%", Objects.equals(data.getAction(), "Recommended") ? _activeColor : "");

        if (hasMore || page > 1) {
            int itemCount = filteredList.size();
            html.replace("%pagination%", getTemplatePagination(itemCount, data, listId, hasMore));
        } else {
            heightIndent += 20;
            html.replace("%pagination%", "");
        }

        html.replace("%heightIndent%", heightIndent);

        player.sendPacket(html);
    }

    private String getTemplateItem(String action, int listId, LocationHolder location) {
        try (BufferedReader reader = new BufferedReader(new FileReader(_itemTemplate))) {
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

    private String getTemplatePagination(int count, GatekeeperData data, int listId, boolean hasMore) {
        try (BufferedReader reader = new BufferedReader(new FileReader(_paginationTemplate))) {
            final int page = data.getPage();
            final int pageCount = (int) Math.ceil((double) count / _itemPerPage);

            String pagination = reader.readLine();

            pagination = pagination.replace("%prevPage%", String.valueOf(page - (page > 1 ? 1 : 0)));
            pagination = pagination.replace("%currentPage%", String.valueOf(page));
            pagination = pagination.replace("%nextPage%", String.valueOf(page + (hasMore ? 1 : 0)));
            pagination = pagination.replace("%listId%", String.valueOf(listId));
            pagination = pagination.replace("%parentAction%", data.getParentAction());
            pagination = pagination.replace("%action%", data.getParentAction());
            pagination = pagination.replace("%pageCount%", String.valueOf(pageCount));

            return pagination;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
