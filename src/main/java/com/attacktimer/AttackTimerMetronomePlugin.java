package com.attacktimer;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;

import java.awt.*;

@PluginDescriptor(
        name = "Attack Timer Metronome",
        description = "Shows a visual cue on an overlay every game tick to help timing based activities",
        tags = {"timers", "overlays", "tick", "skilling"}
)
public class AttackTimerMetronomePlugin extends Plugin
{
    public enum AttackState {
        NOT_ATTACKING,
        DELAYED,
        //PENDING,
    }

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ConfigManager configManager;

    @Inject
    private AttackTimerMetronomeTileOverlay overlay;

    @Inject
    private AttackTimerBarOverlay barOverlay;

    @Inject
    private AttackTimerMetronomeConfig config;

    @Inject
    private ItemUtils itemUtils;

    @Inject
    private Client client;

    public int tickPeriod = 0;

    final int ATTACK_DELAY_NONE = 0;

    private int uiUnshowDebounceTicksMax = 1;

    public int attackDelayHoldoffTicks = ATTACK_DELAY_NONE;

    public AttackState attackState = AttackState.NOT_ATTACKING;

    public Color CurrentColor = Color.WHITE;

    public int DEFAULT_SIZE_UNIT_PX = 25;

    private final int DEFAULT_FOOD_ATTACK_DELAY_TICKS = 3;
    private final int KARAMBWAN_ATTACK_DELAY_TICKS = 2;


    public Dimension DEFAULT_SIZE = new Dimension(DEFAULT_SIZE_UNIT_PX, DEFAULT_SIZE_UNIT_PX);

    @Provides
    AttackTimerMetronomeConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(AttackTimerMetronomeConfig.class);
    }

    private boolean isPlayerAttacking()
    {
        return AnimationData.fromId(client.getLocalPlayer().getAnimation()) != null;
    }

    private void performAttack()
    {
        attackState = AttackState.DELAYED;
        attackDelayHoldoffTicks = itemUtils.getWeaponSpeed();
        tickPeriod = attackDelayHoldoffTicks;
    }

    public int getTicksUntilNextAttack()
    {
        return attackDelayHoldoffTicks;
    }

    public int getWeaponPeriod()
    {
        return tickPeriod;
    }

    public boolean isAttackCooldownPending()
    {
        return (attackState == AttackState.DELAYED) || attackDelayHoldoffTicks > 1;
    }

    @Subscribe(priority = 3)
    public void onAnimationChanged(AnimationChanged animation) {
        Actor actor = animation.getActor();
        boolean isPlayer = actor instanceof Player;
        boolean isLocalPlayer = actor.equals(client.getLocalPlayer());
        boolean attackTicks = attackDelayHoldoffTicks > 1;

        if (isPlayer || isLocalPlayer) {
            return;
        }

        if (attackTicks) {
            return;
        }

        if(isPlayerAttacking()) {
            performAttack();
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        if (attackDelayHoldoffTicks >= 1) {
            System.out.println("tick: " + attackDelayHoldoffTicks);
            attackDelayHoldoffTicks--;
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (event.getType() != ChatMessageType.SPAM)
        {
            return;
        }

        final String message = event.getMessage();

        if (message.startsWith("You eat") ||
                message.startsWith("You drink the wine")) {
            int attackDelay = (message.toLowerCase().contains("karambwan")) ?
                    KARAMBWAN_ATTACK_DELAY_TICKS :
                    DEFAULT_FOOD_ATTACK_DELAY_TICKS;

            if (attackState == AttackState.DELAYED) {
                attackDelayHoldoffTicks += attackDelay;
            }
        }
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged interactingChanged)
    {
    }


    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        attackDelayHoldoffTicks = 0;
    }

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
        overlay.setPreferredSize(DEFAULT_SIZE);
        overlayManager.add(barOverlay);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
        overlayManager.remove(barOverlay);
        attackDelayHoldoffTicks = 0;
    }
}
