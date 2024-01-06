package net.sf.l2j.gameserver.expander.cards.model.holder;

import net.sf.l2j.commons.data.StatSet;

public class CardLvlHolder {
    private final int _lvl;
    private final int _exp;
    private final int _itemId;
    private final int _itemCount;

    public CardLvlHolder(StatSet set) {
        _lvl = set.getInteger("lvl");
        _exp = set.getInteger("exp");
        _itemId = set.getInteger("itemId");
        _itemCount = set.getInteger("itemCount");
    }

    public int getLvl() {
        return _lvl;
    }

    public int getExp() {
        return _exp;
    }

    public int getItemId() {
        return _itemId;
    }

    public int getItemCount() {
        return _itemCount;
    }
}