package pl.bk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Persistent configuration for the three supported BK modules. */
public final class BKConfig {
    public static final class Data {
        public boolean noPush=false, noPushEntities=true, noPushFluids=false, noPushBlocks=true;
        public boolean tracers=false, tracerPlayers=true, tracerAnimals=false, tracerMobs=false;
        public boolean tracerInvisibles=true, tracerNakedPlayers=true, tracerFriends=true, tracerIgnoreFriends=false;
        public int tracerColor=0xFFFFFFFF, tracerFriendColor=0xFF20D86A;
        public float tracerWidth=1.5f;
        public boolean nametagsEnabled=true, nametagBackground=true, nametagArmor=false;
        public float nametagScale=0.8f;
        public int guiKey=344, noPushKey=0, tracersKey=0, nametagsKey=0;
    }
    public static Data data=new Data();
    private static final Gson GSON=new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE=FabricLoader.getInstance().getConfigDir().resolve("bkclient.json");
    private BKConfig() {}
    public static void load() {
        try {
            if(Files.exists(FILE)) {
                Data loaded=GSON.fromJson(Files.readString(FILE),Data.class);
                if(loaded!=null)data=loaded;
            }
        } catch(Exception ignored) { data=new Data(); }
        sanitize();
        save();
    }
    private static void sanitize() {
        data.tracerWidth=clamp(data.tracerWidth,1,5);
        data.nametagScale=clamp(data.nametagScale,0.5f,1.5f);
        data.guiKey=clamp(data.guiKey,0,348);
        data.noPushKey=clamp(data.noPushKey,0,348);
        data.tracersKey=clamp(data.tracersKey,0,348);
        data.nametagsKey=clamp(data.nametagsKey,0,348);
    }
    private static float clamp(float n,float min,float max) {
        return Float.isFinite(n)?Math.max(min,Math.min(max,n)):min;
    }
    private static int clamp(int n,int min,int max) { return Math.max(min,Math.min(max,n)); }
    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE,GSON.toJson(data));
        } catch(IOException ignored) {}
    }
}
