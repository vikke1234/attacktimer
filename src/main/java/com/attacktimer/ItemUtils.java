package com.attacktimer;

import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.http.api.item.ItemEquipmentStats;
import net.runelite.http.api.item.ItemStats;

import javax.inject.Inject;
import java.util.Objects;

public class ItemUtils {
    @Inject
    private ItemManager itemManager;

    @Inject
    private Client client;

    private ItemStats getItemStatsFromContainer(ItemContainer container, int slotID)
    {
        final Item item = container.getItem(slotID);
        return item != null ? itemManager.getItemStats(item.getId(), false) : null;
    }
    private ItemStats getWeaponStats()
    {
        return getItemStatsFromContainer(Objects.requireNonNull(client.getItemContainer(InventoryID.EQUIPMENT)),
                EquipmentInventorySlot.WEAPON.getSlotIdx());
    }

    public int getWeaponSpeed()
    {
        ItemStats weaponStats = getWeaponStats();
        if (weaponStats == null) {
            return 4; // Assume barehanded == 4t
        }

        ItemEquipmentStats e = weaponStats.getEquipment();

        int speed = e.getAspeed();
        if (getAttackStyle() == AttackStyle.RANGING &&
                client.getVarpValue(VarPlayer.ATTACK_STYLE) == 1) { // Hack for index 1 => rapid
            speed -= 1; // Assume ranging == rapid.
        }

        return speed; // Deadline for next available attack.
    }

    private AttackStyle getAttackStyle()
    {
        final int currentAttackStyleVarbit = client.getVarpValue(VarPlayer.ATTACK_STYLE);
        final int currentEquippedWeaponTypeVarbit = client.getVarbitValue(Varbits.EQUIPPED_WEAPON_TYPE);
        AttackStyle[] attackStyles = WeaponType.getWeaponType(currentEquippedWeaponTypeVarbit).getAttackStyles();

        if (currentAttackStyleVarbit < attackStyles.length) {
            return attackStyles[currentAttackStyleVarbit];
        }

        return AttackStyle.ACCURATE;
    }
}
