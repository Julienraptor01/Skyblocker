package de.hysky.skyblocker.skyblock.chocolatefactory;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import de.hysky.skyblocker.SkyblockerMod;
import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.events.SkyblockEvents;
import de.hysky.skyblocker.utils.*;
import de.hysky.skyblocker.utils.command.argumenttypes.EggTypeArgumentType;
import de.hysky.skyblocker.utils.command.argumenttypes.blockpos.ClientBlockPosArgumentType;
import de.hysky.skyblocker.utils.command.argumenttypes.blockpos.ClientPosArgument;
import de.hysky.skyblocker.utils.scheduler.MessageScheduler;
import de.hysky.skyblocker.utils.waypoint.Waypoint;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class EggFinder {
	private static final String eggTypeRegex = "(Breakfast|Lunch|Dinner|Brunch|Déjeuner|Supper)";
	private static final Pattern eggFoundPattern = Pattern.compile("^(?:HOPPITY'S HUNT You found a Chocolate|You have already collected this Chocolate) " + eggTypeRegex);

	private static final Logger logger = LoggerFactory.getLogger("Skyblocker Egg Finder");
	//This is most likely unnecessary with the addition of the location change packet, but it works fine and might be doing something so might as well keep it
	private static final LinkedList<ArmorStandEntity> armorStandQueue = new LinkedList<>();
	/**
	 * The locations that the egg finder should work while the player is in.
	 */
	private static final List<Location> possibleLocations = List.of(Location.CRIMSON_ISLE, Location.DUNGEON_HUB, Location.DEEP_CAVERNS, Location.HUB, Location.THE_END, Location.GOLD_MINE, Location.CRYSTAL_HOLLOWS, Location.DWARVEN_MINES, Location.THE_FARMING_ISLAND, Location.SPIDERS_DEN, Location.THE_PARK);
	/**
	 * Whether the player is in a location where the egg finder should work.
	 * This is set to false upon world change and will be checked with the location change event afterward.
	 */
	private static boolean isLocationCorrect = false;

	private EggFinder() {
	}

	public static void init() {
		ClientPlayConnectionEvents.JOIN.register(EggFinder::invalidateState);
		SkyblockEvents.LOCATION_CHANGE.register(EggFinder::handleLocationChange);
		SkyblockTime.HOUR_CHANGE.register(EggFinder::onHourChange);
		ClientReceiveMessageEvents.GAME.register(EggFinder::onChatMessage);
		WorldRenderEvents.AFTER_TRANSLUCENT.register(EggFinder::renderWaypoints);
		ClientCommandRegistrationCallback.EVENT.register(EggFinder::registerCommand);
	}

	private static void invalidateState(ClientPlayNetworkHandler ignored, PacketSender ignored2, MinecraftClient ignored3) {
		if (!SkyblockerConfigManager.get().helpers.chocolateFactory.enableEggFinder) return;
		isLocationCorrect = false;
		for (EggType type : EggType.entries) {
			type.oddDayEgg = null;
			type.evenDayEgg = null;
			type.unknownEggs = new Egg[2];
		}
	}

	private static void handleLocationChange(Location location) {
		isLocationCorrect = possibleLocations.contains(location);
		if (!isLocationCorrect) {
			armorStandQueue.clear();
			return;
		}
		while (!armorStandQueue.isEmpty()) {
			handleArmorStand(armorStandQueue.poll());
		}
	}

	private static void onHourChange(int hour) {
		for (EggType type : EggType.entries) {
			if (hour == type.resetHour) {
				for (int i = 0; i < type.unknownEggs.length; i++) {
					if (type.unknownEggs[i] != null) {
						Entity eggEntity = type.unknownEggs[i].entity;
						Entity entity = eggEntity.getWorld().getEntityById(eggEntity.getId());
						if (entity == null || entity.getBlockPos().equals(eggEntity.getBlockPos())) {
							type.unknownEggs[i] = null;
						}
					}
				}
				if (SkyblockTime.skyblockDay.get() % 2 == 0) {
					type.evenDayEgg = null;
					type.evenDayCollected = false;
				} else {
					type.oddDayEgg = null;
					type.oddDayCollected = false;
				}
			}
		}
	}

	private static void onChatMessage(Text text, boolean overlay) {
		if (overlay || !SkyblockerConfigManager.get().helpers.chocolateFactory.enableEggFinder) return;
		Matcher matcher = eggFoundPattern.matcher(text.getString());
		if (matcher.find()) {
			try {
				String eggTypeString = matcher.group(1);
				EggType eggType = EggType.entries.stream().filter(type -> type.namePattern.matcher(eggTypeString).matches()).findFirst().orElseThrow();
				switch (eggTypeString) {
					case "Breakfast", "Lunch", "Dinner" -> {
						eggType.oddDayCollected = true;
						if (eggType.oddDayEgg != null) {
							if (eggType.evenDayEgg == null) {
								for (int i = 0; i < eggType.unknownEggs.length; i++) {
									if (eggType.unknownEggs[i] != null) {
										eggType.evenDayEgg = eggType.unknownEggs[i];
										eggType.unknownEggs[i] = null;
										break;
									}
								}
							}
						} else if (eggType.unknownEggs[0] != null && eggType.unknownEggs[1] == null) {
							eggType.oddDayEgg = eggType.unknownEggs[0];
							eggType.unknownEggs[0] = null;
						} else if (eggType.unknownEggs[0] == null && eggType.unknownEggs[1] != null) {
							eggType.oddDayEgg = eggType.unknownEggs[1];
							eggType.unknownEggs[1] = null;
						} else if (eggType.unknownEggs[0] != null && eggType.unknownEggs[1] != null) {
							ClientPlayerEntity player = MinecraftClient.getInstance().player;
							assert player != null;
							double distanceSquared0 = player.getBlockPos().getSquaredDistance(eggType.unknownEggs[0].entity.getBlockPos());
							double distanceSquared1 = player.getBlockPos().getSquaredDistance(eggType.unknownEggs[1].entity.getBlockPos());
							if (distanceSquared0 < distanceSquared1) {
								eggType.oddDayEgg = eggType.unknownEggs[0];
								eggType.unknownEggs[0] = null;
								eggType.evenDayEgg = eggType.unknownEggs[1];
								eggType.unknownEggs[1] = null;
							} else {
								eggType.oddDayEgg = eggType.unknownEggs[1];
								eggType.unknownEggs[1] = null;
								eggType.evenDayEgg = eggType.unknownEggs[0];
								eggType.unknownEggs[0] = null;
							}
						}
						eggType.oddDayEgg.waypoint.setFound();
					}
					case "Brunch", "Déjeuner", "Supper" -> {
						eggType.evenDayCollected = true;
						if (eggType.evenDayEgg != null) {
							if (eggType.oddDayEgg == null) {
								for (int i = 0; i < eggType.unknownEggs.length; i++) {
									if (eggType.unknownEggs[i] != null) {
										eggType.oddDayEgg = eggType.unknownEggs[i];
										eggType.unknownEggs[i] = null;
										break;
									}
								}
							}
						} else if (eggType.unknownEggs[0] != null && eggType.unknownEggs[1] == null) {
							eggType.evenDayEgg = eggType.unknownEggs[0];
							eggType.unknownEggs[0] = null;
						} else if (eggType.unknownEggs[0] == null && eggType.unknownEggs[1] != null) {
							eggType.evenDayEgg = eggType.unknownEggs[1];
							eggType.unknownEggs[1] = null;
						} else if (eggType.unknownEggs[0] != null && eggType.unknownEggs[1] != null) {
							ClientPlayerEntity player = MinecraftClient.getInstance().player;
							assert player != null;
							double distanceSquared0 = player.getBlockPos().getSquaredDistance(eggType.unknownEggs[0].entity.getBlockPos());
							double distanceSquared1 = player.getBlockPos().getSquaredDistance(eggType.unknownEggs[1].entity.getBlockPos());
							if (distanceSquared0 < distanceSquared1) {
								eggType.evenDayEgg = eggType.unknownEggs[0];
								eggType.unknownEggs[0] = null;
								eggType.oddDayEgg = eggType.unknownEggs[1];
								eggType.unknownEggs[1] = null;
							} else {
								eggType.evenDayEgg = eggType.unknownEggs[1];
								eggType.unknownEggs[1] = null;
								eggType.oddDayEgg = eggType.unknownEggs[0];
								eggType.unknownEggs[0] = null;
							}
						}
						eggType.evenDayEgg.waypoint.setFound();
					}
				}
			} catch (NoSuchElementException e) {
				logger.error("[Skyblocker Egg Finder] Failed to find egg type for egg found message. Tried to match against: {}", matcher.group(0), e);
			} catch (NullPointerException e) {
				logger.warn("----- Egg Finder States Dump Start -----");
				logger.info("isLocationCorrect: {}", isLocationCorrect);
				for (EggType type : EggType.entries) {
					logger.info("EggType: {}", type);
					logger.info("messageLastSent: {}", type.messageLastSent);
					if (type.oddDayEgg != null) {
						logger.info("oddDayEgg: {}", type.oddDayEgg);
						if (type.oddDayEgg.entity != null) {
							logger.info("oddDayEgg.entity: {}", type.oddDayEgg.entity);
							NbtCompound nbt = new NbtCompound();
							type.oddDayEgg.entity.writeCustomDataToNbt(nbt);
							logger.info("oddDayEgg.entity.nbt: {}", nbt);
							logger.info("oddDayEgg.entity.getBlockPos(): {}", type.oddDayEgg.entity.getBlockPos());
						} else {
							logger.info("oddDayEgg.entity: null");
						}
						if (type.oddDayEgg.waypoint != null) {
							logger.info("oddDayEgg.waypoint: {}", type.oddDayEgg.waypoint);
							logger.info("oddDayEgg.waypoint.shouldRender(): {}", type.oddDayEgg.waypoint.shouldRender());
							logger.info("oddDayEgg.waypoint.pos: {}", type.oddDayEgg.waypoint.pos);
						} else {
							logger.info("oddDayEgg.waypoint: null");
						}
					} else {
						logger.info("oddDayEgg: null");
					}
					logger.info("oddDayCollected: {}", type.oddDayCollected);
					if (type.evenDayEgg != null) {
						logger.info("evenDayEgg: {}", type.evenDayEgg);
						if (type.evenDayEgg.entity != null) {
							logger.info("evenDayEgg.entity: {}", type.evenDayEgg.entity);
							NbtCompound nbt = new NbtCompound();
							type.evenDayEgg.entity.writeCustomDataToNbt(nbt);
							logger.info("evenDayEgg.entity.nbt: {}", nbt);
							logger.info("evenDayEgg.entity.getBlockPos(): {}", type.evenDayEgg.entity.getBlockPos());
						} else {
							logger.info("evenDayEgg.entity: null");
						}
						if (type.evenDayEgg.waypoint != null) {
							logger.info("evenDayEgg.waypoint: {}", type.evenDayEgg.waypoint);
							logger.info("evenDayEgg.waypoint.shouldRender(): {}", type.evenDayEgg.waypoint.shouldRender());
							logger.info("evenDayEgg.waypoint.pos: {}", type.evenDayEgg.waypoint.pos);
						} else {
							logger.info("evenDayEgg.waypoint: null");
						}
					} else {
						logger.info("evenDayEgg: null");
					}
					logger.info("evenDayCollected: {}", type.evenDayCollected);
					if (type.unknownEggs[0] != null) {
						logger.info("unknownEggs[0]: {}", type.unknownEggs[0]);
						if (type.unknownEggs[0].entity != null) {
							logger.info("unknownEggs[0].entity: {}", type.unknownEggs[0].entity);
							NbtCompound nbt = new NbtCompound();
							type.unknownEggs[0].entity.writeCustomDataToNbt(nbt);
							logger.info("unknownEggs[0].entity.nbt: {}", nbt);
							logger.info("unknownEggs[0].entity.getBlockPos(): {}", type.unknownEggs[0].entity.getBlockPos());
						} else {
							logger.info("unknownEggs[0].entity: null");
						}
						if (type.unknownEggs[0].waypoint != null) {
							logger.info("unknownEggs[0].waypoint: {}", type.unknownEggs[0].waypoint);
							logger.info("unknownEggs[0].waypoint.shouldRender(): {}", type.unknownEggs[0].waypoint.shouldRender());
							logger.info("unknownEggs[0].waypoint.pos: {}", type.unknownEggs[0].waypoint.pos);
						} else {
							logger.info("unknownEggs[0].waypoint: null");
						}
					} else {
						logger.info("unknownEggs[0]: null");
					}
					if (type.unknownEggs[1] != null) {
						logger.info("unknownEggs[1]: {}", type.unknownEggs[1]);
						if (type.unknownEggs[1].entity != null) {
							logger.info("unknownEggs[1].entity: {}", type.unknownEggs[1].entity);
							NbtCompound nbt = new NbtCompound();
							type.unknownEggs[1].entity.writeCustomDataToNbt(nbt);
							logger.info("unknownEggs[1].entity.nbt: {}", nbt);
							logger.info("unknownEggs[1].entity.getBlockPos(): {}", type.unknownEggs[1].entity.getBlockPos());
						} else {
							logger.info("unknownEggs[1].entity: null");
						}
						if (type.unknownEggs[1].waypoint != null) {
							logger.info("unknownEggs[1].waypoint: {}", type.unknownEggs[1].waypoint);
							logger.info("unknownEggs[1].waypoint.shouldRender(): {}", type.unknownEggs[1].waypoint.shouldRender());
							logger.info("unknownEggs[1].waypoint.pos: {}", type.unknownEggs[1].waypoint.pos);
						} else {
							logger.info("unknownEggs[1].waypoint: null");
						}
					} else {
						logger.info("unknownEggs[1]: null");
					}
				}
				logger.warn("----- Egg Finder States Dump End -----");
				assert MinecraftClient.getInstance().player != null;
				MinecraftClient.getInstance().player.sendMessage(Constants.PREFIX.get().append("OwO The egg findew got cowwuwpted!\nPwease send me the wogs UwU").formatted(Formatting.RED));
			}
		}
	}

	private static void renderWaypoints(WorldRenderContext context) {
		if (!SkyblockerConfigManager.get().helpers.chocolateFactory.enableEggFinder) return;
		for (EggType type : EggType.entries) {
			Egg egg = type.oddDayEgg;
			if (egg != null && egg.waypoint.shouldRender())
				egg.waypoint.render(context);
			egg = type.evenDayEgg;
			if (egg != null && egg.waypoint.shouldRender())
				egg.waypoint.render(context);
			for (int i = 0; i < type.unknownEggs.length; i++) {
				egg = type.unknownEggs[i];
				if (egg != null)
					egg.waypoint.render(context);
			}
		}
	}

	private static void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(literal(SkyblockerMod.NAMESPACE).then(literal("eggFinder").then(literal("shareLocation").then(argument("blockPos", ClientBlockPosArgumentType.blockPos()).then(argument("eggType", EggTypeArgumentType.eggType()).executes(context -> {
			MessageScheduler.INSTANCE.sendMessageAfterCooldown("[Skyblocker] Chocolate " + context.getArgument("eggType", EggType.class) + " Egg found at " + context.getArgument("blockPos", ClientPosArgument.class).toAbsoluteBlockPos(context.getSource()).toShortString() + "!");
			return Command.SINGLE_SUCCESS;
		}))))));
	}

	private static void handleArmorStand(ArmorStandEntity armorStand) {
		for (ItemStack itemStack : armorStand.getArmorItems()) {
			ItemUtils.getHeadTextureOptional(itemStack).ifPresent(texture -> {
				for (EggType type : EggType.entries) { //Compare blockPos rather than entity to avoid incorrect matches when the entity just moves rather than a new one being spawned elsewhere
					if (texture.equals(type.texture)) {
						BlockPos armorStandPos = armorStand.getBlockPos();
						if ((type.oddDayEgg == null || !type.oddDayEgg.entity.getBlockPos().equals(armorStandPos)) && (type.evenDayEgg == null || !type.evenDayEgg.entity.getBlockPos().equals(armorStandPos)) && (type.unknownEggs[0] == null || !type.unknownEggs[0].entity.getBlockPos().equals(armorStandPos)) && (type.unknownEggs[1] == null || !type.unknownEggs[1].entity.getBlockPos().equals(armorStandPos))) {
							handleFoundEgg(armorStand, type);
							return;
						}
					}
				}
			});
		}
	}

	private static void handleFoundEgg(ArmorStandEntity entity, EggType eggType) {
		Egg egg = new Egg(entity, new Waypoint(entity.getBlockPos().up(2), SkyblockerConfigManager.get().helpers.chocolateFactory.waypointType, ColorUtils.getFloatComponents(eggType.color)));
		if (eggType.oddDayEgg != null && eggType.evenDayEgg == null) {
			eggType.evenDayEgg = egg;
			if (eggType.evenDayCollected)
				egg.waypoint.setFound();
		} else if (eggType.oddDayEgg == null && eggType.evenDayEgg != null) {
			eggType.oddDayEgg = egg;
			if (eggType.oddDayCollected)
				egg.waypoint.setFound();
		} else {
			if (eggType.unknownEggs[0] == null) {
				eggType.unknownEggs[0] = egg;
			} else {
				eggType.unknownEggs[1] = egg;
			}
		}
		if (!SkyblockerConfigManager.get().helpers.chocolateFactory.sendEggFoundMessages || System.currentTimeMillis() - eggType.messageLastSent < 1000)
			return;
		eggType.messageLastSent = System.currentTimeMillis();
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		assert player != null;
		player.sendMessage(Constants.PREFIX.get().append("Found a ").append(Text.literal("Chocolate " + eggType + " Egg").withColor(eggType.color)).append(" at " + entity.getBlockPos().up(2).toShortString() + "!").styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skyblocker eggFinder shareLocation " + PosUtils.toSpaceSeparatedString(egg.waypoint.pos) + " " + eggType)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to share the location in chat!").formatted(Formatting.GREEN)))));
	}

	public static void checkIfEgg(Entity entity) {
		if (entity instanceof ArmorStandEntity armorStand) checkIfEgg(armorStand);
	}

	public static void checkIfEgg(ArmorStandEntity armorStand) {
		if (!SkyblockerConfigManager.get().helpers.chocolateFactory.enableEggFinder) return;
		if (SkyblockTime.skyblockSeason.get() != SkyblockTime.Season.SPRING) return;
		if (armorStand.hasCustomName() || !armorStand.isInvisible() || !armorStand.shouldHideBasePlate()) return;
		if (Utils.getLocation() == Location.UNKNOWN) { //The location is unknown upon world change and will be changed via location change packets soon, so we can queue it for now
			armorStandQueue.add(armorStand);
			return;
		}
		if (isLocationCorrect) handleArmorStand(armorStand);
	}

	@SuppressWarnings({"DataFlowIssue", "SpellCheckingInspection"})
	public enum EggType {
		BREAKFAST(Pattern.compile("^(?:Breakfast|Brunch)$"), Formatting.GOLD.getColorValue(), "ewogICJ0aW1lc3RhbXAiIDogMTcxMTQ2MjY3MzE0OSwKICAicHJvZmlsZUlkIiA6ICJiN2I4ZTlhZjEwZGE0NjFmOTY2YTQxM2RmOWJiM2U4OCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBbmFiYW5hbmFZZzciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTQ5MzMzZDg1YjhhMzE1ZDAzMzZlYjJkZjM3ZDhhNzE0Y2EyNGM1MWI4YzYwNzRmMWI1YjkyN2RlYjUxNmMyNCIKICAgIH0KICB9Cn0", 7), LUNCH(Pattern.compile("^(?:Lunch|Déjeuner)$"), Formatting.BLUE.getColorValue(), "ewogICJ0aW1lc3RhbXAiIDogMTcxMTQ2MjU2ODExMiwKICAicHJvZmlsZUlkIiA6ICI3NzUwYzFhNTM5M2Q0ZWQ0Yjc2NmQ4ZGUwOWY4MjU0NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJSZWVkcmVsIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzdhZTZkMmQzMWQ4MTY3YmNhZjk1MjkzYjY4YTRhY2Q4NzJkNjZlNzUxZGI1YTM0ZjJjYmM2NzY2YTAzNTZkMGEiCiAgICB9CiAgfQp9", 14), DINNER(Pattern.compile("^(?:Dinner|Supper)$"), Formatting.GREEN.getColorValue(), "ewogICJ0aW1lc3RhbXAiIDogMTcxMTQ2MjY0OTcwMSwKICAicHJvZmlsZUlkIiA6ICI3NGEwMzQxNWY1OTI0ZTA4YjMyMGM2MmU1NGE3ZjJhYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNZXp6aXIiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVlMzYxNjU4MTlmZDI4NTBmOTg1NTJlZGNkNzYzZmY5ODYzMTMxMTkyODNjMTI2YWNlMGM0Y2M0OTVlNzZhOCIKICAgIH0KICB9Cn0", 21);

		//This is to not create an array each time we iterate over the values
		public static final ObjectImmutableList<EggType> entries = ObjectImmutableList.of(EggType.values());
		private final Pattern namePattern;
		private final int color;
		private final String texture;
		private final int resetHour;
		private Egg[] unknownEggs = new Egg[2];
		private Egg oddDayEgg = null;
		private Egg evenDayEgg = null;
		private boolean oddDayCollected = false;
		private boolean evenDayCollected = false;
		/*
			When a new egg spawns in the player's range, the order of packets/messages goes like this:
			set_equipment → new egg message → set_entity_data
			We have to set the egg to null to prevent the highlight from staying where it was before the new egg spawned,
			and doing so causes the found message to get sent twice.
			This is the reason for the existence of this field, so that we don't send the 2nd message.
			This doesn't fix the field being set twice, but that's not an issue anyway.
			It'd be much harder to fix the highlight issue mentioned above if it wasn't being set twice.
		 */
		private long messageLastSent = 0;

		EggType(Pattern namePattern, int color, String texture, int resetHour) {
			this.namePattern = namePattern;
			this.color = color;
			this.texture = texture;
			this.resetHour = resetHour;
		}

		@Override
		public String toString() {
			return WordUtils.capitalizeFully(this.name());
		}
	}

	record Egg(ArmorStandEntity entity, Waypoint waypoint) {
	}
}
