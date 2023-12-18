package net.sf.l2j.gameserver.expander.config.actions;

import net.sf.l2j.commons.config.ExProperties;
import net.sf.l2j.gameserver.expander.common.actions.Action;

import java.util.HashMap;

public class LoadSkillDurationListAction extends Action {
    private final HashMap<Integer, Integer> _skillDurationList = new HashMap<>();

    public HashMap<Integer, Integer> execute(ExProperties players) {
        String[] propertySplit = players.getProperty("SkillDurationList", "").split(";");

        for (String skill : propertySplit) {
            String[] skillSplit = skill.split(",");
            _skillDurationList.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
        }

        return _skillDurationList;
    }
}
