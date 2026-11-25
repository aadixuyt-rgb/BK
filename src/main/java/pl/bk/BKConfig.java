package pl.bk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class BKConfig {
    public static final class Data {
        public boolean noPush=false, noPushEntities=true, noPushFluids=false, noPushBlocks=true;
        public int guiKey=344, noPushKey=72;
        public int dripstoneDelayMs=250;
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
        data.noPushKey=clamp(data.noPushKey,0,348);
        data.dripstoneDelayMs=clamp(data.dripstoneDelayMs,50,2000);
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