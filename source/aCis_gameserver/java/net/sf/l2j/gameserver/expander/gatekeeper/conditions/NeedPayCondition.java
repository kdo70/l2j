package net.sf.l2j.gameserver.expander.gatekeeper.conditions;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.expander.common.conditions.Condition;
import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.LocationHolder;

public class NeedPayCondition extends Condition {
    public boolean execute(LocationHolder location) {
        return Config.FREE_TELEPORT || location.getPriceCount() == 0;
    }
}