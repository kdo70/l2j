package net.sf.l2j.gameserver.expander.cards.actions.hunting;

import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.expander.cards.data.enums.CardsTypeEnum;
import net.sf.l2j.gameserver.expander.cards.data.xml.HuntingCardData;
import net.sf.l2j.gameserver.expander.cards.model.holder.CardLvlHolder;
import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.helpers.data.xml.ItemIconData;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.kind.Item;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GetRewardsAction extends Action {
    protected final String _itemTemplate = "data/html/script/feature/cards/hunting/reward-item.htm";
    private static final int _itemPerPage = 6;
    private static final int _lastItemIndent = 2;

    public String execute(Player player) {
        StringBuilder list = new StringBuilder();
        CharacterCardHolder characterCardHolder = player.getCards().get(CardsTypeEnum.MONSTER.getName());

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(_itemTemplate))) {
            String template = reader.readLine();

            for (int index = 1; index < _itemPerPage; index++) {
                list.append(getTemplateItem(template, characterCardHolder, index));
            }

        } catch (IOException e) {
            throw new IllegalStateException("Error in forming the reward list", e);
        }

        return list.toString();
    }

    private String getTemplateItem(String template, CharacterCardHolder characterCardHolder, int index) {
        final int cardsMaxOffset = HuntingCardData.getInstance().getCards().size() - _itemPerPage;
        int characterCardLvl = Math.min(characterCardHolder.getLevel(), cardsMaxOffset);

        CardLvlHolder nextCard = HuntingCardData.getInstance().getCard(characterCardLvl + index);
        Item item = ItemData.getInstance().getTemplate(nextCard.getItemId());

        template = template.replace("%cardLvl%", getCardLvl(nextCard));
        template = template.replace("%itemIcon%", ItemIconData.getIcon(item.getItemId()));
        template = template.replace("%itemName%", item.getName());
        template = template.replace("%itemDesc%", ItemIconData.getDesc(item.getItemId()));
        template = template.replace("%itemCount%", Integer.toString(nextCard.getItemCount()));

        if (index != _itemPerPage - 1) {
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
