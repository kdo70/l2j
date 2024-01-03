package net.sf.l2j.gameserver.expander.buffer.actions;

import net.sf.l2j.Config;
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
    protected int _itemPerPage = Config.BUFFER_INFO_ITEM_PEG_PAGE;

    public void execute(Player player, Npc npc, int page) {
        final StringBuilder list = new StringBuilder();

        final List<BuffHolder> buffs = BuffsByClassData.getInstance().getValidBuffs(player.isMystic());
        buffs.addAll(BuffsCommonData.getInstance().getBuffs());

        int startIndex = (page - 1) * _itemPerPage;
        int endIndex = Math.min(startIndex + _itemPerPage, buffs.size());

        for (int i = startIndex; i < endIndex; i++) {
            list.append(buildList(buffs.get(i)));
        }

        if (endIndex < buffs.size() || page > 1) {
            StringUtil.append(list, getTemplatePagination(buffs.size(), page, endIndex < buffs.size()));
        }

        player.sendPacket(getHtml(list, npc));
    }

    private NpcHtmlMessage getHtml(StringBuilder list, Npc npc) {
        final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
        html.setFile(_infoTemplate);

        html.replace("%list%", list.toString());
        html.replace("%npcTitle%", npc.getTitle());
        html.replace("%npcName%", npc.getName());
        html.replace("%objectId%", npc.getObjectId());

        return html;
    }

    private String buildList(BuffHolder buffHolder) {
        try (BufferedReader reader = new BufferedReader(new FileReader(_itemTemplate))) {
            String template = reader.readLine();

            return fillTemplateItem(template, buffHolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String fillTemplateItem(String template, BuffHolder buffHolder) {
        String onlyNightTxt = "Доступно только ночью";
        String availableLvlTxt = "Доступно с " + buffHolder.getMinLvl() + " уровня";
        String available = buffHolder.isOnlyNight() ? onlyNightTxt : availableLvlTxt;

        final int skillId = buffHolder.getSkill().getId();
        final int skillLvl = buffHolder.getSkill().getLevel();

        template = template.replace("%skillIcon%", SkillInfoData.getIco(skillId, skillLvl));
        template = template.replace("%skillName%", SkillInfoData.getName(skillId, skillLvl));
        template = template.replace("%skillLvl%", String.valueOf(skillLvl));
        template = template.replace("%available%", available);
        template = template.replace("%desc%", SkillInfoData.getDesc(skillId, skillLvl));

        return template;
    }

    private String getTemplatePagination(int count, int page, boolean hasMore) {
        try (BufferedReader reader = new BufferedReader(new FileReader(_paginationTemplate))) {
            final int pageCount = (int) Math.ceil((double) count / _itemPerPage);
            String pagination = reader.readLine();

            pagination = pagination.replace("%prevPage%", String.valueOf(page - (page > 1 ? 1 : 0)));
            pagination = pagination.replace("%currentPage%", String.valueOf(page));
            pagination = pagination.replace("%nextPage%", String.valueOf(page + (hasMore ? 1 : 0)));
            pagination = pagination.replace("%action%", "Info");
            pagination = pagination.replace("%pageCount%", String.valueOf(pageCount));

            return pagination;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
