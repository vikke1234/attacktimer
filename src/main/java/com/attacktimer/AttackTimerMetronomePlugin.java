package com.attacktimer;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import java.awt.event.KeyEvent;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.http.api.item.ItemEquipmentStats;
import net.runelite.http.api.item.ItemStats;

import java.awt.*;

@PluginDescriptor(
        name = "Attack Timer Metronome",
        description = "Shows a visual cue on an overlay every game tick to help timing based activities",
        tags = {"timers", "overlays", "tick", "skilling"}
)
public class AttackTimerMetronomePlugin extends Plugin implements KeyListener
{
    public enum AttackState {
        NOT_ATTACKING,
        DELAYED,
        PENDING,
    };

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ConfigManager configManager;

    @Inject
    private AttackTimerMetronomeTileOverlay tileOverlay;

    @Inject
    private FullResizableAttackTimerMetronomeOverlay overlay;

    @Inject
    private AttackTimerMetronomeConfig config;

    @Inject
    private KeyManager keyManager;

    @Inject
    private ItemManager itemManager;

    @Inject
    private Client client;

    private boolean CurrentTick = true;
    public int tickCounter = 0;
    public int tickPeriod = 0;

    final int ATTACK_DELAY_NONE = 0;

    public int attackDelayTicks = ATTACK_DELAY_NONE;

    public AttackState attackState = AttackState.NOT_ATTACKING;

    public Color CurrentColor = Color.WHITE;

    public Dimension DEFAULT_SIZE = new Dimension(25, 25);

    @Provides
    AttackTimerMetronomeConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(AttackTimerMetronomeConfig.class);
    }

    private ItemStats getItemStatsFromContainer(ItemContainer container, int slotID)
    {
        final Item item = container.getItem(slotID);
        return item != null ? itemManager.getItemStats(item.getId(), false) : null;
    }
    private ItemStats getWeaponStats()
    {
        return getItemStatsFromContainer(client.getItemContainer(InventoryID.EQUIPMENT),
                EquipmentInventorySlot.WEAPON.getSlotIdx());
    }

    private int getWeaponSpeed()
    {
        ItemStats weaponStats = getWeaponStats();
        ItemEquipmentStats e = weaponStats.getEquipment();

        return e.getAspeed(); // Deadline for next available attack.
    }

    private boolean isPlayerAttacking()
    {
        return AnimationData.fromId(client.getLocalPlayer().getAnimation()) != null;
    }

    private void performAttack()
    {
        attackState = AttackState.DELAYED;
        tickCounter = 0;
        tickPeriod = getWeaponSpeed();
    }
    @Subscribe
    public void onInteractingChanged(InteractingChanged interactingChanged)
    {
        Actor source = interactingChanged.getSource();
        Actor target = interactingChanged.getTarget();

        Player p = client.getLocalPlayer();

        if (source.equals(p) && (target instanceof NPC)) {

            switch (attackState) {
                case NOT_ATTACKING:
                    // If not previously attacking, this action can result in a queued attack or
                    // an instant attack. If its queued, don't trigger the cooldown yet.
                    if (isPlayerAttacking()) {
                       performAttack();
                    } else {
                        attackState = AttackState.PENDING;
                    }
                    break;

                case PENDING:
                case DELAYED:
                    // Don't reset tick counter or tick period.
                    break;
            };
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        // Tick count == 1 means to cycle through configured colors.

        // changes color every tick
        // Color cycle is the number of ticks.

        switch (attackState) {
            case PENDING:
                if (isPlayerAttacking()) {
                    performAttack(); // Sets state to DELAYED.
                }
                break;
            case NOT_ATTACKING:
                return;
        }

        if (tickCounter >= tickPeriod)
        {
            tickCounter = 0;
            // Update attack state; if attacking, set to delayed. Else, no attack.

            if (isPlayerAttacking()) {
                // Found an attack animation. Assume auto attack triggered.
                attackState = AttackState.DELAYED;
            } else {
                // No attack animation; assume no attack.
                attackState = AttackState.NOT_ATTACKING;
                return;
            }
        }

        tickCounter++;

        // Index into color array.
        switch (tickCounter)
        {
            case 1:
                CurrentColor = config.getTickColor();
                break;
            case 2:
                CurrentColor = config.getTockColor();
                break;
            case 3:
                CurrentColor = config.getTick3Color();
                break;
            case 4:
                CurrentColor = config.getTick4Color();
                break;
            case 5:
                CurrentColor = config.getTick5Color();
                break;
            case 6:
                CurrentColor = config.getTick6Color();
                break;
            case 7:
                CurrentColor = config.getTick7Color();
                break;
            case 8:
                CurrentColor = config.getTick8Color();
                break;
            case 9:
                CurrentColor = config.getTick9Color();
                break;
            case 10:
                CurrentColor = config.getTick10Color();
        }
    }


    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (tickCounter > config.colorCycle())
        {
            tickCounter = 0;
        }
        DEFAULT_SIZE = new Dimension(config.boxWidth(), config.boxWidth());
    }

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
        overlay.setPreferredSize(DEFAULT_SIZE);
        overlayManager.add(tileOverlay);
        keyManager.registerKeyListener(this);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
        overlayManager.remove(tileOverlay);
        tickCounter = 0;
        keyManager.unregisterKeyListener(this);
    }

    //hotkey settings
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (config.tickResetHotkey().matches(e))
        {
            tickCounter = 0;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
