package com.attacktimer;

import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;
import javax.inject.Inject;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.OverlayUtil;

public class FullResizableAttackTimerMetronomeOverlay extends Overlay
{

    private final AttackTimerMetronomeConfig config;
    private final AttackTimerMetronomePlugin plugin;

    private static int TITLE_PADDING = 10;
    private static final int MINIMUM_SIZE = 16; // too small and resizing becomes impossible, requiring a reset

    @Inject
    public FullResizableAttackTimerMetronomeOverlay(AttackTimerMetronomeConfig config, AttackTimerMetronomePlugin plugin)
    {
        super(plugin);
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setMinimumSize(MINIMUM_SIZE);
        setResizable(true);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (plugin.attackState != AttackTimerMetronomePlugin.AttackState.DELAYED) {
            return null;
        }

        int ticksRemaining = plugin.getTicksUntilNextAttack();
        Dimension preferredSize = getPreferredSize();

        if (preferredSize == null)
        {
            // if this happens, reset to default - should be rare, but eg. alt+rightclick will cause this
            preferredSize = plugin.DEFAULT_SIZE;
            setPreferredSize(preferredSize);
        }

        if (config.enableMetronome())
        {
            graphics.setColor(plugin.CurrentColor);
            // Parition the rectangle by color here
            graphics.fillRect(0, 0, preferredSize.width, preferredSize.height);
            TITLE_PADDING = (Math.min(preferredSize.width, preferredSize.height) / 2 - 4); // scales tick number position with box size

            if (config.showTick())
            {
                if (config.disableFontScaling())
                {
                    graphics.setColor(config.NumberColor());

                    // Show count down instead.
                    // plugin.tickCounter => ticksRemaining
                    graphics.drawString(String.valueOf(ticksRemaining), TITLE_PADDING, preferredSize.height - TITLE_PADDING);
                } else {

                    if (config.fontType() == FontTypes.REGULAR)
                    {
                        graphics.setFont(new Font(FontManager.getRunescapeFont().getName(), Font.PLAIN, Math.min(preferredSize.width, preferredSize.height))); //scales font size based on the size of the metronome
                    }
                    else
                        {
                        graphics.setFont(new Font(config.fontType().toString(), Font.PLAIN, Math.min(preferredSize.width, Math.min(preferredSize.width, preferredSize.height))));
                    }

                    final Point tickCounterPoint = new Point(preferredSize.width / 3, preferredSize.height);
                    OverlayUtil.renderTextLocation(graphics, tickCounterPoint, String.valueOf(ticksRemaining), config.NumberColor());
                }
            }
        }

        return preferredSize;
    }
}
