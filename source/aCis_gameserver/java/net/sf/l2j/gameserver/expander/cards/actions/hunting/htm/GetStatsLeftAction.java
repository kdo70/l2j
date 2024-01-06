package net.sf.l2j.gameserver.expander.cards.actions.hunting.htm;

import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.statistic.CharacterStatistic;
import net.sf.l2j.gameserver.model.actor.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GetStatsLeftAction extends Action {
    protected static final String _statsLeftTemplate = "data/html/script/feature/cards/hunting/hunting-stats-left.htm";

    public String execute(Player player) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(_statsLeftTemplate))) {
            String template = reader.readLine();

            template = template.replace("%adena%", CharacterStatistic.getAdena(player));
            template = template.replace("%adena%", CharacterStatistic.getAdena(player));
            template = template.replace("%adenaToday%", CharacterStatistic.getAdenaToday(player));
            template = template.replace("%gold%", CharacterStatistic.getGold(player));
            template = template.replace("%goldToday%", CharacterStatistic.getGoldToday(player));
            template = template.replace("%items%", CharacterStatistic.getItems(player));
            template = template.replace("%itemsToday%", CharacterStatistic.getItemsToday(player));
            template = template.replace("%equip%", CharacterStatistic.getEquip(player));
            template = template.replace("%equipToday%", CharacterStatistic.getEquipToday(player));
            template = template.replace("%exp%", CharacterStatistic.getExp(player));
            template = template.replace("%expToday%", CharacterStatistic.getExpToday(player));


            return template;
        } catch (IOException e) {
            throw new IllegalStateException("Error in forming the reward list", e);
        }
    }
}
