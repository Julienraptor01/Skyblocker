package de.hysky.skyblocker.skyblock.chocolatefactory;

import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.skyblock.item.tooltip.TooltipAdder;
import de.hysky.skyblocker.skyblock.item.tooltip.adders.LineSmoother;
import de.hysky.skyblocker.utils.ItemUtils;
import de.hysky.skyblocker.utils.render.gui.ColorHighlight;
import de.hysky.skyblocker.utils.render.gui.ContainerSolver;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChocolateFactoryHelper extends ContainerSolver {
	public static final String TITLE_PATTERN = "^Chocolate Factory$";

	private static final Logger LOGGER = LoggerFactory.getLogger(ChocolateFactoryHelper.class);

	private static final String NUMBER = "(?:\\d+,)*\\d+(?:.\\d+)?";
	private static final String ROMAN_NUMBER = "M{0,3}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})";
	private static final String NOT_EMPTY_ROMAN_NUMBER = "(?=[MDCLXVI])" + ROMAN_NUMBER;

	private static final Pattern PRESTIGE_SLOT = Pattern.compile("^Chocolate Factory " + NOT_EMPTY_ROMAN_NUMBER + "$");
	private static final Pattern RABBIT_BARN_SLOT = Pattern.compile("^Rabbit Barn " + NOT_EMPTY_ROMAN_NUMBER + "$");
	private static final Pattern HAND_BAKED_CHOCOLATE_SLOT = Pattern.compile("^Hand-Baked Chocolate " + NOT_EMPTY_ROMAN_NUMBER + "$");
	private static final Pattern RABBIT_SHRINE_SLOT = Pattern.compile("^Rabbit Shrine(?: " + NOT_EMPTY_ROMAN_NUMBER + ")?$");
	private static final Pattern TIME_TOWER_SLOT = Pattern.compile("^Time Tower " + NOT_EMPTY_ROMAN_NUMBER + "$");
	private static final Pattern PRODUCTION_SLOT = Pattern.compile("^Chocolate Production$");
	private static final Pattern CLICKER_SLOT = Pattern.compile("^" + NUMBER + " Chocolate$");
	private static final Pattern COACH_SLOT = Pattern.compile("^Coach Jackrabbit ?" + ROMAN_NUMBER + "$");
	private static final Pattern RABBIT_BRO_SLOT = Pattern.compile("^Rabbit Bro.*$");
	private static final Pattern RABBIT_COUSIN_SLOT = Pattern.compile("^Rabbit Cousin.*$");
	private static final Pattern RABBIT_SIS_SLOT = Pattern.compile("^Rabbit Sis.*$");
	private static final Pattern RABBIT_DADDY_SLOT = Pattern.compile("^Rabbit Daddy.*$");
	private static final Pattern RABBIT_GRANNY_SLOT = Pattern.compile("^Rabbit Granny.*$");
	private static final Pattern RABBIT_UNCLE_SLOT = Pattern.compile("^Rabbit Uncle.*$");
	private static final Pattern RABBIT_DOG_SLOT = Pattern.compile("^Rabbit Dog.*$");
	private static final Pattern RABBIT_UNKNOWN_SLOT = Pattern.compile("^Rabbit .*$");

	private static final byte STRAY_RABBIT_START_SLOT = 0;
	private static final byte STRAY_RABBIT_END_SLOT = 26;

	private static final Pattern CURRENT_PRESTIGE_CHOCOLATE = Pattern.compile("^Chocolate this Prestige: (" + NUMBER + ")$");
	private static final Pattern REQUIRED_PRESTIGE_CHOCOLATE = Pattern.compile("Requires (" + NUMBER + "[a-zA-Z]?) Chocolate");
	private static final Pattern READY_TO_PRESTIGE = Pattern.compile("^Click to prestige!$");
	private static final Pattern MAX_PRESTIGE = Pattern.compile("^You have reached max prestige!$");
	private static final Pattern CURRENT_CHOCOLATE_OR_UPGRADE_COST = Pattern.compile("^(" + NUMBER + ") Chocolate$");
	private static final Pattern CURRENT_CPS = Pattern.compile("^(" + NUMBER + ") (?:Chocolate )?per second$");
	private static final Pattern BREAKDOWN_CPS = Pattern.compile("^ {2}\\+(" + NUMBER + ") \\([\\w' ]+\\)$");
	private static final Pattern CURRENT_MULTIPLIER = Pattern.compile("^Total Multiplier: (" + NUMBER + ")x$");
	private static final Pattern TIME_TOWER_CURRENT_MULTIPLIER = Pattern.compile("\\+(" + NUMBER + ")x");
	private static final Pattern TIME_TOWER_STATE = Pattern.compile("^Status: ((?:IN)?ACTIVE)(?: \\w+)?$");
	private static final Pattern NEXT_MULTIPLIER = Pattern.compile("^ {2}\\+(" + NUMBER + ")x Chocolate per second$");
	private static final Pattern COACH_CURRENT_MULTIPLIER = Pattern.compile("\\+(" + NUMBER + ")x");
	private static final Pattern RABBIT_CURRENT_CPS = Pattern.compile("\\+(" + NUMBER + ")");
	private static final Pattern RABBIT_NEXT_CPS = Pattern.compile("^ {2}\\+(" + NUMBER + ") Chocolate per second$");
	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);
	private static final NumberFormat COMPACT_NUMBER_FORMAT = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);

	private static final IntArrayList upgradesSlots = new IntArrayList(3);
	private static final IntArrayList rabbitSlots = new IntArrayList(7);

	private static final List<cpsUpgrade> cpsUpgrades = new ObjectArrayList<>(9);
	private static final List<upgrade> upgrades = new ObjectArrayList<>(3);

	private static final List<ColorHighlight> highlights = new ObjectArrayList<>(3);

	private static boolean slotsValid = false;
	private static Integer prestigeSlot = null;
	private static Integer productionSlot = null;
	private static Integer clickerSlot = null;
	private static Integer timeTowerSlot = null;
	private static Integer coachSlot = null;

	private static boolean maxPrestige = false;
	private static Long currentPrestigeChocolate = null;
	private static Long chocolateRequiredForPrestige = null;
	private static Long currentChocolate = null;
	private static double currentCps = 0;
	private static int rawCps = 0;
	private static double currentMultiplier = 1;
	private static boolean towerActive = false;
	private static double correctedMultiplier = 1;

	public ChocolateFactoryHelper() {
		super(TITLE_PATTERN);
	}

	@Override
	protected boolean isEnabled() {
		return SkyblockerConfigManager.get().helpers.chocolateFactory.enableChocolateFactoryHelper;
	}

	@Override
	protected List<ColorHighlight> getColors(String[] groups, Int2ObjectMap<ItemStack> slots) {
		highlights.clear();
		cpsUpgrades.clear();
		upgrades.clear();

		if (!slotsValid) {
			upgradesSlots.clear();
			rabbitSlots.clear();
			for (Int2ObjectMap.Entry<ItemStack> entry : slots.int2ObjectEntrySet()) {
				switch (entry.getValue().getName().getString()) {
					case String s when PRESTIGE_SLOT.matcher(s).matches() -> prestigeSlot = entry.getIntKey();
					case String s when CLICKER_SLOT.matcher(s).matches() -> clickerSlot = entry.getIntKey();
					case String s when PRODUCTION_SLOT.matcher(s).matches() -> productionSlot = entry.getIntKey();
					case String s when RABBIT_BARN_SLOT.matcher(s).matches()
							|| HAND_BAKED_CHOCOLATE_SLOT.matcher(s).matches()
							|| RABBIT_SHRINE_SLOT.matcher(s).matches() -> upgradesSlots.add(entry.getIntKey());
					case String s when TIME_TOWER_SLOT.matcher(s).matches() -> timeTowerSlot = entry.getIntKey();
					case String s when COACH_SLOT.matcher(s).matches() -> coachSlot = entry.getIntKey();
					case String s when RABBIT_BRO_SLOT.matcher(s).matches()
							|| RABBIT_COUSIN_SLOT.matcher(s).matches()
							|| RABBIT_SIS_SLOT.matcher(s).matches()
							|| RABBIT_DADDY_SLOT.matcher(s).matches()
							|| RABBIT_GRANNY_SLOT.matcher(s).matches()
							|| RABBIT_UNCLE_SLOT.matcher(s).matches()
							|| RABBIT_DOG_SLOT.matcher(s).matches()
							|| RABBIT_UNKNOWN_SLOT.matcher(s).matches() -> rabbitSlots.add(entry.getIntKey());
					default -> {
					}
				}
			}
			slotsValid = prestigeSlot != null;
			if (prestigeSlot == null)
				return List.of();
		}

		if (!maxPrestige) {
			final long[] requiredPrestigeChocolate = new long[1];
			for (Text line : ItemUtils.getLore(slots.get(prestigeSlot.intValue()))) {
				Matcher currentMatcher = CURRENT_PRESTIGE_CHOCOLATE.matcher(line.getString());
				Matcher requiredMatcher = REQUIRED_PRESTIGE_CHOCOLATE.matcher(line.getString());
				Matcher readyMatcher = READY_TO_PRESTIGE.matcher(line.getString());
				Matcher maxMatcher = MAX_PRESTIGE.matcher(line.getString());
				try {
					if (currentMatcher.matches()) {
						long newCurrentPrestigeChocolate = NUMBER_FORMAT.parse(currentMatcher.group(1)).longValue();
						if (currentPrestigeChocolate != null && newCurrentPrestigeChocolate < currentPrestigeChocolate)
							slotsValid = false;
						currentPrestigeChocolate = newCurrentPrestigeChocolate;
					} else if (requiredMatcher.find())
						requiredPrestigeChocolate[0] = COMPACT_NUMBER_FORMAT.parse(requiredMatcher.group(1)).longValue();
					else if (readyMatcher.matches())
						chocolateRequiredForPrestige = 0L;
					else if (maxMatcher.matches()) {
						currentPrestigeChocolate = null;
						chocolateRequiredForPrestige = null;
						maxPrestige = true;
					}
				} catch (Exception e) {
					LOGGER.error("Prestige :", e);
					slotsValid = false;
				}
			}
			if (!maxPrestige && requiredPrestigeChocolate[0] != 0)
				chocolateRequiredForPrestige = requiredPrestigeChocolate[0] - currentPrestigeChocolate;
		}

		if (!slotsValid)
			return List.of();

		if (clickerSlot != null) {
			try {
				Matcher currentMatcher = CURRENT_CHOCOLATE_OR_UPGRADE_COST.matcher(slots.get(clickerSlot.intValue()).getName().getString());
				if (currentMatcher.matches())
					currentChocolate = NUMBER_FORMAT.parse(currentMatcher.group(1)).longValue();

				for (Text line : ItemUtils.getLore(slots.get(clickerSlot.intValue()))) {
					Matcher currentCpsMatcher = CURRENT_CPS.matcher(line.getString());
					if (currentCpsMatcher.matches())
						currentCps = NUMBER_FORMAT.parse(currentCpsMatcher.group(1)).doubleValue();
				}
			} catch (Exception e) {
				LOGGER.error("Clicker :", e);
			}
		}

		if (productionSlot != null) {
			int[] newRawCps = new int[1];
			for (Text line : ItemUtils.getLore(slots.get(productionSlot.intValue()))) {
				Matcher currentCpsMatcher = CURRENT_CPS.matcher(line.getString());
				Matcher breakdownCpsMatcher = BREAKDOWN_CPS.matcher(line.getString());
				Matcher currentMultiplierMatcher = CURRENT_MULTIPLIER.matcher(line.getString());
				try {
					if (currentCpsMatcher.matches())
						currentCps = NUMBER_FORMAT.parse(currentCpsMatcher.group(1)).doubleValue();
					else if (breakdownCpsMatcher.matches())
						newRawCps[0] += NUMBER_FORMAT.parse(breakdownCpsMatcher.group(1)).intValue();
					else if (currentMultiplierMatcher.matches())
						currentMultiplier = NUMBER_FORMAT.parse(currentMultiplierMatcher.group(1)).doubleValue();
				} catch (Exception e) {
					LOGGER.error("Production :", e);
				}
			}
			rawCps = newRawCps[0];
		} else slotsValid = false;

		if (timeTowerSlot != null) {
			ItemStack slotLore = slots.get(timeTowerSlot.intValue());
			final Double[] values = new Double[2];
			final long[] upgradeCost = new long[1];
			for (Text line : ItemUtils.getLore(slotLore)) {
				Matcher currentMatcher = TIME_TOWER_CURRENT_MULTIPLIER.matcher(line.getString());
				Matcher stateMatcher = TIME_TOWER_STATE.matcher(line.getString());
				Matcher nextMatcher = NEXT_MULTIPLIER.matcher(line.getString());
				Matcher upgradeCostMatcher = CURRENT_CHOCOLATE_OR_UPGRADE_COST.matcher(line.getString());
				try {
					if (stateMatcher.matches())
						towerActive = stateMatcher.group(1).equals("ACTIVE");
					else if (nextMatcher.matches())
						values[1] = NUMBER_FORMAT.parse(nextMatcher.group(1)).doubleValue();
					else if (upgradeCostMatcher.matches())
						upgradeCost[0] = NUMBER_FORMAT.parse(upgradeCostMatcher.group(1)).longValue();
					else if (currentMatcher.find())
						values[0] = NUMBER_FORMAT.parse(currentMatcher.group(1)).doubleValue();
				} catch (Exception e) {
					LOGGER.error("Time Tower :", e);
				}
			}
			if (values[0] != null) {
				double efficiencyFactor = SkyblockerConfigManager.get().helpers.chocolateFactory.timeTowerEfficiency / 7;
				if (values[1] != null) {
					double cpsIncrease = rawCps * (values[1] - values[0]) * efficiencyFactor;
					cpsUpgrades.add(new cpsUpgrade(timeTowerSlot, cpsIncrease, upgradeCost[0], upgradeCost[0] / cpsIncrease));
				}
				correctedMultiplier = currentMultiplier - (towerActive ? values[0] : 0) + values[0] * efficiencyFactor;
			} else correctedMultiplier = currentMultiplier;
		} else correctedMultiplier = currentMultiplier;

		if (coachSlot != null) {
			ItemStack slotLore = slots.get(coachSlot.intValue());
			final Double[] values = new Double[2];
			final long[] upgradeCost = new long[1];
			for (Text line : ItemUtils.getLore(slotLore)) {
				Matcher currentMatcher = COACH_CURRENT_MULTIPLIER.matcher(line.getString());
				Matcher nextMatcher = NEXT_MULTIPLIER.matcher(line.getString());
				Matcher upgradeCostMatcher = CURRENT_CHOCOLATE_OR_UPGRADE_COST.matcher(line.getString());
				try {
					if (nextMatcher.matches())
						values[1] = NUMBER_FORMAT.parse(nextMatcher.group(1)).doubleValue();
					else if (upgradeCostMatcher.matches())
						upgradeCost[0] = NUMBER_FORMAT.parse(upgradeCostMatcher.group(1)).longValue();
					else if (currentMatcher.find())
						values[0] = NUMBER_FORMAT.parse(currentMatcher.group(1)).doubleValue();
				} catch (Exception e) {
					LOGGER.error("Coach :", e);
				}
			}
			if (values[1] != null) {
				double cpsIncrease = rawCps * (values[0] == null ? values[1] : values[1] - values[0]);
				cpsUpgrades.add(new cpsUpgrade(coachSlot, cpsIncrease, upgradeCost[0], upgradeCost[0] / cpsIncrease));
			}
		}

		for (int slot : rabbitSlots.toIntArray()) {
			final Double[] values = new Double[2];
			final long[] upgradeCost = new long[1];
			for (Text line : ItemUtils.getLore(slots.get(slot))) {
				Matcher currentMatcher = RABBIT_CURRENT_CPS.matcher(line.getString());
				Matcher nextMatcher = RABBIT_NEXT_CPS.matcher(line.getString());
				Matcher upgradeCostMatcher = CURRENT_CHOCOLATE_OR_UPGRADE_COST.matcher(line.getString());
				try {
					if (nextMatcher.matches())
						values[1] = NUMBER_FORMAT.parse(nextMatcher.group(1)).doubleValue();
					else if (upgradeCostMatcher.matches())
						upgradeCost[0] = NUMBER_FORMAT.parse(upgradeCostMatcher.group(1)).longValue();
					else if (currentMatcher.find())
						values[0] = NUMBER_FORMAT.parse(currentMatcher.group(1)).doubleValue();
				} catch (Exception e) {
					LOGGER.error("Rabbit :", e);
				}
			}
			if (values[1] != null) {
				double cpsIncrease = (values[0] == null ? values[1] : values[1] - values[0]) * correctedMultiplier;
				cpsUpgrades.add(new cpsUpgrade(slot, cpsIncrease, upgradeCost[0], upgradeCost[0] / cpsIncrease));
			}
		}

		for (int slot : upgradesSlots.toIntArray()) {
			final Long[] upgradeCost = new Long[1];
			for (Text line : ItemUtils.getLore(slots.get(slot))) {
				Matcher currentMatcher = CURRENT_CHOCOLATE_OR_UPGRADE_COST.matcher(line.getString());
				try {
					if (currentMatcher.matches())
						upgradeCost[0] = NUMBER_FORMAT.parse(currentMatcher.group(1)).longValue();
				} catch (Exception e) {
					LOGGER.error("Upgrade :", e);
				}
			}
			if (upgradeCost[0] != null)
				upgrades.add(new upgrade(slot, upgradeCost[0]));
		}

		cpsUpgrades.sort(Comparator.comparingDouble(cpsUpgrade::costPerCpsIncrease));

		if (!maxPrestige && chocolateRequiredForPrestige == 0)
			highlights.add(ColorHighlight.aqua(prestigeSlot));

		for (int i = STRAY_RABBIT_START_SLOT; i <= STRAY_RABBIT_END_SLOT; i++)
			switch (slots.get(i).getName().getString()) {
				case "CLICK ME!" -> highlights.add(ColorHighlight.lightPurple(i));
				case String s when s.startsWith("Golden Rabbit - ") -> highlights.add(ColorHighlight.gold(i));
				default -> {}
			}

		if (cpsUpgrades.isEmpty())
			return highlights;

		cpsUpgrade bestUpgrade = cpsUpgrades.getFirst();
		if (currentChocolate == null) {
			highlights.add(ColorHighlight.blue(bestUpgrade.slot));
			return highlights;
		}

		if (bestUpgrade.cost <= currentChocolate) {
			highlights.add(ColorHighlight.green(bestUpgrade.slot));
			return highlights;
		}

		for (cpsUpgrade upgrade : cpsUpgrades.subList(1, cpsUpgrades.size()))
			if (upgrade.cost <= currentChocolate) {
				highlights.add(ColorHighlight.yellow(upgrade.slot));
				highlights.add(ColorHighlight.blue(bestUpgrade.slot));
				return highlights;
			}

		highlights.add(ColorHighlight.blue(bestUpgrade.slot));
		return highlights;

		/*
		LOGGER.warn("---- Chocolate Factory Helper States Dump start ----");
		LOGGER.info("slotsValid: {}", slotsValid);
		LOGGER.info("prestigeSlot: {}", prestigeSlot);
		LOGGER.info("productionSlot: {}", productionSlot);
		LOGGER.info("clickerSlot: {}", clickerSlot);
		LOGGER.info("timeTowerSlot: {}", timeTowerSlot);
		LOGGER.info("coachSlot: {}", coachSlot);
		LOGGER.info("rabbitSlots size: {}", rabbitSlots.size());
		for (int i = 0; i < rabbitSlots.size(); i++) {
			LOGGER.info("rabbitSlots[{}]: {}", i, rabbitSlots.getInt(i));
		}
		LOGGER.info("upgradesSlots size: {}", upgradesSlots.size());
		for (int i = 0; i < upgradesSlots.size(); i++) {
			LOGGER.info("upgradesSlots[{}]: {}", i, upgradesSlots.getInt(i));
		}
		LOGGER.info("maxPrestige: {}", maxPrestige);
		LOGGER.info("currentPrestigeChocolate: {}", currentPrestigeChocolate);
		LOGGER.info("chocolateRequiredForPrestige: {}", chocolateRequiredForPrestige);
		LOGGER.info("currentChocolate: {}", currentChocolate);
		LOGGER.info("currentCps: {}", currentCps);
		LOGGER.info("rawCps: {}", rawCps);
		LOGGER.info("currentMultiplier: {}", currentMultiplier);
		LOGGER.info("towerActive: {}", towerActive);
		LOGGER.info("correctedMultiplier: {}", correctedMultiplier);
		LOGGER.warn("---- Chocolate Factory Helper States Dump end ----");
		*/
	}

	private record cpsUpgrade(int slot, double cpsIncrease, long cost, double costPerCpsIncrease) {
	}

	private record upgrade(int slot, long cost) {
	}

	public static final class Tooltip extends TooltipAdder {
		private static final Map<String, Integer> TIME_UNITS = new LinkedHashMap<>() {{
			put("y", 31536000);
			put("mo", 2592000);
			put("w", 604800);
			put("d", 86400);
			put("h", 3600);
			put("m", 60);
			put("s", 1);
		}};

		private static final List<Text> NEW_LORE_LINES = new ArrayList<>();

		private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);

		static {
			NUMBER_FORMAT.setMinimumFractionDigits(0);
			NUMBER_FORMAT.setMaximumFractionDigits(2);
		}

		public Tooltip(int priority) {
			super(TITLE_PATTERN, priority);
		}

		private static MutableText formatTime(double seconds) {
			final long[] secondsLeft = {(long) Math.ceil(seconds)};
			if (secondsLeft[0] <= 0)
				return Text.literal("Now").formatted(Formatting.GREEN);
			else if (currentCps == 0)
				return Text.literal("∞").formatted(Formatting.RED);
			else {
				StringBuilder time = new StringBuilder();
				for (Map.Entry<String, Integer> unit : TIME_UNITS.entrySet()) {
					long value = secondsLeft[0] / unit.getValue();
					secondsLeft[0] %= unit.getValue();
					if (value > 0)
						time.append(value).append(unit.getKey()).append(" ");
				}
				return Text.literal(time.toString().trim()).formatted(Formatting.GOLD);
			}
		}

		@Override
		public void addToTooltip(Slot focusedSlot, ItemStack stack, List<Text> lines) {
			if (!SkyblockerConfigManager.get().helpers.chocolateFactory.enableChocolateFactoryHelper || focusedSlot == null)
				return;

			NEW_LORE_LINES.clear();

			if (prestigeSlot != null && focusedSlot.id == prestigeSlot) {
				if (!maxPrestige && chocolateRequiredForPrestige != null)
					NEW_LORE_LINES.add(Text.literal("Time until prestige: ").formatted(Formatting.GRAY).append(formatTime(chocolateRequiredForPrestige == 0 ? 0 : chocolateRequiredForPrestige / (rawCps * correctedMultiplier))));
			} else if (currentChocolate != null) {
				for (cpsUpgrade upgrade : cpsUpgrades)
					if (focusedSlot.id == upgrade.slot) {
						long chocolateRequiredForUpgrade = upgrade.cost - currentChocolate;
						NEW_LORE_LINES.add(Text.literal("Time until upgrade: ").formatted(Formatting.GRAY).append(formatTime(chocolateRequiredForUpgrade == 0 ? 0 : chocolateRequiredForUpgrade / currentCps)));
						NEW_LORE_LINES.add(Text.literal("CPS increase: ").formatted(Formatting.GRAY).append(Text.literal(NUMBER_FORMAT.format(upgrade.cpsIncrease)).formatted(Formatting.GOLD)));
						NEW_LORE_LINES.add(Text.literal("Cost per CPS: ").formatted(Formatting.GRAY).append(Text.literal(NUMBER_FORMAT.format(upgrade.costPerCpsIncrease)).formatted(Formatting.GOLD)));
						break;
					}

				for (upgrade upgrade : upgrades)
					if (focusedSlot.id == upgrade.slot) {
						NEW_LORE_LINES.add(Text.literal("Time until upgrade: ").formatted(Formatting.GRAY).append(formatTime((upgrade.cost - currentChocolate) / currentCps)));
						break;
					}
			}

			if (!NEW_LORE_LINES.isEmpty()) {
				lines.add(LineSmoother.createSmoothLine());
				lines.addAll(NEW_LORE_LINES);
			}
		}
	}
}
