package de.hysky.skyblocker.skyblock.item.tooltip.adders;

import de.hysky.skyblocker.skyblock.item.tooltip.TooltipAdder;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LineSmoother extends TooltipAdder {
	//This is static to not create a new text object for each line in every item
	private static final Text BUMPY_LINE = Text.literal("-".repeat(17)).formatted(Formatting.DARK_GRAY, Formatting.STRIKETHROUGH);

	public LineSmoother(int priority) {
		super(priority);
	}

	public static Text createSmoothLine() {
		return Text.literal(" ".repeat(20)).formatted(Formatting.DARK_GRAY, Formatting.STRIKETHROUGH, Formatting.BOLD);
	}

	@Override
	public void addToTooltip(@Nullable Slot focusedSlot, ItemStack stack, List<Text> lines) {
		for (Text line : lines) {
			List<Text> lineSiblings = line.getSiblings();
			//Compare the first sibling rather than the whole object as the style of the root object can change while visually staying the same
			if (lineSiblings.size() == 1 && lineSiblings.getFirst().equals(BUMPY_LINE)) {
				lines.set(lines.indexOf(line), createSmoothLine());
			}
		}
	}
}
