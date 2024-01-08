package net.sf.l2j.gameserver.expander.cards.actions.hunting.htm;

import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GetRewardsAction extends Action {
    protected static final String _template = "data/html/script/feature/cards/hunting/rewards.htm";
    protected static final GetRewardListAction _getRewardListAction = new GetRewardListAction();

    public String execute(CharacterCardHolder card) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(_template))) {
            String template = reader.readLine();

            template = template.replace("%rewardList%", _getRewardListAction.execute(card));

            return template;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
