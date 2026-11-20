# BK Client

Samodzielny mod klienta Minecraft **1.21.4 / Fabric / Java 21**.

## Moduły

W modzie pozostają wyłącznie trzy moduły:

- **NoPush** — niezależne wyłączanie wypychania przez graczy, płyny i bloki.
- **Nametags** — nazwa gracza i HP nad postacią, z opcjonalnym tłem, skalą i ikonami zbroi.
- **Tracers** — linie do graczy, zwierząt i wrogich mobów, z filtrami znajomych, niewidzialnych i graczy bez zbroi.

Pozostałe moduły, ich bindy, ustawienia i nieużywane mixiny zostały usunięte.

## Sterowanie

- **Prawy Shift** — otwiera ClickGUI.
- Każdy z trzech modułów ma osobny bind, który można zmienić w GUI.
- W GUI dostępne są ustawienia NoPush i Tracers.
- Konfiguracja jest zapisywana w `.minecraft/config/bkclient.json`.

## Build

Wymagane: Java 21, Gradle 8.10, Minecraft 1.21.4 i Fabric Loader 0.16+.

```bash
gradle build
```

Plik moda powstaje w `build/libs/`. GitHub Actions buduje projekt przy każdym pushu do main i publikuje artefakt BK-Client-Mod.
