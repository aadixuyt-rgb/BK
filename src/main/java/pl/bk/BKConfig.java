package pl.bk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Persistent settings for the standalone BK client. */
public final class BKConfig {
    public static final class Data {
        public boolean noPush=false, noPushEntities=true, noPushFluids=false, noPushBlocks=true;
        public boolean tracers=false, tracerPlayers=true, tracerAnimals=false, tracerMobs=false, tracerInvisibles=true, tracerIgnoreFriends=false;
        public int tracerColor=0xFFFFFFFF, tracerFriendColor=0xFF20D86A;
        public float tracerWidth=1.5f;
        public boolean hitboxes=false;
        public float hitboxExpansion=0.30f;
        public boolean hitboxPlayers=true, hitboxAnimals=true, hitboxMobs=true, hitboxInvisibles=true, hitboxFriends=false;
        public boolean errorKiller=false;
        public int errorKillerCobwebDelay=1, errorKillerObsidianDelay=1;
        public boolean knockbackDisplacement=false, knockbackDynamic=true, knockbackSprintOnly=false, knockbackHideRotation=false;
        public float knockbackAngle=0.0f;
        public int knockbackAngleStep=90;
        public boolean playerEsp=false, espBox=true, espName=true, espHealth=true, espFriends=true;
        public float espDistance=96.0f;
        public int espColor=0xFF9A5DFF, espFriendColor=0xFF20D86A;
        public boolean nametags=true;
        public float nametagScale=1.0f;
        public boolean nametagBackground=true, nametagArmor=false;
        public int guiKey=344, noPushKey=0, tracersKey=0, hitboxesKey=0, errorKillerKey=0, knockbackKey=0, playerEspKey=0;
    }
    public static Data data=new Data();
    private static final Gson GSON=new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE=FabricLoader.getInstance().getConfigDir().resolve("bkclient.json");
    private BKConfig() {}
    public static void load() {
        try {
            if(Files.exists(FILE)) { Data loaded=GSON.fromJson(Files.readString(FILE),Data.class); if(loaded!=null)data=loaded; }
        } catch(Exception ignored) { data=new Data(); }
        sanitize(); save();
    }
    private static void sanitize() {
        data.tracerWidth=clamp(data.tracerWidth,1,5); data.hitboxExpansion=clamp(data.hitboxExpansion,0,1);
        data.errorKillerCobwebDelay=clamp(data.errorKillerCobwebDelay,1,100);
        data.errorKillerObsidianDelay=clamp(data.errorKillerObsidianDelay,1,100);
        data.knockbackAngle=clamp(data.knockbackAngle,-180,180); data.knockbackAngleStep=clamp(data.knockbackAngleStep,15,180);
        data.espDistance=clamp(data.espDistance,8,192); data.nametagScale=clamp(data.nametagScale,.5f,2);
        data.guiKey=clamp(data.guiKey,0,348); data.noPushKey=clamp(data.noPushKey,0,348);
        data.tracersKey=clamp(data.tracersKey,0,348); data.hitboxesKey=clamp(data.hitboxesKey,0,348);
        data.errorKillerKey=clamp(data.errorKillerKey,0,348); data.knockbackKey=clamp(data.knockbackKey,0,348);
        data.playerEspKey=clamp(data.playerEspKey,0,348);
    }
    private static float clamp(float n,float min,float max){return Float.isFinite(n)?Math.max(min,Math.min(max,n)):min;}
    private static int clamp(int n,int min,int max){return Math.max(min,Math.min(max,n));}
    public static void save() {
        try { Files.createDirectories(FILE.getParent()); Files.writeString(FILE,GSON.toJson(data)); }
        catch(IOException ignored) {}
    }
}
