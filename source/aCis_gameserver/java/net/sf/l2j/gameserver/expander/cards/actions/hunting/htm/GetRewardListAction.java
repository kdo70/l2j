package net.sf.l2j.gameserver.expander.cards.actions.hunting.htm;

import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.expander.cards.data.xml.HuntingCardData;
import net.sf.l2j.gameserver.expander.cards.model.holder.CardLvlHolder;
import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.helpers.data.xml.ItemIconData;
import net.sf.l2j.gameserver.model.item.kind.Item;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GetRewardListAction extends Action {
    protected final String _template = "data/html/script/feature/cards/hunting/reward-item.htm";
    private static final int _pageSize = 6;
    private static final int _lastItemIndent = 2;

    public String execute(CharacterCardHolder card) {
        StringBuilder list = new StringBuilder();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(_template))) {
            String template = reader.readLine();

            for (int index = 1; index < _pageSize; index++) {
                list.append(getTemplateItem(template, card, index));
            }

            return list.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Error in forming the reward list", e);
        }
    }

    private String getTemplateItem(String template, CharacterCardHolder card, int index) {
        final int cardsMaxOffset = HuntingCardData.getInstance().getList().size() - _pageSize;
        int characterCardLvl = Math.min(card.getRewardLvl(), cardsMaxOffset);

        CardLvlHolder nextCard = HuntingCardData.getInstance().getLvl(characterCardLvl + index);
        Item item = ItemData.getInstance().getTemplate(nextCard.getItemId());

        boolean colorCondition = card.getRewardLvl() + 1 == nextCard.getLvl() && nextCard.getLvl() <= card.getLvl();
        String buttonColor = colorCondition ? "6697FF" : "B09979";

        template = template.replace("%cardLvl%", Integer.toString(nextCard.getLvl()));
        template = template.replace("%cardLvlTxt%", getCardLvl(nextCard));
        template = template.replace("%itemIcon%", ItemIconData.getIcon(item.getItemId()));
        template = template.replace("%itemName%", item.getName());
        template = template.replace("%itemDesc%", ItemIconData.getDesc(item.getItemId()));
        template = template.replace("%itemCount%", Integer.toString(nextCard.getItemCount()));
        template = template.replace("%buttonColor%", buttonColor);

        if (index != _pageSize - 1) {
            template = template.replace("%indent%", Integer.toString(_lastItemIndent));
        }

        return template;
    }

    private String getCardLvl(CardLvlHolder nextCard) {
        String cardLvl;

        if (nextCard.getLvl() < 10) {
            cardLvl = "&nbsp;&nbsp; " + nextCard.getLvl();
        } else {
            cardLvl = " " + nextCard.getLvl();
        }

        return cardLvl;
    }
}
