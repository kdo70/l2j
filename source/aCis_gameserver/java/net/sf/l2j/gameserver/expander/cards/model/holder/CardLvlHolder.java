package net.sf.l2j.gameserver.expander.cards.model.holder;

import net.sf.l2j.commons.data.StatSet;

public class CardLvlHolder {
    private final int _lvl;
    private final int _exp;
    private final int _skillId;
    private final int _skillLvl;
    private final int _itemId;
    private final int _itemCount;

    public CardLvlHolder(StatSet set) {
        _lvl = set.getInteger("lvl");
        _exp = set.getInteger("exp");
        _skillId = set.getInteger("skillId");
        _skillLvl = set.getInteger("skillLvl");
        _itemId = set.getInteger("itemId");
        _itemCount = set.getInteger("itemCount");
    }

    public int getLvl() {
        return _lvl;
    }

    public int getExp() {
        return _exp;
    }

    public int getSkillId() {
        return _skillId;
    }

    public int getSkillLvl() {
        return _skillLvl;
    }

    public int getItemId() {
        return _itemId;
    }

    public int getItemCount() {
        return _itemCount;
    }
}