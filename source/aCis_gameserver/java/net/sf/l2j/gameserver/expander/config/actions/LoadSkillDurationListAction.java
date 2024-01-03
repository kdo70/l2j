package net.sf.l2j.gameserver.expander.config.actions;

import net.sf.l2j.commons.config.ExProperties;
import net.sf.l2j.gameserver.expander.common.actions.Action;

import java.util.HashMap;

public class LoadSkillDurationListAction extends Action {
    public HashMap<Integer, Integer> execute(ExProperties players) {
        HashMap<Integer, Integer> skillDurationList = new HashMap<>();

        String[] propertySplit = players.getProperty("SkillDurationList", "").split(";");

        for (String skill : propertySplit) {
            String[] skillSplit = skill.split(",");
            skillDurationList.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
        }

        return skillDurationList;
    }
}
