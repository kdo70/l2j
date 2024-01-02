package net.sf.l2j.gameserver;

import net.sf.l2j.Config;
import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.mmocore.SelectorConfig;
import net.sf.l2j.commons.mmocore.SelectorThread;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.commons.util.SysUtil;
import net.sf.l2j.gameserver.communitybbs.CommunityBoard;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.cache.CrestCache;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.manager.*;
import net.sf.l2j.gameserver.data.sql.BookmarkTable;
import net.sf.l2j.gameserver.data.sql.ClanTable;
import net.sf.l2j.gameserver.data.sql.PlayerInfoTable;
import net.sf.l2j.gameserver.data.sql.ServerMemoTable;
import net.sf.l2j.gameserver.data.xml.*;
import net.sf.l2j.gameserver.expander.buffer.data.xml.BuffsByClassData;
import net.sf.l2j.gameserver.expander.buffer.data.xml.BuffsCommonData;
import net.sf.l2j.gameserver.expander.gatekeeper.data.xml.LocationsData;
import net.sf.l2j.gameserver.expander.gatekeeper.data.xml.MenuData;
import net.sf.l2j.gameserver.expander.helpers.data.xml.ItemIconData;
import net.sf.l2j.gameserver.expander.helpers.data.xml.SkillInfoData;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.handler.*;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.expander.statistic.tasks.ResetDayStatisticTask;
import net.sf.l2j.gameserver.expander.cards.data.xml.ShopData;
import net.sf.l2j.gameserver.expander.cards.data.xml.HuntingCardData;
import net.sf.l2j.gameserver.model.boat.*;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.model.olympiad.OlympiadGameManager;
import net.sf.l2j.gameserver.network.GameClient;
import net.sf.l2j.gameserver.network.GamePacketHandler;
import net.sf.l2j.gameserver.taskmanager.*;
import net.sf.l2j.util.DeadLockDetector;
import net.sf.l2j.util.IPv4Filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.logging.LogManager;

public class GameServer {
    private static final CLogger LOGGER = new CLogger(GameServer.class.getName());

    private final SelectorThread<GameClient> _selectorThread;

    private static GameServer _gameServer;

    public static void main(String[] args) throws Exception {
        _gameServer = new GameServer();
    }

