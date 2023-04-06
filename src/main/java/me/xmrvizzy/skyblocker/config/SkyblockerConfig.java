package me.xmrvizzy.skyblocker.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.xmrvizzy.skyblocker.chat.ChatFilterResult;

import java.util.ArrayList;
import java.util.List;

@Config(name = "skyblocker")
public class SkyblockerConfig implements ConfigData {

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.TransitiveObject
    public General general = new General();

    @ConfigEntry.Category("locations")
    @ConfigEntry.Gui.TransitiveObject
    public Locations locations = new Locations();

    @ConfigEntry.Category("messages")
    @ConfigEntry.Gui.TransitiveObject
    public Messages messages = new Messages();

    @ConfigEntry.Category("richPresence")
    @ConfigEntry.Gui.TransitiveObject
    public RichPresence richPresence = new RichPresence();

    @ConfigEntry.Category("quickNav")
    @ConfigEntry.Gui.TransitiveObject
    public QuickNav quickNav = new QuickNav();

    public static class QuickNav {
        public boolean enableQuickNav = true;

        @ConfigEntry.Category("button1")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public QuickNavItem button1 = new QuickNavItem(true, new ItemData("diamond_sword"), "Your Skills", "/skills");

        @ConfigEntry.Category("button2")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public QuickNavItem button2 = new QuickNavItem(true, new ItemData("painting"), "Collections", "/collection");

        @ConfigEntry.Category("button3")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public QuickNavItem button3 = new QuickNavItem(true, new ItemData("bone"), "\\(\\d+/\\d+\\) Pets", "/pets");

        @ConfigEntry.Category("button4")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public QuickNavItem button4 = new QuickNavItem(true, new ItemData("leather_chestplate", 1, "tag:{display:{color:8991416}}"), "Wardrobe \\([12]/2\\)", "/wardrobe");

        @ConfigEntry.Category("button5")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public QuickNavItem button5 = new QuickNavItem(true, new ItemData("player_head", 1, "tag:{SkullOwner:{Id:[I;-2081424676,-57521078,-2073572414,158072763],Properties:{textures:[{Value:\"ewogICJ0aW1lc3RhbXAiIDogMTU5MTMxMDU4NTYwOSwKICAicHJvZmlsZUlkIiA6ICI0MWQzYWJjMmQ3NDk0MDBjOTA5MGQ1NDM0ZDAzODMxYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNZWdha2xvb24iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODBhMDc3ZTI0OGQxNDI3NzJlYTgwMDg2NGY4YzU3OGI5ZDM2ODg1YjI5ZGFmODM2YjY0YTcwNjg4MmI2ZWMxMCIKICAgIH0KICB9Cn0=\"}]}}}"), "Sack of Sacks", "/sacks");

        @ConfigEntry.Category("button6")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public QuickNavItem button6 = new QuickNavItem(true, new ItemData("ender_chest"),  "Storage", "/storage");

        @ConfigEntry.Category("button7")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public QuickNavItem button7 = new QuickNavItem(true, new ItemData("player_head", 1, "tag:{SkullOwner:{Id:[I;-300151517,-631415889,-1193921967,-1821784279],Properties:{textures:[{Value:\"e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdjYzY2ODc0MjNkMDU3MGQ1NTZhYzUzZTA2NzZjYjU2M2JiZGQ5NzE3Y2Q4MjY5YmRlYmVkNmY2ZDRlN2JmOCJ9fX0=\"}]}}}"), "Hub", "/hub");

        @ConfigEntry.Category("button8")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public QuickNavItem button8 = new QuickNavItem(true, new ItemData("player_head", 1, "tag:{SkullOwner:{Id:[I;1605800870,415127827,-1236127084,15358548],Properties:{textures:[{Value:\"e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg5MWQ1YjI3M2ZmMGJjNTBjOTYwYjJjZDg2ZWVmMWM0MGExYjk0MDMyYWU3MWU3NTQ3NWE1NjhhODI1NzQyMSJ9fX0=\"}]}}}"), "Dungeon Hub", "/warp dungeon_hub");

