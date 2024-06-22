package de.hysky.skyblocker.skyblock.item.tooltip.adders;

import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.skyblock.item.tooltip.TooltipAdder;
import de.hysky.skyblocker.utils.ItemUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class DungeonQualityTooltip extends TooltipAdder {
	public static final String BASE_STAT_BOOST_PERCENTAGE = "baseStatBoostPercentage";
	public static final String ITEM_TIER = "item_tier";
	public static final String DUNGEON_SKILL_REQ = "dungeon_skill_req";

	public static final int MAX_BASE_STAT_BOOST_PERCENTAGE = 50;
	public static final int MAX_ITEM_TIER = 10;

	public static final Int2ObjectMap<Map<String, String>> DUNGEON_FLOOR_TIERS = new Int2ObjectArrayMap<>(
			Map.of(
					4, Map.of(
							"CATACOMBS:9", "F4",
							"CATACOMBS:24", "M1"
					),
					5, Map.of(
							"CATACOMBS:14", "F5",
							"CATACOMBS:26", "M2"
					),
					6, Map.of(
							"CATACOMBS:19", "F6",
							"CATACOMBS:28", "M3"
					),
					7, Map.of(
							"CATACOMBS:24", "F7",
							"CATACOMBS:30", "M4"
					)
			)
	);

	public DungeonQualityTooltip(int priority) {
		super(priority);
	}

	@Override
	public void addToTooltip(@Nullable Slot focusedSlot, ItemStack stack, List<Text> lines) {
		if (!SkyblockerConfigManager.get().general.itemTooltip.dungeonQuality) return;
		NbtCompound customData = ItemUtils.getCustomData(stack);
		if (customData.contains(BASE_STAT_BOOST_PERCENTAGE))
			lines.add(getItemQualityText(customData.getInt(BASE_STAT_BOOST_PERCENTAGE)));
		if (customData.contains(ITEM_TIER))
			lines.add(getFloorTierText(customData.getInt(ITEM_TIER), customData.contains(DUNGEON_SKILL_REQ), customData.getString(DUNGEON_SKILL_REQ)));
	}

	Text getItemQualityText(int quality) {
		return getText("Item Quality:", quality + "/50", quality == MAX_BASE_STAT_BOOST_PERCENTAGE);
	}

	Text getFloorTierText(int tier, boolean hasDungeonSkillReq, String dungeonSkillReq) {
		return getText("Floor Tier:", getItemTierFloor(tier, hasDungeonSkillReq, dungeonSkillReq), tier == MAX_ITEM_TIER);
	}

	Text getText(String description, String value, boolean isMax) {
		return Text.literal(String.format("%-21s", description))
				.formatted(Formatting.AQUA)
				.append(Text.literal(value)
						.formatted(isMax ? Formatting.GREEN : Formatting.BLUE)
				);
	}

	String getItemTierFloor(int tier, boolean hasDungeonSkillReq, String dungeonSkillReq) {
		return switch (tier) {
			case 0 -> "E";
			case 1, 2, 3 -> String.format("F%d", tier);
			case 4, 5, 6, 7 -> {
				if (hasDungeonSkillReq) {
					Map<String, String> possibleTiers = DUNGEON_FLOOR_TIERS.get(tier);
					if (possibleTiers.containsKey(dungeonSkillReq)) {
						yield possibleTiers.get(dungeonSkillReq);
					}
				}
				yield String.format("F%d/M%d", tier, tier - 3);
			}
			case 8, 9, 10 -> String.format("M%d", tier - 3);
			default -> String.format("Unknown (%d)", tier);
		};
	}
}
