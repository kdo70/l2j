package net.sf.l2j.gameserver.expander.gatekeeper.actions;

import net.sf.l2j.Config;
import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.enums.TeleportType;
import net.sf.l2j.gameserver.expander.gatekeeper.data.dto.GatekeeperData;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.gatekeeper.calculators.PriceCalculator;
import net.sf.l2j.gameserver.expander.gatekeeper.data.xml.MenuData;
import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.LocationHolder;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.expander.helpers.Str;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class GetListAction extends Action {
    protected final GetMenuAction _getMenuAction = new GetMenuAction();
    protected final PriceCalculator _priceCalculator = new PriceCalculator();
    protected final String _dialogTemplate = "data/html/script/feature/gatekeeper/list.htm";
    protected final String _itemTemplate = "data/html/script/feature/gatekeeper/list-item.htm";
    protected final String _paginationTemplate = "data/html/script/feature/gatekeeper/pagination.htm";
    protected final String _activeColor = "color=B09878";
    protected final int _itemPerPage = Config.TELEPORT_LIST_ITEM_PER_PAGE;
    protected final int _heightIndentPerItem = Config.TELEPORT_LIST_HEIGHT_INDENT_PER_ITEM;
    protected final int _minHeightIndent = Config.TELEPORT_LIST_MIN_HEIGHT_INDENT;

    public void execute(Player player, Npc npc, int listId, GatekeeperData data) {
        final Map<Integer, LocationHolder> list = MenuData.getInstance().getList(listId);
        if (list == null) {
            Str.sendMsg(player, "Выбранная вами локация недоступна для телепорта");

            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final StringBuilder locations = new StringBuilder();

        int currentPage = 1;
        int iteration = 0;
        int itemInPage = 0;
        boolean hasMore = false;

        Comparator<LocationHolder> comparator = Comparator.comparing(LocationHolder::getId, Comparator.naturalOrder());
        if (listId == 20) {
            comparator = Comparator.comparing(LocationHolder::getTeleportCount, Comparator.reverseOrder());
        }
        Stream<LocationHolder> listStream = list.values().stream();

        if (!player.isNoble()) {
            listStream = listStream.filter(locationHolder -> locationHolder.getType() != TeleportType.NOBLE);
        }

        final int page = data.getPage();
        List<LocationHolder> filteredList = listStream.sorted(comparator).toList();
        for (LocationHolder locationHolder : filteredList) {
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

            StringUtil.append(locations, getTemplateItem(player, locationHolder, listId));

            itemInPage++;
        }

        int heightIndent = _minHeightIndent;
        if (itemInPage < _itemPerPage) {
            heightIndent += _heightIndentPerItem * (_itemPerPage - itemInPage);
        }

        final String parentAction = data.getParentAction();
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

        if (hasMore || page > 1) {
            int itemCount = filteredList.size();
            html.replace("%pagination%", getTemplatePagination(itemCount, data, listId, hasMore));
        } else {
            html.replace("%pagination%", "");
            heightIndent += 24;
        }

        html.replace("%heightIndent%", heightIndent);
        html.replace("%parentAction%", Objects.equals(parentAction, "Popular") ? "Towns" : parentAction);

        player.sendPacket(html);
    }

    private String getTemplateItem(Player player, LocationHolder locationHolder, int listId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(_itemTemplate))) {
            Item item = ItemData.getInstance().getTemplate(locationHolder.getPriceId());
            StringTokenizer tokenizer = new StringTokenizer(item.getName());
            String itemName = tokenizer.nextToken();
            final int priceCount = _priceCalculator.execute(player, locationHolder);

            String template = reader.readLine();

            template = template.replace("%name%", locationHolder.getName());
            template = template.replace("%listId%", String.valueOf(listId));
            template = template.replace("%id%", String.valueOf(locationHolder.getId()));
            template = template.replace("%point%", String.valueOf(locationHolder.getPoint()));
            template = template.replace("%priceCount%", String.valueOf(priceCount));
            template = template.replace("%itemName%", itemName);

            return template;
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
            pagination = pagination.replace("%action%", "List");
            pagination = pagination.replace("%pageCount%", String.valueOf(pageCount));

            return pagination;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
