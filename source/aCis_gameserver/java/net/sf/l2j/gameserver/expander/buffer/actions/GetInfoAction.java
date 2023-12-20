package net.sf.l2j.gameserver.expander.buffer.actions;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.gameserver.expander.buffer.data.xml.BuffsByClassData;
import net.sf.l2j.gameserver.expander.buffer.data.xml.BuffsCommonData;
import net.sf.l2j.gameserver.expander.buffer.model.holder.BuffHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.helpers.data.xml.SkillInfoData;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class GetInfoAction extends Action {
    protected final String _itemTemplate = "data/html/script/feature/buffer/templates/item.htm";
    protected final String _paginationTemplate = "data/html/script/feature/buffer/templates/pagination.htm";
    protected final String _infoTemplate = "data/html/script/feature/buffer/templates/info.htm";
    protected final int _itemPerPage = 8;

    public void execute(Player player, Npc npc, int page) {
        final StringBuilder list = new StringBuilder();

        final List<BuffHolder> buffHolderList = BuffsByClassData.getInstance().getValidBuffs(player.isMystic());
        buffHolderList.addAll(BuffsCommonData.getInstance().getBuffs());

        int currentPage = 1;
        int iteration = 0;
        int itemInPage = 0;
        boolean hasMore = false;

        for (BuffHolder buffHolder : buffHolderList) {
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

            StringUtil.append(list, getTemplateItem(buffHolder));
            itemInPage++;
        }

        StringUtil.append(list, getTemplatePagination(page, hasMore));

        final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
        html.setFile(_infoTemplate);

        html.replace("%list%", list.toString());
        html.replace("%npcTitle%", npc.getTitle());
        html.replace("%npcName%", npc.getName());
        html.replace("%objectId%", npc.getObjectId());

        player.sendPacket(html);
    }


    private String getTemplateItem(BuffHolder buffHolder) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(_itemTemplate));
            String templateItem = reader.readLine();

            String onlyNightTxt = "Доступно только ночью";
            String availableLvlTxt = "Доступно с " + buffHolder.getMinLvl() + " уровня";
            String available = buffHolder.isOnlyNight() ? onlyNightTxt : availableLvlTxt;

            final int skillId = buffHolder.getSkill().getId();
            final int skillLvl = buffHolder.getSkill().getLevel();

            templateItem = templateItem.replace("%skillIcon%", SkillInfoData.getIco(skillId, skillLvl));
            templateItem = templateItem.replace("%skillName%", SkillInfoData.getName(skillId, skillLvl));
            templateItem = templateItem.replace("%skillLvl%", String.valueOf(skillLvl));
            templateItem = templateItem.replace("%available%", available);
            templateItem = templateItem.replace("%desc%", SkillInfoData.getDesc(skillId, skillLvl));

            return templateItem;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTemplatePagination(int page, boolean hasMore) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(_paginationTemplate));
            String pagination = reader.readLine();

            pagination = pagination.replace("%prevPage%", String.valueOf(page - (page > 1 ? 1 : 0)));
            pagination = pagination.replace("%currentPage%", String.valueOf(page));
            pagination = pagination.replace("%nextPage%", String.valueOf(page + (hasMore ? 1 : 0)));

            return pagination;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
