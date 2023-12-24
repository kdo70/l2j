package net.sf.l2j.gameserver.expander.gatekeeper.conditions;

import net.sf.l2j.gameserver.expander.common.conditions.Condition;
import net.sf.l2j.gameserver.expander.gatekeeper.model.holder.LocationHolder;

import java.util.Map;

public class PopularCondition extends Condition {
    final int _listSize = 50;

    public boolean execute(Map<Integer, Integer> _statistics, LocationHolder locationHolder, int size) {
        final int locationId = locationHolder.getId();
        final boolean existsInList = _statistics.get(locationId) != null && _statistics.get(locationId) != 0;

        return existsInList && locationHolder.getCastleId() == 0 && size < _listSize;
    }
}
