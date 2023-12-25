package net.sf.l2j.gameserver.expander.gatekeeper.model.holder;

import net.sf.l2j.commons.data.StatSet;

public class MenuHolder {
    private final int _locId;
    private final int _childId;
    private final String _placeholder;
    private final String _desc;

    public MenuHolder(StatSet set) {
        _locId = set.getInteger("locId");
        _childId = set.getInteger("childId", 0);
        _placeholder = set.getString("placeholder", "-");
        _desc = set.getString("desc", "-");
    }

    public int getLocId() {
        return _locId;
    }

    public int getChildId() {
        return _childId;
    }

    public String getPlaceholder() {
        return _placeholder;
    }

    public String getDesc() {
        return _desc;
    }
}