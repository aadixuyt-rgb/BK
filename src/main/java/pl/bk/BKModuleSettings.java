package pl.bk;
import java.util.*;
/** Detailed editable presets for every added module. */
public final class BKModuleSettings {
 public record Option(String key,String label,String... values){}
 private static final Map<String,List<Option>> M=new LinkedHashMap<>();
 private static void add(String m,String... rows){List<Option> out=new ArrayList<>();for(String row:rows){String[] p=row.split("\\|",-1);if(p.length>2)out.add(new Option(p[0],p[1],Arrays.copyOfRange(p,2,p.length)));}M.put(m,List.copyOf(out));}
 static {
 add("ESP","box|Typ ramek|2D|3D|Narożniki|Wyłączone","boxColor|Kolor ramek|Fioletowy|Czerwony|Zielony|Niebieski|Biały|Tęczowy","fill|Wypełnienie|Wyłączone|Włączone","alpha|Przezroczystość|10%|25%|50%|75%","outline|Obrys|Włączony|Wyłączony","name|Nicki|Włączone|Wyłączone","nameColor|Kolor nicków|Biały|Fioletowy|Czerwony|Zielony|Tęczowy","health|HP|Włączone|Wyłączone","healthStyle|Styl HP|Pasek|Tekst|Oba","armor|Zbroja|Włączona|Wyłączona","distance|Dystans|Włączony|Wyłączony","range|Zasięg|32|64|96|128|192","players|Gracze|Pokaż|Ukryj","invisibles|Niewidzialni|Pokaż|Ukryj","friends|Znajomi|Pokaż|Ukryj");
 add("Custom Swords","style|Styl|Vanilla|Katana|Crystal|Neon","texture|Tekstura|Domyślna|Ognista|Lodowa|Ciemna","color|Kolor|Fioletowy|Czerwony|Niebieski|Zielony|Tęczowy","glint|Poświata|Włączona|Wyłączona","animation|Animacja|Domyślna|Szybka|Płynna|Wyłączona","scale|Skala|75%|100%|125%|150%");
 add("Sky","preset|Preset|Vanilla|Sunset|Night|Space|Fantasy","color|Kolor nieba|Domyślny|Fioletowy|Różowy|Niebieski|Czerwony","sun|Słońce|Domyślne|Ukryte|Niestandardowe","moon|Księżyc|Domyślny|Ukryty|Niestandardowy","clouds|Chmury|Vanilla|Wyłączone|Niestandardowe","stars|Gwiazdy|Wyłączone|Mało|Dużo");
 add("Beautify","particles|Cząsteczki|Włączone|Wyłączone","weather|Pogoda|Vanilla|Bez deszczu|Bez śniegu|Bez pogody","time|Pora dnia|Vanilla|Świt|Dzień|Zachód|Noc","animations|Animacje|Vanilla|Płynne|Szybkie","fov|Efekt FOV|Wyłączony|Subtelny|Mocny","screen|Efekty ekranu|Wyłączone|Subtelne|Pełne");
 add("Orbit Particles","particle|Typ cząsteczek|End Rod|Serce|Płomień|Portal|Dusza|Śnieg","color|Kolor|Domyślny|Fioletowy|Czerwony|Niebieski|Zielony|Tęczowy","count|Liczba|3|6|9|12|18","radius|Promień|0.5|1.0|1.5|2.0|3.0","speed|Prędkość|0.5x|1x|1.5x|2x","height|Wysokość|0.5|1.0|1.5|2.0","direction|Kierunek|Oba|Zgodnie|Przeciwnie");
 add("Jump Circles","particle|Cząsteczki|End Rod|Dusza|Płomień|Serce|Portal","color|Kolor|Domyślny|Fioletowy|Czerwony|Niebieski|Zielony|Tęczowy","radius|Promień|0.5|0.7|0.9|1.1|1.3|1.5|1.7","points|Liczba punktów|12|18|24|30|36|42|48","duration|Czas|0.5s|1s|1.5s|2s|3s","animation|Animacja|Rozszerzanie|Zanikanie|Obie|Brak");
 add("Blizzard","density|Gęstość|Niska|Średnia|Wysoka|Bardzo wysoka","size|Rozmiar|Mały|Średni|Duży","speed|Prędkość|Wolna|Normalna|Szybka","range|Zasięg|8|16|24|32|48","wind|Wiatr|Brak|Lekki|Mocny","color|Kolor|Biały|Niebieski|Fioletowy");
 add("Chams","mode|Tryb|Obrys|Wypełnienie|Obrys + wypełnienie","visibleColor|Kolor widocznych|Fioletowy|Czerwony|Zielony|Niebieski|Tęczowy","hiddenColor|Kolor przez ściany|Czerwony|Fioletowy|Niebieski|Zielony","alpha|Przezroczystość|10%|25%|50%|75%|100%","players|Gracze|Włączeni|Wyłączeni","friends|Znajomi|Wyróżnij|Normalnie");
 add("Aura","target|Cele|Gracze|Gracze + moby","range|Zasięg|2.5|3.0|3.5|4.0|4.5|5.0|6.0","priority|Priorytet|Najbliższy|Najmniej HP|Najwięcej HP|Ostatni atak","rotation|Rotacja|Wyłączona|Legit|Packet","cps|Szybkość|4|6|8|10|12|15|20","swing|Animacja ręki|Włączona|Wyłączona","walls|Przez ściany|Wyłączone|Włączone","players|Gracze|Włączeni|Wyłączeni","animals|Moby|Wyłączone|Włączone");
 add("AutoTotem","mode|Tryb|Zawsze|Przy niskim HP|Przy zagrożeniu","health|Próg HP|2|4|6|8|10|12|14","offhand|Slot|Offhand|Automatycznie","inventory|Szukaj w ekwipunku|Włączone|Wyłączone","delay|Opóźnienie|0|1|2|4|6|10");
 add("AutoTrap","target|Cel|Najbliższy|Ostatni atak|Wskazany","range|Zasięg|2|3|4|5|6","mode|Schemat|Normalny|Szybki|Bezpieczny","rotate|Rotacja|Legit|Packet|Wyłączona","support|Bloki pomocnicze|Włączone|Wyłączone","delay|Opóźnienie|0|1|2|4|6|10");
 add("AutoWeb","target|Cel|Najbliższy|Ostatni atak|Wskazany","range|Zasięg|2|3|4|5|6","rotate|Rotacja|Legit|Packet|Wyłączona","predict|Predykcja|Wyłączona|Włączona","delay|Opóźnienie|0|1|2|4|6|10");
 add("AutoCrystal","placeRange|Zasięg stawiania|2|3|4|5|6","breakRange|Zasięg niszczenia|2|3|4|5|6","minDamage|Min. obrażenia|2|4|6|8|10|12","maxSelf|Maks. obrażenia własne|2|4|6|8|10","speed|Szybkość|Wolna|Normalna|Szybka","rotate|Rotacja|Legit|Packet|Wyłączona","target|Priorytet|Najbliższy|Największe obrażenia");
 add("AutoDripstone","range|Zasięg|3|4|5|6|8","target|Cel|Najbliższy|Ostatni atak","place|Automatyczne stawianie|Włączone|Wyłączone","rotate|Rotacja|Legit|Packet|Wyłączona","delay|Opóźnienie|0|1|2|4|6|10");
 add("FreeCam","speed|Prędkość|0.1|0.2|0.5|1|2|3","collision|Kolizje|Włączone|Wyłączone","mode|Tryb|Kamera|Odłączona","showBody|Pokaż ciało|Włączone|Wyłączone","freeze|Zamroź gracza|Włączone|Wyłączone");
 add("Free Look","mode|Tryb|Klasyczny|Odwrócony","sensitivity|Czułość|0.25x|0.5x|0.75x|1x|1.5x|2x","smooth|Płynność|Wyłączona|Niska|Średnia|Wysoka","toggle|Sterowanie|Przełącznik|Przytrzymanie");
 add("Stealer","delay|Opóźnienie|0|1|2|4|6|10","items|Przedmioty|Wszystkie|Tylko wartościowe","sort|Sortowanie|Brak|Wartość|Typ","close|Zamknij po zabraniu|Włączone|Wyłączone","auto|Automatyczne zabieranie|Włączone|Wyłączone");
 add("Target Pearl","range|Zasięg|8|16|24|32|48|64","mode|Tryb|Celownik|Najbliższy|Ostatni atak","prediction|Predykcja|Wyłączona|Niska|Średnia|Wysoka","rotate|Rotacja|Legit|Packet|Wyłączona");
 add("Fast Place","blocks|Bloki|Wszystkie|Wybrane","delay|Opóźnienie|0|1|2|3|4|5","items|Przedmioty|Bloki|Wszystkie","airPlace|Stawianie w powietrzu|Wyłączone|Włączone");
 add("Blink / FakeLag","mode|Tryb|Blink|FakeLag|Pulse","delay|Interwał|50ms|100ms|200ms|300ms|500ms|1s","pulse|Czas aktywny|50ms|100ms|200ms|300ms|500ms","visual|Podgląd pozycji|Włączony|Wyłączony","autoDisable|Wyłącz przy ekranie|Włączone|Wyłączone");
 }
 public static List<Option> forModule(String m){return M.getOrDefault(m,List.of());}
 public static String value(String m,Option o){return BKExtraModules.state(m).settings.computeIfAbsent(o.key(),k->o.values()[0]);}
 public static String next(String m,Option o){String[] v=o.values();int i=Arrays.asList(v).indexOf(value(m,o));String n=v[(i+1+v.length)%v.length];BKExtraModules.state(m).settings.put(o.key(),n);BKConfig.save();return n;}
 private BKModuleSettings(){}
}