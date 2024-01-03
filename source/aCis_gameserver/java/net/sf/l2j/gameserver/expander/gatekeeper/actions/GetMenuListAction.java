package net.sf.l2j.gameserver.expander.gatekeeper.actions;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.gatekeeper.calculators.PriceCalculator;
import net.sf.l2j.gameserver.expander.gatekeeper.data.dto.GatekeeperActionDto;
import net.sf.l2j.gameserver.expander.gatekeeper.data.dto.GatekeeperHtmlDto;
import net.sf.l2j.gameserver.expander.gatekeeper.data.xml.LocationsData;
import net.sf.l2j.gameserver.expander.gatekeeper.data.xml.MenuData;
import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.LocationHolder;
import net.sf.l2j.gameserver.expander.helpers.Str;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GetMenuListAction extends Action {
    protected final GetMenuAction _getMenuAction = new GetMenuAction();
    protected final PriceCalculator _priceCalculator = new PriceCalculator();
    protected final String _dialogTemplate = "data/html/script/feature/gatekeeper/towns.htm";
    protected final String _itemTemplate = "data/html/script/feature/gatekeeper/town-item.htm";
    protected final String _paginationTemplate = "data/html/script/feature/gatekeeper/pagination.htm";
    protected final String _activeColor = "color=B09878";
    protected final int _itemPerPage = Config.TELEPORT_MENU_ITEM_PER_PAGE;
    protected final int _heightIndentPerItem = Config.TELEPORT_MENU_HEIGHT_INDENT_PER_ITEM;

    public void execute(Player player, Npc npc, int listId, GatekeeperActionDto actionDto) {
        final Map<Integer, LocationHolder> locations = MenuData.getInstance().getList(listId);
        final StringBuilder list = new StringBuilder();

        int currentPage = 1;
        int itemInPage = 0;
        boolean hasMore = false;
        final int page = actionDto.getPage();

        List<LocationHolder> filteredList = getFilteredList(locations);

        for (LocationHolder location : filteredList) {
            if (currentPage != page) {
                if (++itemInPage > _itemPerPage) {
                    hasMore = true;
                    break;
                }
                continue;
            }

            list.append(buildList(player, actionDto, listId, location));
            if (++itemInPage > _itemPerPage) {
                hasMore = true;
                break;
            }
        }

        GatekeeperHtmlDto htmlDto = new GatekeeperHtmlDto(itemInPage, list, hasMore, currentPage, listId, filteredList);
        player.sendPacket(getHtml(actionDto, npc, htmlDto));
    }

    private List<LocationHolder> getFilteredList(Map<Integer, LocationHolder> locations) {
        Comparator<LocationHolder> comparator = Comparator.comparing(LocationHolder::getId, Comparator.naturalOrder());
        return locations.values().stream().sorted(comparator).toList();
    }

    private NpcHtmlMessage getHtml(GatekeeperActionDto actionDto, Npc npc, GatekeeperHtmlDto htmlDto) {
        int heightIndent = 0;
        if (htmlDto.getItemInPage() < _itemPerPage) {
            heightIndent += _heightIndentPerItem * (_itemPerPage - htmlDto.getItemInPage());
        }

        final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());

        html.setFile(_dialogTemplate);

        html.replace("%npcTitle%", npc.getTitle());
        html.replace("%npcName%", npc.getName());

        html.replace("%list%", htmlDto.getList().toString());
        html.replace("%menu%", _getMenuAction.execute());

        html.replace("%towns%", Objects.equals(actionDto.getAction(), "Towns") ? _activeColor : "");
        html.replace("%villages%", Objects.equals(actionDto.getAction(), "Villages") ? _activeColor : "");
        html.replace("%popular%", Objects.equals(actionDto.getAction(), "Popular") ? _activeColor : "");
        html.replace("%recommended%", Objects.equals(actionDto.getAction(), "Recommended") ? _activeColor : "");

        if (htmlDto.getHasMore() || htmlDto.getPage() > 1) {
            int itemCount = htmlDto.getFilteredList().size();
            final String pagination = getTemplatePagination(itemCount, actionDto, htmlDto.getListId(), htmlDto.getHasMore());
            html.replace("%pagination%", pagination);
        } else {
            heightIndent += 20;
            html.replace("%pagination%", "");
        }

        html.replace("%heightIndent%", heightIndent);

        return html;
    }

    private String buildList(Player player, GatekeeperActionDto dto, int listId, LocationHolder location) {
        try (BufferedReader reader = new BufferedReader(new FileReader(_itemTemplate))) {
            String template = reader.readLine();
            return getTemplateItem(player, template, dto.getAction(), listId, location);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTemplateItem(Player player, String template, String action, int listId, LocationHolder location) {
        final int priceCount = _priceCalculator.execute(player, location);

        Item item = ItemData.getInstance().getTemplate(location.getPriceId());
        StringTokenizer tokenizer = new StringTokenizer(item.getName());
        String itemName = tokenizer.nextToken();

        template = template.replace("%listId%", Integer.toString(listId));
        template = template.replace("%id%", Integer.toString(location.getId()));
        template = template.replace("%childId%", Integer.toString(location.getChildId()));
        template = template.replace("%name%", String.valueOf(location.getName()));
        template = template.replace("%priceCount%", Str.number(priceCount));
        template = template.replace("%itemName%", itemName);
        template = template.replace("%desc%", String.valueOf(location.getDesc()));
        template = template.replace("%parentAction%", action);

        return template;
    }

    private String getTemplatePagination(int count, GatekeeperActionDto actionDto, int listId, boolean hasMore) {
        try (BufferedReader reader = new BufferedReader(new FileReader(_paginationTemplate))) {
            final int page = actionDto.getPage();
            final int pageCount = (int) Math.ceil((double) count / _itemPerPage);

            String pagination = reader.readLine();

            pagination = pagination.replace("%prevPage%", Integer.toString(page - (page > 1 ? 1 : 0)));
            pagination = pagination.replace("%currentPage%", Integer.toString(page));
            pagination = pagination.replace("%nextPage%", Integer.toString(page + (hasMore ? 1 : 0)));
            pagination = pagination.replace("%listId%", Integer.toString(listId));
            pagination = pagination.replace("%parentAction%", actionDto.getParentAction());
            pagination = pagination.replace("%action%", actionDto.getParentAction());
            pagination = pagination.replace("%pageCount%", Integer.toString(pageCount));

            return pagination;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
