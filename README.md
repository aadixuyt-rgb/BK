# BK Client

Samodzielny mod klienta Minecraft **1.21.4 / Fabric / Java 21**. Nie jest częścią AdixuFX.

## Moduły

- **NoPush** — oddzielne przełączniki dla push od graczy, płynów i wypychania z bloków.
- **Tracers** — linie do graczy, zwierząt i mobów; filtry niewidzialnych, znajomych i graczy bez zbroi; kolor i grubość.
- **Hitboxes** — powiększanie hitboxów X/Z, wybór kategorii, filtr znajomych/niewidzialnych i regulowany rozmiar.
- **Error Killer** — makro interakcji z przyciskami/dźwigniami z konfiguracją opóźnień Cobweb i Obsidian.
- **Knockback Displacement** — kierunek dynamiczny lub stały, kąt, krok kąta, tylko sprint i ustawienie ukrycia rotacji. Funkcja odracza wyłącznie normalny atak kliknięty przez gracza; nie tworzy dodatkowych ataków.
- **Player ESP** — ramki, nazwy, życie, odległość i kolory.
- **Nametags** — osobny włącznik i bind, tło, skala oraz opcjonalne ikony zbroi.

## Dodatkowe moduły — rejestr i kategorie

ClickGUI grupuje dodatkowe pozycje w kategoriach **Visuals**, **Combat**, **Player** i **Misc**. Każda pozycja ma osobny stan WŁ./WYŁ. oraz bind zapisywany w konfiguracji. Jump Circles tworzy pierścień cząsteczek przy oderwaniu się gracza od ziemi; promień i liczba cząsteczek są konfigurowalne. Nowe pozycje są domyślnie wyłączone. Rejestr obejmuje: ESP, Custom Swords, Sky, Beautify, Orbit Particles, Jump Circles, Aura, AutoTotem, AutoTrap, AutoWeb, AutoCrystal, AutoDripstone, FreeCam, Free Look, Stealer, Target Pearl, Fast Place i Blink / FakeLag.

**Uwaga:** dodany rejestr, przełączniki i bindy nie oznaczają jeszcze pełnego przeniesienia logiki wszystkich 17 modułów. Każdy z nich wymaga osobnej adaptacji do samodzielnego BK; moduły źródłowe zależą od rozbudowanego frameworka BKClient-Full.

## Sterowanie

- **Prawy Shift** — otwiera ClickGUI.
- Każdy moduł ma osobny bind. Kliknij przycisk Bind w GUI, a potem naciśnij wybrany klawisz.
- Kliknij Ustawienia przy module, aby zmieniać jego ustawienia.

Konfiguracja jest zapisywana w .minecraft/config/bkclient.json.

## Build

Wymagane: Java 21, Gradle 8.10, Minecraft 1.21.4 i Fabric Loader 0.16+.

```bash
gradle build
```

Plik moda powstaje w build/libs/. GitHub Actions buduje projekt przy każdym pushu do main i publikuje artefakt BK-Client-Mod.

## Źródło i licencja

Część funkcji jest adaptacją wybranych modułów z [BKClient-Full](https://github.com/bkubiakdev/BKClient-Full). Oryginalny projekt jest udostępniony na licencji MIT; zachowano informację o prawach autorskich i dołączono licencję w pliku LICENSE. Repozytorium BKClient-Full jest źródłem tylko do odczytu — wszystkie zmiany tego projektu trafiają wyłącznie tutaj.
