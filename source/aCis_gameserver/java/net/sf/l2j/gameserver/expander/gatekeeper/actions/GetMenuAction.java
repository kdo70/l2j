package net.sf.l2j.gameserver.expander.gatekeeper.actions;

import net.sf.l2j.gameserver.expander.common.actions.Action;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GetMenuAction extends Action {
    protected final String _template = "data/html/script/feature/gatekeeper/menu.htm";

    public String execute() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(_template));

            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
