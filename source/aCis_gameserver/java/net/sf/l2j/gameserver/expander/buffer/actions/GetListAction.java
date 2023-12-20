package net.sf.l2j.gameserver.expander.buffer.actions;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.helpers.data.xml.SkillInfoData;
import net.sf.l2j.gameserver.expander.buffer.calculators.BuffPriceCalculator;
import net.sf.l2j.gameserver.expander.buffer.conditions.VisibleBuffCondition;
import net.sf.l2j.gameserver.expander.buffer.data.xml.BuffsCommonData;
import net.sf.l2j.gameserver.expander.buffer.model.holder.BuffHolder;
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
    protected final String _tableListTemplate = "data/html/script/feature/buffer/templates/table.htm";
    protected final List<BuffHolder> _buffList = BuffsCommonData.getInstance().getBuffs();
    protected final int _minBuffsPerPage = 12;
    protected final int _heightIndentPerBuff = 24;
    protected final int _minHeightIndent = 12;

    public String execute(Player player, Npc npc) {
        int heightIndent = _minHeightIndent;
        final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
        final StringBuilder list = new StringBuilder();

        int visibleCount = 0;
        for (int index = 0; index < _buffList.size(); index++) {
            final BuffHolder buffHolder = _buffList.get(index);

            if (!_visibleBuffCondition.execute(player, buffHolder)) {
                continue;
            }

            Item item = ItemData.getInstance().getTemplate(buffHolder.getPriceId());
            StringTokenizer tokenizer = new StringTokenizer(item.getName());
            String itemName = tokenizer.nextToken();

            try {
                BufferedReader reader = new BufferedReader(new FileReader(_tableListTemplate));
                String template = reader.readLine();
                String price = Str.number(_buffPriceCalculator.execute(player, buffHolder));

                final int skillId = buffHolder.getSkill().getId();
                final int skillLvl = buffHolder.getSkill().getLevel();

                template = template.replace("%skillIcon%", SkillInfoData.getIco(skillId, skillLvl));
                template = template.replace("%skillName%", SkillInfoData.getName(skillId, skillLvl));
                template = template.replace("%price%", price);
                template = template.replace("%itemName%", itemName);
                template = template.replace("%index%", String.valueOf(index));

                StringUtil.append(list, template);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            visibleCount++;
        }

        if (visibleCount < _minBuffsPerPage) {
            heightIndent += _heightIndentPerBuff * (_minBuffsPerPage - visibleCount);
        }

        html.setFile(_listTemplate);

        html.replace("%list%", list.toString());
        html.replace("%npcTitle%", npc.getTitle());
        html.replace("%npcName%", npc.getName());
        html.replace("%objectId%", npc.getObjectId());
        html.replace("%heightIndent%", heightIndent);

        player.sendPacket(html);

        return null;
    }
}
