package me.xmrvizzy.skyblocker.skyblock.quicknav;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuickNav {
    public static List<QuickNavButton> init(String screenTitle) {
        List<QuickNavButton> buttons = new ArrayList<>();
        SkyblockerConfig.QuickNav data = SkyblockerConfig.get().quickNav;
        try {
            if (data.button1.render) buttons.add(parseButton(data.button1, screenTitle, 0));
            if (data.button2.render) buttons.add(parseButton(data.button2, screenTitle, 1));
            if (data.button3.render) buttons.add(parseButton(data.button3, screenTitle, 2));
            if (data.button4.render) buttons.add(parseButton(data.button4, screenTitle, 3));
            if (data.button5.render) buttons.add(parseButton(data.button5, screenTitle, 4));
            if (data.button6.render) buttons.add(parseButton(data.button6, screenTitle, 5));
            if (data.button7.render) buttons.add(parseButton(data.button7, screenTitle, 6));
            if (data.button8.render) buttons.add(parseButton(data.button8, screenTitle, 7));
            if (data.button9.render) buttons.add(parseButton(data.button9, screenTitle, 8));
            if (data.button10.render) buttons.add(parseButton(data.button10, screenTitle, 9));
            if (data.button11.render) buttons.add(parseButton(data.button11, screenTitle, 10));
            if (data.button12.render) buttons.add(parseButton(data.button12, screenTitle, 11));
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return buttons;
    }

    private static QuickNavButton parseButton(SkyblockerConfig.QuickNavItem buttonInfo, String screenTitle, int id) throws CommandSyntaxException {
        SkyblockerConfig.ItemData itemData = buttonInfo.item;
        String nbtString = "{id:\"minecraft:" + itemData.itemName.toLowerCase(Locale.ROOT) + "\",Count:1";
        if (itemData.nbt.length() > 2) nbtString += "," + itemData.nbt;
        nbtString += "}";
        return new QuickNavButton(id,
                screenTitle.matches(buttonInfo.uiTitle),
                buttonInfo.clickEvent,
                ItemStack.fromNbt(StringNbtReader.parse(nbtString))
        );
    }
}
