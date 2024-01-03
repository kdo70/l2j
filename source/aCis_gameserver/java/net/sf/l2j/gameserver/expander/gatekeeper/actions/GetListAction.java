package net.sf.l2j.gameserver.expander.gatekeeper.actions;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.enums.TeleportType;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.common.actions.SendMsgAction;
import net.sf.l2j.gameserver.expander.gatekeeper.calculators.PriceCalculator;
import net.sf.l2j.gameserver.expander.gatekeeper.data.dto.GatekeeperActionDto;
import net.sf.l2j.gameserver.expander.gatekeeper.data.dto.GatekeeperHtmlDto;
import net.sf.l2j.gameserver.expander.gatekeeper.data.xml.MenuData;
import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.LocationHolder;
import net.sf.l2j.gameserver.expander.helpers.Str;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
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
    protected final int _popularListId = Config.TELEPORT_POPULAR_LIST_ID;

    public void execute(Player player, Npc npc, int listId, GatekeeperActionDto actionDto) {
        final Map<Integer, LocationHolder> locations = MenuData.getInstance().getList(listId);
        if (locations == null) {
            SendMsgAction.execute(player, "Выбранная вами локация недоступна для телепорта");
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        int currentPage = actionDto.getPage();
        int itemInPage = 0;
        boolean hasMore = false;

        final StringBuilder list = new StringBuilder();
        final List<LocationHolder> filteredList = getFilteredList(listId, locations, player);

        for (int index = (currentPage - 1) * _itemPerPage; index < filteredList.size(); index++) {
            if (itemInPage == _itemPerPage) {
                hasMore = true;
                break;
            }

            LocationHolder locationHolder = filteredList.get(index);
            list.append(buildList(player, locationHolder, listId));
            itemInPage++;
        }

        GatekeeperHtmlDto htmlDto = new GatekeeperHtmlDto(itemInPage, list, hasMore, currentPage, listId, filteredList);
        player.sendPacket(getHtml(actionDto, npc, htmlDto));
    }

    private List<LocationHolder> getFilteredList(int listId, Map<Integer, LocationHolder> locations, Player player) {
        Comparator<LocationHolder> comparator = Comparator.comparing(LocationHolder::getId, Comparator.naturalOrder());
        if (listId == _popularListId) {
            comparator = Comparator.comparing(LocationHolder::getTeleportCount, Comparator.reverseOrder());
        }
        Stream<LocationHolder> listStream = locations.values().stream();

        if (!player.isNoble()) {
            listStream = listStream.filter(locationHolder -> locationHolder.getType() != TeleportType.NOBLE);
        }

        return listStream.sorted(comparator).toList();
    }

    private NpcHtmlMessage getHtml(GatekeeperActionDto actionDto, Npc npc, GatekeeperHtmlDto htmlDto) {
        int heightIndent = _minHeightIndent;
        if (htmlDto.getItemInPage() < _itemPerPage) {
            heightIndent += _heightIndentPerItem * (_itemPerPage - htmlDto.getItemInPage());
        }

        final String parentAction = actionDto.getParentAction();
        final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());

        html.setFile(_dialogTemplate);

        html.replace("%npcTitle%", npc.getTitle());
        html.replace("%npcName%", npc.getName());

        html.replace("%menu%", _getMenuAction.execute());

        html.replace("%towns%", Objects.equals(parentAction, "Towns") ? _activeColor : "");
        html.replace("%villages%", Objects.equals(parentAction, "Villages") ? _activeColor : "");
        html.replace("%popular%", Objects.equals(parentAction, "Popular") ? _activeColor : "");
        html.replace("%recommended%", Objects.equals(parentAction, "Recommended") ? _activeColor : "");

        html.replace("%list%", htmlDto.getList().toString());

        if (htmlDto.getHasMore() || htmlDto.getPage() > 1) {
            int itemCount = htmlDto.getFilteredList().size();
            final String pagination = getTemplatePagination(itemCount, actionDto, htmlDto.getListId(), htmlDto.getHasMore());
            html.replace("%pagination%", pagination);
        } else {
            html.replace("%pagination%", "");
            heightIndent += 24;
        }

        html.replace("%heightIndent%", heightIndent);
        html.replace("%parentAction%", Objects.equals(parentAction, "Popular") ? "Towns" : parentAction);

        return html;
    }

    private String buildList(Player player, LocationHolder locationHolder, int listId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(_itemTemplate))) {
            String template = reader.readLine();

            return getTemplateItem(template, player, locationHolder, listId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTemplateItem(String template, Player player, LocationHolder locationHolder, int listId) {
        final int priceCount = _priceCalculator.execute(player, locationHolder);

        Item item = ItemData.getInstance().getTemplate(locationHolder.getPriceId());
        StringTokenizer tokenizer = new StringTokenizer(item.getName());
        String itemName = tokenizer.nextToken();

        template = template.replace("%name%", locationHolder.getName());
        template = template.replace("%listId%", String.valueOf(listId));
        template = template.replace("%id%", String.valueOf(locationHolder.getId()));
        template = template.replace("%point%", String.valueOf(locationHolder.getPoint()));
        template = template.replace("%priceCount%", Str.number(priceCount));
        template = template.replace("%itemName%", itemName);

        return template;
    }

    private String getTemplatePagination(int count, GatekeeperActionDto actionDto, int listId, boolean hasMore) {
        try (BufferedReader reader = new BufferedReader(new FileReader(_paginationTemplate))) {
            final int page = actionDto.getPage();
            final int pageCount = (int) Math.ceil((double) count / _itemPerPage);

            String pagination = reader.readLine();

            pagination = pagination.replace("%prevPage%", String.valueOf(page - (page > 1 ? 1 : 0)));
            pagination = pagination.replace("%currentPage%", String.valueOf(page));
            pagination = pagination.replace("%nextPage%", String.valueOf(page + (hasMore ? 1 : 0)));
            pagination = pagination.replace("%listId%", String.valueOf(listId));
            pagination = pagination.replace("%parentAction%", actionDto.getParentAction());
            pagination = pagination.replace("%action%", "List");
            pagination = pagination.replace("%pageCount%", String.valueOf(pageCount));

            return pagination;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
