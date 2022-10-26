package com.attacktimer;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import java.awt.Color;

@ConfigGroup("attacktimermetronome")
public interface AttackTimerMetronomeConfig extends Config
{
	@ConfigItem(
			position = 1,
			keyName = "enableMetronome",
			name = "Attack Timer Metronome",
			description = "Enable visual metronome"
	)
	default boolean enableMetronome()
	{
		return true;
	}

	@ConfigItem(
			position = 2,
			keyName = "highlightCurrentTile",
			name = "Enable True Tile Overlay",
			description = "Highlights true player tile using the metronome colors (replacement for tile indicator plugin setting)"
	)
	default boolean highlightCurrentTile()
	{
		return false;
	}


	@Range(
			min = 16
	)
	@ConfigItem(
			position = 3,
			keyName = "boxWidth",
			name = "Default Box Size (Alt + Right Click Box)",
			description = "Configure the default length and width of the box. Use alt + right click on the box to reset to the size specified"
	)
	default int boxWidth()
	{
		return 25;
	}

	@Range(
			min = 1
	)
	@ConfigItem(
			position = 4,
			keyName = "tickCount",
			name = "Tick Count",
			description = "The tick on which the color changes (Only supports two colors)"
	)
	default int tickCount()
	{
		return 1;
	}

	@ConfigSection(
			name = "Tick Number Settings",
			description = "Change Tick Number settings",
			position = 5
	)
	String TickNumberSettings = "Tick Number Settings";

	@ConfigItem(
			position = 1,
			keyName = "showTick",
			name = "Show Metronome Tick Number",
			description = "Shows current tick number on the metronome",
			section = TickNumberSettings
	)
	default boolean showTick()
	{
		return false;
	}

	@ConfigItem(
			position = 2,
			keyName = "showPlayerTick",
			name = "Show Tick Number Above Player",
			description = "Shows current tick number above the player",
			section = TickNumberSettings
	)
	default boolean showPlayerTick()
	{
		return false;
	}

	@ConfigItem(
			position = 3,
			keyName = "disableFontScaling",
			name = "Disable Font Size Scaling (Metronome Tick Only)",
			description = "Disables font size scaling for metronome tick number",
			section = TickNumberSettings
	)
	default boolean disableFontScaling()
	{
		return false;
	}

	@Range(
			min = 8,
			max = 50
	)
	@ConfigItem(
			position = 4,
			keyName = "fontSize",
			name = "Font Size (Overhead Tick Only)",
			description = "Change the font size of the overhead Tick Number",
			section = TickNumberSettings
	)
	default int fontSize()
	{
		return 15;
	}

	@ConfigItem(
			position = 5,
			keyName = "countColor",
			name = "Tick Number Color",
			description = "Configures the color of tick number",
			section = TickNumberSettings
	)
	default Color NumberColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
			position = 6,
			keyName = "fontType",
			name = "Font Type",
			description = "Change the font of the Tick Number",
			section = TickNumberSettings
	)
	default FontTypes fontType() { return FontTypes.REGULAR; }

	@ConfigSection(
			name = "True Tile Overlay Settings",
			description = "Settings only applied to True Tile Overlay",
			position = 6
	)
	String TileSettings = "True Tile Overlay Settings";

	@Alpha
	@ConfigItem(
			position = 1,
			keyName = "currentTileFillColor",
			name = "True Tile Fill Color",
			description = "Fill color of the true tile overlay",
			section = TileSettings
	)
	default Color currentTileFillColor()
	{
		return new Color(0, 0, 0, 50);
	}

	@ConfigItem(
			position = 2,
			keyName = "currentTileBorderWidth",
			name = "True Tile Border Width",
			description = "Border size of the true tile overlay",
			section = TileSettings
	)
	default double currentTileBorderWidth()
	{
		return 2;
	}

	@ConfigSection(
			name = "Attack Bar",
			description = "Change the colors and number of colors to cycle through",
			position = 7
	)
	String AttackBarSettings = "Attack Cooldown Bar Settings";

	@ConfigItem(
			position = 1,
			keyName = "attackBar",
			name = "Show Attack Bar",
			description = "Show the attack bar",
			section = AttackBarSettings
	)
	default boolean showBar() { return false; }

	@ConfigItem(
			position = 2,
			keyName = "attackBarHeightOffset",
			name = "Height Offset",
			description = "Height offset for the bar from top of player model",
			section =AttackBarSettings
	)
	default int heightOffset() { return 0; }

	@ConfigItem(
			position = 3,
			keyName = "attackBarEmpties",
			name = "Empties Before Attack",
			description = "Controls whether the attack bar will fully empty before a new attack can occur",
			section =AttackBarSettings
	)
	default boolean barEmpties() { return true; }

	@ConfigItem(
			position = 4,
			keyName = "attackBarFills",
			name = "Fills Before Attack",
			description = "Controls whether the attack bar will fill completely after an attack",
			section =AttackBarSettings
	)
	default boolean barFills() { return true; }

	@ConfigItem(
			position = 5,
			keyName = "attackBarDirection",
			name = "Attack Bar Fills or Drains",
			description = "Controls whether the attack bar will fill or drain as a cooldown",
			section =AttackBarSettings
	)
	default boolean barDirection() { return true; }
}

