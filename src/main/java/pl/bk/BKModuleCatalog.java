package pl.bk;

import java.util.List;

/** The additional modules, grouped for the ClickGUI. */
public final class BKModuleCatalog {
    public record Entry(String name, String category) {}
    public static final List<Entry> EXTRA = List.of(
        new Entry("ESP", "Visuals"),
        new Entry("Custom Swords", "Visuals"),
        new Entry("Sky", "Visuals"),
        new Entry("Beautify", "Visuals"),
        new Entry("Orbit Particles", "Visuals"),
        new Entry("Aura", "Combat"),
        new Entry("AutoTotem", "Combat"),
        new Entry("AutoTrap", "Combat"),
        new Entry("AutoWeb", "Combat"),
        new Entry("AutoCrystal", "Combat"),
        new Entry("AutoDripstone", "Combat"),
        new Entry("FreeCam", "Player"),
        new Entry("Free Look", "Player"),
        new Entry("Stealer", "Player"),
        new Entry("Target Pearl", "Player"),
        new Entry("Fast Place", "Misc"),
        new Entry("Blink / FakeLag", "Misc")
    );
    private BKModuleCatalog() {}
}
