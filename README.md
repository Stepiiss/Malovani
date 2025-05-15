Dokumentace projektu kreslicí aplikace
Tento dokument popisuje jednoduchou grafickou aplikaci pro kreslení základních tvarů a provádění operací s nimi. Aplikace je napsána v jazyce Java s využitím grafického rozhraní Swing.
Přehled funkcí
Aplikace umožňuje kreslit následující objekty:

Čáry (tenké, tlusté, tečkované)
Obdélníky
Kružnice
Polygony (mnohoúhelníky)

Dále nabízí tyto funkce:

Výplň plochy barvou
Mazání (gumování)
Nastavení barvy a tloušťky čáry
Zarovnání čar na úhly 45° při kreslení se stisknutým Shift

Ovládání aplikace
Základní ovládání

Levé tlačítko myši - Kreslení tvarů, přidávání bodů polygonu, výběr bodu pro výplň nebo mazání
Shift + kreslení čáry - Zarovnání čáry na násobky úhlu 45°
Double click - Dokončení kreslení polygonu
Ctrl - Přepnutí na tečkovanou čáru (během držení)
C - Vymazání celého plátna

Nástroje
Nástroje se volí pomocí tlačítek v horní části okna:

Čára
Obdélník
Kružnice
Polygon
Výplň
Guma
Barva (otevře dialog pro výběr barvy)
Tloušťka (rozbalovací menu s možnostmi 1, 2, 3, 5, 8 pixelů)

Architektura aplikace
Hlavní třídy
App.java
Hlavní třída aplikace, která vytváří uživatelské rozhraní a spravuje interakci s uživatelem. Obsažené metody:

start() - Inicializace hlavního okna
createToolbar() - Vytvoření panelu s nástroji
createAdapters() - Nastavení obsluhy událostí pro myš a klávesnici
Metody pro vykreslování jednotlivých tvarů

Models

Point - Reprezentace bodu v rovině (x, y)
Line - Reprezentace čáry (2 body a barva)
Polygon - Reprezentace mnohoúhelníku (seznam bodů)
LineCanvas - Uchovává všechny nakreslené objekty

Rasters

Raster - Rozhraní pro práci s rastrem (mřížkou pixelů)
RasterBufferedImage - Implementace rastru pomocí BufferedImage

Rasterizers

Rasterizer - Rozhraní pro převod geometrických objektů na pixely rastru
LineRasterizerTrivial - Rasterizace čar pomocí Bresenhamova algoritmu
DottedLineRasterizerTrivial - Rasterizace tečkovaných čar
LineCanvasRasterizer - Rasterizace celého plátna

Fillers

Filler - Rozhraní pro vyplňování ploch
BasicFiller - Implementace algoritmu vyplňování (flood fill)

Algoritmy
Kreslení čar
Aplikace používá Bresenhamův algoritmus pro vykreslování čar, který zajišťuje efektivní převod vektorových čar na rastrovou reprezentaci.
Kreslení kružnice
Pro vykreslování kružnic je použit Midpoint Circle Algorithm (Bresenhamův algoritmus pro kružnice), který efektivně určuje, které pixely mají být vykresleny.
Výplň plochy
Implementace nerekurzivního algoritmu semínkové výplně (flood fill) s využitím zásobníku pro zamezení přetečení zásobníku při vyplňování velkých ploch.
