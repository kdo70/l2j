package net.sf.l2j.gameserver.expander.helpers.data.xml;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.gameserver.expander.helpers.model.holder.SkillInfoHolder;
import org.w3c.dom.Document;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SkillInfoData implements IXmlReader {
    private static final Map<Integer, Map<Integer, SkillInfoHolder>> _skills = new ConcurrentHashMap<>();

    public static SkillInfoData getInstance() {
        return SingletonHolder._instance;
    }

    protected SkillInfoData() {
        load();
    }

    @Override
    public void load() {
        parseFile("./data/xml/expander/Helpers/skills.xml");
        LOGGER.info("Loaded {} skills info.", _skills.size());
    }

    @Override
    public void parseDocument(Document doc, Path path) {
        forEach(doc, "list", listNode -> forEach(listNode, "skill", skillData -> {
            final StatSet attributes = parseAttributes(skillData);
            final SkillInfoHolder skillInfoHolder = new SkillInfoHolder(attributes);

            final int skillId = attributes.getInteger("id");
            final int skillLvl = attributes.getInteger("lvl");

            if (_skills.get(skillId) == null) {
                Map<Integer, SkillInfoHolder> map = new ConcurrentHashMap<>();
                map.put(skillLvl, skillInfoHolder);

                _skills.put(skillId, map);
            } else {
                _skills.get(skillId).put(skillLvl, skillInfoHolder);
            }
        }));
    }

    public static int getId(int id, int lvl) {
        return _skills.get(id).get(lvl).getId();
    }

    public static int getLvl(int id, int lvl) {
        return _skills.get(id).get(lvl).getLvl();
    }

    public static String getName(int id, int lvl) {
        return _skills.get(id).get(lvl).getName();
    }

    public static String getDesc(int id, int lvl) {
        return _skills.get(id).get(lvl).getDesc();
    }

    public static String getDescAdd1(int id, int lvl) {
        return _skills.get(id).get(lvl).getDescAdd1();
    }

    public static String getDescAdd2(int id, int lvl) {
        return _skills.get(id).get(lvl).getDescAdd2();
    }

    public static String getIco(int id, int lvl) {
        return _skills.get(id).get(lvl).getIco();
    }

    private static class SingletonHolder {
        protected static final SkillInfoData _instance = new SkillInfoData();
    }
}
