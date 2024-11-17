package de.hysky.skyblocker.skyblock.item.tooltip.adders;

import de.hysky.skyblocker.skyblock.item.tooltip.TooltipAdder;
import de.hysky.skyblocker.utils.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;

public class SupercraftReminder extends TooltipAdder {
	private static final byte SUPERCRAFT_SLOT = 32;
	private static final byte RECIPE_RESULT_SLOT = 25;

	public SupercraftReminder(int priority) {
		super(Pattern.compile("^.+ Recipe$"), priority);
	}

	@Override
	public void addToTooltip(@Nullable Slot focusedSlot, ItemStack stack, List<Text> lines) {
		if (focusedSlot == null || focusedSlot.id != SUPERCRAFT_SLOT || !stack.isOf(Items.GOLDEN_PICKAXE)) return;
		String uuid = ItemUtils.getItemUuid(focusedSlot.inventory.getStack(RECIPE_RESULT_SLOT));
		if (!uuid.isEmpty()) return; //Items with UUID can't be stacked, and therefore the shift-click feature doesn't matter
		int index = lines.size() - 1;
		if (lines.get(lines.size() - 2).getString().equals("Recipe not unlocked!")) index--; //Place it right below the "Right-Click to set amount" line
		lines.add(index, Text.literal("Shift-Click to maximize the amount!").formatted(Formatting.GOLD));
	}
}
