package net.sf.l2j.gameserver.expander.cards.model.holder;

public class CharacterCardHolder {
    protected final String _type;
    protected int _lvl;
    protected int _exp;
    protected int _points;
    protected int _sp;
    protected int _rewardLvl;

    public CharacterCardHolder(String type, int lvl, int exp, int points, int sp, int rewardLvl) {
        _type = type;
        _lvl = lvl;
        _exp = exp;
        _points = points;
        _sp = sp;
        _rewardLvl = rewardLvl;
    }

    public String getType() {
        return _type;
    }

    public int getLvl() {
        return _lvl;
    }

    public void setLvl(int lvl) {
        _lvl = lvl;
    }

    public int getExp() {
        return _exp;
    }

    public void setExp(int exp) {
        _exp = exp;
    }

    public void setPoints(int points) {
        _points = points;
    }

    public int getPoints() {
        return _points;
    }

    public int getSp() {
        return _sp;
    }

    public void setSp(int sp) {
        _sp = sp;
    }

    public int getRewardLvl() {
        return _rewardLvl;
    }

    public void setRewardLvl(int rewardLvl) {
        _rewardLvl = rewardLvl;
    }
}