package net.sf.l2j.gameserver.expander.buffer.actions;

import net.sf.l2j.Config;
import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.expander.buffer.calculators.BuffPriceCalculator;
import net.sf.l2j.gameserver.expander.buffer.conditions.VisibleBuffCondition;
import net.sf.l2j.gameserver.expander.buffer.data.xml.BuffsCommonData;
import net.sf.l2j.gameserver.expander.buffer.model.holder.BuffHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.helpers.data.xml.SkillInfoData;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.container.player.custom.helpers.Str;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

public class GetListAction extends Action {
    protected final VisibleBuffCondition _visibleBuffCondition = new VisibleBuffCondition();
    protected final BuffPriceCalculator _buffPriceCalculator = new BuffPriceCalculator();
    protected final String _listTemplate = "data/html/script/feature/buffer/templates/list.htm";
    protected final String _itemTemplate = "data/html/script/feature/buffer/templates/list-item.htm";
    protected final String _paginationTemplate = "data/html/script/feature/buffer/templates/pagination.htm";
    protected final List<BuffHolder> _buffList = BuffsCommonData.getInstance().getBuffs();
    protected final int _itemPerPage = Config.BUFFER_LIST_ITEM_PEG_PAGE;
    protected final int _heightIndentPerItem = Config.BUFFER_LIST_HEIGHT_INDENT_PER_ITEM;
    protected final int _minHeightIndent = Config.BUFFER_LIST_MIN_HEIGHT_INDENT;

    public String execute(Player player, Npc npc, int page) {
        final StringBuilder list = new StringBuilder();
        int currentPage = 1;
        int iteration = 0;
        int itemInPage = 0;
        int count = 0;
        boolean hasMore = false;

        for (int index = 0; index < _buffList.size(); index++) {
            final BuffHolder buffHolder = _buffList.get(index);

            if (!_visibleBuffCondition.execute(player, buffHolder)) {
                continue;
            }

            count++;

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

            StringUtil.append(list, getTemplateItem(player, buffHolder, index, page));

            itemInPage++;
        }

        int heightIndent = _minHeightIndent;
        if (itemInPage < _itemPerPage) {
            heightIndent += _heightIndentPerItem * (_itemPerPage - itemInPage);
        }

        final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
        html.setFile(_listTemplate);

        html.replace("%list%", list.toString());
        html.replace("%npcTitle%", npc.getTitle());
        html.replace("%npcName%", npc.getName());
        html.replace("%objectId%", npc.getObjectId());

        if (hasMore || page > 1) {
            html.replace("%pagination%", getTemplatePagination(count, page, hasMore));
        } else {
            html.replace("%pagination%", "");
            heightIndent += 24;
        }

        html.replace("%heightIndent%", heightIndent);

        player.sendPacket(html);

        return null;
    }

    private String getTemplateItem(Player player, BuffHolder buffHolder, int index, int page) {
        Item item = ItemData.getInstance().getTemplate(buffHolder.getPriceId());
        StringTokenizer tokenizer = new StringTokenizer(item.getName());
        String itemName = tokenizer.nextToken();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(_itemTemplate));
            String template = reader.readLine();
            String price = Str.number(_buffPriceCalculator.execute(player, buffHolder));

            final int skillId = buffHolder.getSkill().getId();
            final int skillLvl = buffHolder.getSkill().getLevel();

            template = template.replace("%skillIcon%", SkillInfoData.getIco(skillId, skillLvl));
            template = template.replace("%skillName%", SkillInfoData.getName(skillId, skillLvl));
            template = template.replace("%price%", price);
            template = template.replace("%itemName%", itemName);
            template = template.replace("%index%", String.valueOf(index));
            template = template.replace("%page%", String.valueOf(page));

            return template;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTemplatePagination(int count, int page, boolean hasMore) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(_paginationTemplate));
            final int pageCount = (int) Math.ceil((double) count / _itemPerPage);
            String pagination = reader.readLine();

            pagination = pagination.replace("%prevPage%", String.valueOf(page - (page > 1 ? 1 : 0)));
            pagination = pagination.replace("%currentPage%", String.valueOf(page));
            pagination = pagination.replace("%nextPage%", String.valueOf(page + (hasMore ? 1 : 0)));
            pagination = pagination.replace("%action%", "List");
            pagination = pagination.replace("%pageCount%", String.valueOf(pageCount));

            return pagination;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