        @ConfigEntry.Category("button9")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public QuickNavItem button9 = new QuickNavItem(true, new ItemData("player_head", 1, "tag:{SkullOwner:{Id:[I;1860483573,1726564425,-1428231295,-436665521],Properties:{textures:[{\"Signature\":\"vqNpEahIthHsm+Rgy9CPR01fzqdkhpnKeUNvZS7EktFZJu1T9MTpel5/KEL/sKFcmt+RAJJ9uLEwosF5fsb97A1TBnSbRwkfl/RUExO5F0EoV9+4L2wN/VGmOnjmXssqjfl9GsXPInJhhaAPcvZtmriRrznJez/JWzITjLEdBCbzKwBHqVu6Qa6zeD9N93LxVUEYYxKaULgTXkdpsxV2dl486ZJIhUKomp/KtWQrK77q2kfTsS8fWyiI/FeeSiWAE53okIkug3Tejj8UbzOGmPr43l99TMy+cWMREsCrfGmGJ0oKURTBy0n1l8E9GcfEGQLLaPrrOKUyBtOSAildnl2/yMEVRg+IYQ1ro91t+RMfNhsZOoH19ijCp4JH74J4xbd4CUn4Nb/CyxXrJUAenj4RLB7dEsiR0EU3rLS6wMZvr3h7QxZqFPPZ9UD7iF6GQ+HKQNOz47GaWFlw7g0AK/h0h8odUH6yvX6gsYGhc3EYII4czSKmFVgiEXdV4MP8rWlSrkqRpFyMByzNAjq2z6RL4F/NZdT9ZCuifdBjbBIBvARsMJ0VuTSGyfHXW0qB5d7Wn2GPKnPMX/AHWM/938knYe3ECwh/2AcywewXKwr/qflU0V+U1rY7XGpQTO7jRDs8o0XBIT7arlxw/McleXdkWQoELQcU17pM4TB16Fw=\",Value:\"ewogICJ0aW1lc3RhbXAiIDogMTY3NjcyMDcxOTU5MywKICAicHJvZmlsZUlkIiA6ICI2ZWU0YjlmNTY2ZTk0ODQ5YWFkZWViODFlNWY5MDM0ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJwcnRsIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I1ZGY1NTU5MjY0MzBkNWQ3NWFkZWQyMWRkOTYxOWI3NmM1YjdjYTJjN2Y1NDAxNDQwNTIzZDUzYThiY2ZhYWIiCiAgICB9CiAgfQp9=\"}]}}}"), "Visit prtl", "/visit prtl");

        @ConfigEntry.Category("button10")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public QuickNavItem button10 = new QuickNavItem(true, new ItemData("enchanting_table"), "Enchant Item", "/etable");

        @ConfigEntry.Category("button11")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public QuickNavItem button11 = new QuickNavItem(true, new ItemData("anvil"), "Anvil", "/anvil");

        @ConfigEntry.Category("button12")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public QuickNavItem button12 = new QuickNavItem(true, new ItemData("crafting_table"), "Craft Item", "/craft");
    }

    public static class QuickNavItem {
        public QuickNavItem(Boolean render, ItemData itemData, String uiTitle, String clickEvent) {
            this.render = render;
            this.item = itemData;
            this.clickEvent = clickEvent;
            this.uiTitle = uiTitle;
        }

        public Boolean render;

        @ConfigEntry.Category("item")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public ItemData item;

        public String uiTitle;
        public String clickEvent;
    }

    public static class ItemData {
        public ItemData(String itemName, int count, String nbt) {
            this.itemName = itemName;
            this.count = count;
            this.nbt = nbt;
        }

        public ItemData(String itemName) {
            this.itemName = itemName;
            this.count = 1;
            this.nbt = "";
        }

        public String itemName;
        public int count;
        public String nbt;
    }

    public static class General {
        public boolean enableUpdateNotification = true;
        public boolean backpackPreviewWithoutShift = false;

        @ConfigEntry.Gui.Excluded
        public String apiKey;

        @ConfigEntry.Category("bars")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public Bars bars = new Bars();
      
        @ConfigEntry.Category("itemList")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public ItemList itemList = new ItemList();

        @ConfigEntry.Category("itemTooltip")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public ItemTooltip itemTooltip = new ItemTooltip();

        @ConfigEntry.Category("hitbox")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public Hitbox hitbox = new Hitbox();

        @ConfigEntry.Gui.Excluded
        public List<Integer> lockedSlots = new ArrayList<>();
    }

