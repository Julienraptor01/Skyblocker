package de.hysky.skyblocker.utils.render.gui;

public record ColorHighlight(int slot, int color) {
	public static final int BLACK = 0x000000;
	public static final int DARK_BLUE = 0x0000AA;
	public static final int DARK_GREEN = 0x00AA00;
	public static final int DARK_AQUA = 0x00AAAA;
	public static final int DARK_RED = 0xAA0000;
	public static final int DARK_PURPLE = 0xAA00AA;
	public static final int GOLD = 0xFFAA00;
	public static final int GRAY = 0xAAAAAA;
	public static final int DARK_GRAY = 0x555555;
	public static final int BLUE = 0x5555FF;
	public static final int GREEN = 0x55FF55;
	public static final int AQUA = 0x55FFFF;
	public static final int RED = 0xFF5555;
	public static final int LIGHT_PURPLE = 0xFF55FF;
	public static final int YELLOW = 0xFFFF55;
	public static final int WHITE = 0xFFFFFF;
	public static final int MINECOIN = 0xDDD605;
	public static final int MATERIAL_QUARTZ = 0xE3D4D1;
	public static final int MATERIAL_IRON = 0xCECACA;
	public static final int MATERIAL_NETHERITE = 0x443A3B;
	public static final int MATERIAL_REDSTONE = 0x971607;
	public static final int MATERIAL_COPPER = 0xB4684D;
	public static final int MATERIAL_GOLD = 0xDEB12D;
	public static final int MATERIAL_EMERALD = 0x47A036;
	public static final int MATERIAL_DIAMOND = 0x2CBAA8;
	public static final int MATERIAL_LAPIS = 0x21497B;
	public static final int MATERIAL_AMETHYST = 0x9A5CC6;

	public static ColorHighlight black(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | BLACK);
	}

	public static ColorHighlight darkBlue(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | DARK_BLUE);
	}

	public static ColorHighlight darkGreen(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | DARK_GREEN);
	}

	public static ColorHighlight darkAqua(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | DARK_AQUA);
	}

	public static ColorHighlight darkRed(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | DARK_RED);
	}

	public static ColorHighlight darkPurple(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | DARK_PURPLE);
	}

	public static ColorHighlight gold(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | GOLD);
	}

	public static ColorHighlight gray(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | GRAY);
	}

	public static ColorHighlight darkGray(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | DARK_GRAY);
	}

	public static ColorHighlight blue(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | BLUE);
	}

	public static ColorHighlight green(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | GREEN);
	}

	public static ColorHighlight aqua(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | AQUA);
	}

	public static ColorHighlight red(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | RED);
	}

	public static ColorHighlight lightPurple(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | LIGHT_PURPLE);
	}

	public static ColorHighlight yellow(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | YELLOW);
	}

	public static ColorHighlight white(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | WHITE);
	}

	public static ColorHighlight minecoin(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | MINECOIN);
	}

	public static ColorHighlight materialQuartz(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | MATERIAL_QUARTZ);
	}

	public static ColorHighlight materialIron(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | MATERIAL_IRON);
	}

	public static ColorHighlight materialNetherite(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | MATERIAL_NETHERITE);
	}

	public static ColorHighlight materialRedstone(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | MATERIAL_REDSTONE);
	}

	public static ColorHighlight materialCopper(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | MATERIAL_COPPER);
	}

	public static ColorHighlight materialGold(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | MATERIAL_GOLD);
	}

	public static ColorHighlight materialEmerald(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | MATERIAL_EMERALD);
	}

	public static ColorHighlight materialDiamond(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | MATERIAL_DIAMOND);
	}

	public static ColorHighlight materialLapis(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | MATERIAL_LAPIS);
	}

	public static ColorHighlight materialAmethyst(int slot) {
		return new ColorHighlight(slot, 0x80 << 24 | MATERIAL_AMETHYST);
	}

	public static ColorHighlight fromRGB(int slot, int RGB) {
		return new ColorHighlight(slot, 0x80 << 24 | (RGB & 0xFFFFFF));
	}

	public static ColorHighlight fromRGB(int slot, byte r, byte g, byte b) {
		return new ColorHighlight(slot, 0x80 << 24 | r << 16 | g << 8 | b);
	}

	public static ColorHighlight fromARGB(int slot, int ARGB) {
		return new ColorHighlight(slot, ARGB);
	}

	public static ColorHighlight fromARGB(int slot, byte a, byte r, byte g, byte b) {
		return new ColorHighlight(slot, a << 24 | r << 16 | g << 8 | b);
	}
}