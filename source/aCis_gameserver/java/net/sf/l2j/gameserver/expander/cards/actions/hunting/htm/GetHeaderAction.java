package net.sf.l2j.gameserver.expander.cards.actions.hunting.htm;

import net.sf.l2j.gameserver.expander.cards.model.holder.CharacterCardHolder;
import net.sf.l2j.gameserver.expander.common.actions.Action;
import net.sf.l2j.gameserver.expander.helpers.Str;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GetHeaderAction extends Action {
    protected static final String _template = "data/html/script/feature/cards/hunting/header.htm";

    public String execute(CharacterCardHolder card) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(_template))) {
            String template = reader.readLine();

            template = template.replace("%skillPoints%", Str.numFormat(card.getSp()));
            template = template.replace("%hunterPoints%", Str.numFormat(card.getPoints()));

            return template;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
