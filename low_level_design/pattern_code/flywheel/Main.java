import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// Flyweight interfaces
interface FontColor {
    public String getName();
    public String getType();
}

class BlueFont implements FontColor {
    private String name;
    public BlueFont(String name) {
        this.name = name + "Blue Color Font";
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return "BlueFont";
    }
}

interface FontType {
    public String getName();
    public String getType();
}

class ArialFont implements FontType {
    private String name;
    public ArialFont(String name) {
        this.name = name + "Arial Type Font";
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return "ArialFont";
    }
}

// Flyweight class to store the shared state
class FontStyle {
    private FontColor fontColor;
    private FontType fontType;
    private String name;

    public FontStyle(FontColor fontColor, FontType fontType) {
        this.fontColor = fontColor;
        this.fontType = fontType;
        updateName(fontColor, fontType);
    }

    private void updateName(FontColor fontColor, FontType fontType) {
        this.name = fontColor.getType() + "-" + fontType.getType();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FontStyle fontStyle = (FontStyle) obj;
        return Objects.equals(fontColor, fontStyle.fontColor) &&
               Objects.equals(fontType, fontStyle.fontType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fontColor, fontType);
    }

    @Override
    public String toString() {
        return name;
    }
}

// Abstract class for Characters (Flyweight object)
abstract class Character {
    private Map<FontStyle, String> fontStyleMap;
    protected String value;

    public Character(String value) {
        this.value = value;
        fontStyleMap = new HashMap<>();
    }

    protected void addFontStyle(FontStyle fontStyle) {
        fontStyleMap.put(fontStyle, value + " with " + fontStyle.toString());
    }

    protected String getFontStyle(FontStyle fontStyle) throws Exception {
        if (fontStyleMap.containsKey(fontStyle)) {
            return fontStyleMap.get(fontStyle);
        } else {
            throw new Exception("FontStyle not found!");
        }
    }
}

// Concrete class for a Character 'A'
class CharacterA extends Character {
    public CharacterA() {
        super("A");
    }
}

// Factory class to manage flyweight objects (shared state)
class CharacterFactory {
    private static Map<String, Character> characters = new HashMap<>();
    private static Map<FontStyle, FontStyle> fontStyleCache = new HashMap<>();

    static {
        characters.put("A", new CharacterA());
    }

    public static void addCharacter(String character, FontType fontType, FontColor fontColor) {
        // Check if the character already exists
        if (characters.containsKey(character)) {
            // Get the shared FontStyle (Flyweight)
            FontStyle fontStyle = getFontStyle(fontColor, fontType);
            characters.get(character).addFontStyle(fontStyle);
        }
    }

    public static String getCharacter(String character, FontType fontType, FontColor fontColor) throws Exception {
        if (characters.containsKey(character)) {
            FontStyle fontStyle = getFontStyle(fontColor, fontType);
            return characters.get(character).getFontStyle(fontStyle);
        } else {
            throw new Exception("Character not found!");
        }
    }

    private static FontStyle getFontStyle(FontColor fontColor, FontType fontType) {
        // Check if the FontStyle already exists in the cache
        FontStyle fontStyle = fontStyleCache.get(new FontStyle(fontColor, fontType));
        if (fontStyle == null) {
            fontStyle = new FontStyle(fontColor, fontType);
            fontStyleCache.put(fontStyle, fontStyle);
        }
        return fontStyle;
    }
}

// Main class to test the Flyweight Pattern
public class Main {
    public static void main(String[] args) throws Exception {
        FontColor blue = new BlueFont("Light");
        FontType arial = new ArialFont("Regular");

        // Add new font styles for character 'A'
        CharacterFactory.addCharacter("A", arial, blue);
        System.out.println(CharacterFactory.getCharacter("A", arial, blue));

        // Attempt to get the same font style for 'A', it should use the cached FontStyle
        System.out.println(CharacterFactory.getCharacter("A", arial, blue));
    }
}