    public static class Bars {
        public boolean enableBars = true;

        @ConfigEntry.Category("barpositions")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public BarPositions barpositions = new BarPositions();
    }

    public static class BarPositions {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public BarPosition healthBarPosition = BarPosition.LAYER1;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public BarPosition manaBarPosition = BarPosition.LAYER1;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public BarPosition defenceBarPosition = BarPosition.LAYER1;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public BarPosition experienceBarPosition = BarPosition.LAYER1;

    }

    public enum BarPosition {
        LAYER1,
        LAYER2,
        RIGHT,
        NONE;

        public String toString() {
            return switch (this) {
                case LAYER1 -> "Layer 1";
                case LAYER2 -> "Layer 2";
                case RIGHT -> "Right";
                case NONE -> "Disabled";
            };
        }

        public int toInt() {
            return switch (this) {
                case LAYER1 -> 0;
                case LAYER2 -> 1;
                case RIGHT -> 2;
                case NONE -> -1;
            };
        }
    }

    public static class Hitbox {
        public boolean oldFarmlandHitbox = true;
        public boolean oldLeverHitbox = false;
    }

    public static class RichPresence {
        public boolean enableRichPresence = false;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        @ConfigEntry.Gui.Tooltip()
        public Info info = Info.LOCATION;
        public boolean cycleMode = false;
        public String customMessage;
    }

    public static class ItemList {
        public boolean enableItemList = true;
    }

    public enum Average {
        ONE_DAY,
        THREE_DAY,
        BOTH;

        public String toString() {
            return switch (this) {
                case ONE_DAY -> "1 day price";
                case THREE_DAY -> "3 day price";
                case BOTH -> "Both";
            };
        }
    }

    public static class ItemTooltip {
        public boolean enableNPCPrice = true;
        public boolean enableAvgBIN = true;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        @ConfigEntry.Gui.Tooltip()
        public Average avg = Average.THREE_DAY;
        public boolean enableLowestBIN = true;
        public boolean enableBazaarPrice = true;
        public boolean enableMuseumDate = true;
    }

    public static class Locations {
        @ConfigEntry.Category("dungeons")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public Dungeons dungeons = new Dungeons();

        @ConfigEntry.Category("dwarvenmines")
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public DwarvenMines dwarvenMines = new DwarvenMines();
    }

    public static class Dungeons {
        @ConfigEntry.Gui.Tooltip()
        public boolean croesusHelper = true;
        public boolean enableMap = true;
        public boolean solveThreeWeirdos = true;
        public boolean blazesolver = true;
        public boolean solveTrivia = true;
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public Terminals terminals = new Terminals();
    }

    public static class Terminals {
        public boolean solveColor = true;
        public boolean solveOrder = true;
        public boolean solveStartsWith = true;
    }

    public static class DwarvenMines {
        public boolean enableDrillFuel = true;
        public boolean solveFetchur = true;
        public boolean solvePuzzler = true;
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public DwarvenHud dwarvenHud = new DwarvenHud();
    }

    public static class DwarvenHud {
        public boolean enabled = true;
        public boolean enableBackground = true;
        public int x = 10;
        public int y = 10;
    }

    public static class Messages {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public ChatFilterResult hideAbility = ChatFilterResult.PASS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public ChatFilterResult hideHeal = ChatFilterResult.PASS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public ChatFilterResult hideAOTE = ChatFilterResult.PASS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public ChatFilterResult hideImplosion = ChatFilterResult.PASS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public ChatFilterResult hideMoltenWave = ChatFilterResult.PASS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public ChatFilterResult hideAds = ChatFilterResult.PASS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public ChatFilterResult hideTeleportPad = ChatFilterResult.PASS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public ChatFilterResult hideCombo = ChatFilterResult.PASS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public ChatFilterResult hideAutopet = ChatFilterResult.PASS;
        @ConfigEntry.Gui.Tooltip()
        public boolean hideMana = false;
    }

    public enum Info {
        PURSE,
        BITS,
        LOCATION
    }

    public static void init() {
        AutoConfig.register(SkyblockerConfig.class, GsonConfigSerializer::new);
    }

    public static SkyblockerConfig get() {
        return AutoConfig.getConfigHolder(SkyblockerConfig.class).getConfig();
    }
}