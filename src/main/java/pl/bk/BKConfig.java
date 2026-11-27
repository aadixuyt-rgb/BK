package pl.bk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class BKConfig {
    public static final class Data {
        public boolean airFreeze=false;
        public boolean tracersEnabled=false;
        public int tracerColor=0;
        public float tracerThickness=2.0f;
        public boolean noPush=false, noPushEntities=true, noPushFluids=false, noPushBlocks=true;
        public int guiKey=344, airFreezeKey=72;
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
        data.guiKey=clamp(data.guiKey,0,348);
        data.airFreezeKey=clamp(data.airFreezeKey==0?72:data.airFreezeKey,0,348);
        data.tracerColor=clamp(data.tracerColor,0,4);
        data.tracerThickness=Math.max(1.0f,Math.min(5.0f,data.tracerThickness));
        save();
    }
    private static int clamp(int n,int min,int max) { return Math.max(min,Math.min(max,n)); }
    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE,GSON.toJson(data));
        } catch(IOException ignored) {}
    }
}