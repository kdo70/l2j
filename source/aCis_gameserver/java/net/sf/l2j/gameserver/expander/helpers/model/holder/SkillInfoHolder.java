package net.sf.l2j.gameserver.expander.helpers.model.holder;

import net.sf.l2j.commons.data.StatSet;

public class SkillInfoHolder {
    private final int _id;
    private final int _lvl;
    private final String _name;
    private final String _desc;
    private final String _desc_add1;
    private final String _desc_add2;
    private final String _ico;

    public SkillInfoHolder(StatSet set) {
        _id = set.getInteger("id");
        _lvl = set.getInteger("lvl");
        _name = set.getString("name");
        _desc = set.getString("desc");
        _desc_add1 = set.getString("desc_add1");
        _desc_add2 = set.getString("desc_add2");
        _ico = set.getString("ico");
    }

    public int getId() {
        return _id;
    }

    public int getLvl() {
        return _lvl;
    }

    public String getName() {
        return _name;
    }

    public String getDesc() {
        return _desc;
    }

    public String getDescAdd1() {
        return _desc_add1;
    }

    public String getDescAdd2() {
        return _desc_add2;
    }

    public String getIco() {
        return _ico;
    }
}
