package net.sf.l2j.gameserver.expander.cards.actions.hunting.htm;

import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.statistic.CharacterStatistic;
import net.sf.l2j.gameserver.model.actor.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GetStatsRightAction extends Action {
    protected static final String _template = "data/html/script/feature/cards/hunting/hunting-stats-right.htm";

    public String execute(Player player) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(_template))) {
            String template = reader.readLine();

            template = template.replace("%monsterKills%", CharacterStatistic.getMonsterKills(player));
            template = template.replace("%monsterKillsToday%", CharacterStatistic.getMonsterKillsToday(player));
            template = template.replace("%monsterDeaths%", CharacterStatistic.getMonsterDeaths(player));
            template = template.replace("%monsterDeathsToday%", CharacterStatistic.getMonsterDeathsToday(player));
            template = template.replace("%sp%", CharacterStatistic.getSp(player));
            template = template.replace("%spToday%", CharacterStatistic.getSpToday(player));

            return template;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
