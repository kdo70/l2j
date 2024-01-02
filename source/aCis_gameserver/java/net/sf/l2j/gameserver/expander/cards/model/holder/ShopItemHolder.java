package net.sf.l2j.gameserver.expander.cards.model.holder;

import net.sf.l2j.commons.data.StatSet;

public class ShopItemHolder {
    private final int _itemId;
    private final int _count;
    private final int _price;
    private int _limit;
    private final int _lvl;
    private int _soldCount = 0;

    public ShopItemHolder(StatSet set) {
        _itemId = set.getInteger("itemId");
        _count = set.getInteger("count");
        _price = set.getInteger("price");
        _limit = set.getInteger("limit");
        _lvl = set.getInteger("lvl");
    }

    public int getItemId() {
        return _itemId;
    }

    public int getCount() {
        return _count;
    }

    public int getPrice() {
        return _price;
    }

    public int getLimit() {
        return _limit;
    }

    public void setLimit(int limit) {
        _limit = limit;
    }

    public int getLvl() {
        return _lvl;
    }

    public int getSoldCount() {
        return _soldCount;
    }

    public void setSoldCount(int soldCount) {
        _soldCount = soldCount;
    }
}