    public GameServer() throws Exception {
        // Create log folder
        new File("./log").mkdir();
        new File("./log/chat").mkdir();
        new File("./log/console").mkdir();
        new File("./log/error").mkdir();
        new File("./log/gm_audit").mkdir();
        new File("./log/item").mkdir();
        new File("./data/crests").mkdirs();

        // Create input stream for log file -- or store file data into memory
        try (InputStream is = new FileInputStream("config/logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        }

        StringUtil.printSection("Config");
        Config.loadGameServer();

        StringUtil.printSection("Poole's");
        ConnectionPool.init();
        ThreadPool.init();

        StringUtil.printSection("IdFactory");
        IdFactory.getInstance();

        StringUtil.printSection("Cache");
        HtmCache.getInstance();
        CrestCache.getInstance();

        StringUtil.printSection("World");
        World.getInstance();
        MapRegionData.getInstance();
        AnnouncementData.getInstance();
        ServerMemoTable.getInstance();

        StringUtil.printSection("Skills");
        SkillTable.getInstance();
        SkillTreeData.getInstance();

        StringUtil.printSection("Items");
        ItemData.getInstance();
        SummonItemData.getInstance();
        HennaData.getInstance();
        BuyListManager.getInstance();
        MultisellData.getInstance();
        RecipeData.getInstance();
        ArmorSetData.getInstance();
        FishData.getInstance();
        SpellbookData.getInstance();
        SoulCrystalData.getInstance();
        AugmentationData.getInstance();
        CursedWeaponManager.getInstance();

        StringUtil.printSection("Admins");
        AdminData.getInstance();
        BookmarkTable.getInstance();
        PetitionManager.getInstance();
        ItemIconData.getInstance();
        SkillInfoData.getInstance();

        StringUtil.printSection("Characters");
        PlayerData.getInstance();
        PlayerInfoTable.getInstance();
        PlayerLevelData.getInstance();
        HuntingCardData.getInstance();
        PartyMatchRoomManager.getInstance();
        RaidPointManager.getInstance();
        HealSpsData.getInstance();

        StringUtil.printSection("Community server");
        CommunityBoard.getInstance();

        StringUtil.printSection("Clans");
        ClanTable.getInstance();

        StringUtil.printSection("Geo-data & Pathfinding");
        GeoEngine.getInstance();

        StringUtil.printSection("Zones");
        ZoneManager.getInstance();

        StringUtil.printSection("Castles & Clan Halls");
        CastleManager.getInstance();
        ClanHallManager.getInstance();

        StringUtil.printSection("Task Managers");
        AttackStanceTaskManager.getInstance();
        DecayTaskManager.getInstance();
        GameTimeTaskManager.getInstance();
        ItemsOnGroundTaskManager.getInstance();
        PvpFlagTaskManager.getInstance();
        RandomAnimationTaskManager.getInstance();
        ShadowItemTaskManager.getInstance();
        WaterTaskManager.getInstance();

        StringUtil.printSection("Seven Signs");
        SevenSignsManager.getInstance();
        FestivalOfDarknessManager.getInstance();

        StringUtil.printSection("Manor Manager");
        ManorAreaData.getInstance();
        CastleManorManager.getInstance();

        StringUtil.printSection("NPCs");
        BufferManager.getInstance();
        NpcData.getInstance();
        WalkerRouteData.getInstance();
        DoorData.getInstance().spawn();
        StaticObjectData.getInstance();
        SpawnManager.getInstance();
        GrandBossManager.getInstance();
        DimensionalRiftManager.getInstance();
        InstantTeleportData.getInstance();
        TeleportData.getInstance();
        ObserverGroupData.getInstance();
        ShopData.getInstance();

        CastleManager.getInstance().loadArtifacts();

        StringUtil.printSection("Buffer");
        BuffsByClassData.getInstance();
        BuffsCommonData.getInstance();

        StringUtil.printSection("Gatekeeper");
        LocationsData.getInstance();
        MenuData.getInstance();

        StringUtil.printSection("Olympiads & Heroes");
        OlympiadGameManager.getInstance();
        Olympiad.getInstance();
        HeroManager.getInstance();

        StringUtil.printSection("Quests & Scripts");
        ScriptData.getInstance();

        if (Config.ALLOW_BOAT) {
            BoatManager.getInstance();
            BoatGiranTalking.load();
            BoatGludinRune.load();
            BoatInnadrilTour.load();
            BoatRunePrimeval.load();
            BoatTalkingGludin.load();
        }

        StringUtil.printSection("Events");
        DerbyTrackManager.getInstance();
        LotteryManager.getInstance();

        if (Config.ALLOW_WEDDING)
            CoupleManager.getInstance();

        if (Config.ALLOW_FISH_CHAMPIONSHIP)
            FishingChampionshipManager.getInstance();

        StringUtil.printSection("Spawns");
        SpawnManager.getInstance().spawn();

        StringUtil.printSection("Handlers");
        LOGGER.info("Loaded {} admin command handlers.", AdminCommandHandler.getInstance().size());
        LOGGER.info("Loaded {} chat handlers.", ChatHandler.getInstance().size());
        LOGGER.info("Loaded {} item handlers.", ItemHandler.getInstance().size());
        LOGGER.info("Loaded {} skill handlers.", SkillHandler.getInstance().size());
        LOGGER.info("Loaded {} target handlers.", TargetHandler.getInstance().size());
        LOGGER.info("Loaded {} user command handlers.", UserCommandHandler.getInstance().size());

        StringUtil.printSection("System");
        Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

        if (Config.DEADLOCK_DETECTOR) {
            LOGGER.info("Deadlock detector is enabled. Timer: {}s.", Config.DEADLOCK_CHECK_INTERVAL);

            final DeadLockDetector deadDetectThread = new DeadLockDetector();
            deadDetectThread.setDaemon(true);
            deadDetectThread.start();
        } else
            LOGGER.info("Deadlock detector is disabled.");

        LOGGER.info("Game-server has started, used memory: {} / {} Mo.", SysUtil.getUsedMemory(), SysUtil.getMaxMemory());
        LOGGER.info("Maximum allowed players: {}.", Config.MAXIMUM_ONLINE_USERS);

        StringUtil.printSection("Login");
        LoginServerThread.getInstance().start();

        final SelectorConfig sc = new SelectorConfig();
        sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
        sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
        sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
        sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;

        final GamePacketHandler handler = new GamePacketHandler();
        _selectorThread = new SelectorThread<>(sc, handler, handler, handler, new IPv4Filter());

        ThreadPool.scheduleAtFixedRate(new ResetDayStatisticTask(), 60000, 60000);

        InetAddress bindAddress = null;
        if (!Config.GAMESERVER_HOSTNAME.equals("*")) {
            try {
                bindAddress = InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
            } catch (Exception e) {
                LOGGER.error("The GameServer bind address is invalid, using all available IPs.", e);
            }
        }

        try {
            _selectorThread.openServerSocket(bindAddress, Config.GAMESERVER_PORT);
        } catch (Exception e) {
            LOGGER.error("Failed to open server socket.", e);
            System.exit(1);
        }
        _selectorThread.start();
    }

    public static GameServer getInstance() {
        return _gameServer;
    }

    public SelectorThread<GameClient> getSelectorThread() {
        return _selectorThread;
    }
}