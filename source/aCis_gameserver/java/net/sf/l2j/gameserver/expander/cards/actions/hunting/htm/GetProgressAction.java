package net.sf.l2j.gameserver.expander.cards.actions.hunting.htm;

import net.sf.l2j.gameserver.expander.cards.data.xml.HuntingCardData;
import net.sf.l2j.gameserver.expander.cards.model.holder.CardLvlHolder;
import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.helpers.Str;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GetProgressAction extends Action {
    protected static final String _template = "data/html/script/feature/cards/hunting/progress.htm";

    public String execute(CharacterCardHolder card) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(_template))) {
            String template = reader.readLine();

            template = template.replace("%lvl%", Integer.toString(card.getLvl()));
            template = template.replace("%percent%", getPercent(card));
            template = template.replace("%progress%", getProgress(card));

            return template;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private String getPercent(CharacterCardHolder card) {
        double percent = 0;

        if (card.getLvl() < HuntingCardData.getInstance().getList().size()) {
            CardLvlHolder nextCard = HuntingCardData.getInstance().getLvl(card.getLvl() + 1);
            percent = ((double) card.getExp() / (double) nextCard.getExp()) * 100;
        }

        return Str.percent(percent);
    }

    private String getProgress(CharacterCardHolder card) {
        double percent;
        double progress = 0;

        if (card.getLvl() < HuntingCardData.getInstance().getList().size()) {
            CardLvlHolder nextCard = HuntingCardData.getInstance().getLvl(card.getLvl() + 1);
            percent = ((double) card.getExp() / (double) nextCard.getExp()) * 100;
            progress = 2.85 * percent;
        }

        return Str.percent(progress);
    }
}
