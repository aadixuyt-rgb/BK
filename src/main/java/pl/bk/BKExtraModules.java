package pl.bk;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Persistent independent state for the additional module set.
 *
 * Feature implementations are deliberately isolated from the source client's
 * framework; this standalone port can add each implementation without
 * importing incompatible event, rotation, inventory or shader infrastructure.
 */
public final class BKExtraModules {
    public static final class State {
        public boolean enabled = false;
        public int key = 0;
        public Map<String, String> settings = new LinkedHashMap<>();
    }

    private BKExtraModules() {}

    public static State state(String name) {
        State state = BKConfig.data.extraModules.get(name);
        if (state == null) {
            state = new State();
            BKConfig.data.extraModules.put(name, state);
        }
        if (state.settings == null) state.settings = new LinkedHashMap<>();
        return state;
    }

    public static boolean isEnabled(String name) {
        return state(name).enabled;
    }

    public static void toggle(String name) {
        State state = state(name);
        state.enabled = !state.enabled;
        BKConfig.save();
    }

    public static void setEnabled(String name, boolean enabled) {
        state(name).enabled = enabled;
        BKConfig.save();
    }
}
