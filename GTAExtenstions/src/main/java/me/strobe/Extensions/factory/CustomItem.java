package me.strobe.Extensions.factory;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * This is CustomItem in Project: (Gta-Core) : But you already knew that
 *
 * @author G.P of Prentice Productions
 * @version 1.0
 * Created On    : 2/25/2022, 9:04 AM
 * Last Edit     : 2/25/2022, 9:04 AM (Update Me!)
 * Time to Write : (Rough Estimate)
 * <p>
 * (Class Description)
 */
@Getter @SerializableAs("CustomItem")
public abstract class CustomItem implements ConfigurationSerializable {

    protected CustomItem(ItemStack item,
                         boolean isStackable, short maxStackSize,
                         boolean isContainer,
                         boolean doesBreakAfterSetUses,  short totalNumUses,
                         boolean isRepairable, ItemStack itemThatRepairsThis, double percentToRepairPerItem, int flatDurabilityRestoredPerItem,
                         boolean isWearable, EquipmentSlot activeSlot,
                         boolean isArmor, short defenseValue,
                         boolean isDropOnDeath,
                         boolean isDropOutsideInventory,
                         short maxContents) {

        Validate.notNull(item);
        this.item = item;
        this.nbtWrapper = new NBTItem(item);
        this.isDropOnDeath = isDropOnDeath;
        this.isDropOutsideInventory = isDropOutsideInventory;

        checkStackableTag(isStackable, maxStackSize,this);

        checkContainerTag(isContainer, maxContents, this);

        checkWearableTag(isWearable, activeSlot, this);

        checkArmorTag(isArmor, defenseValue, activeSlot,this);

        checkDurabilityTag(doesBreakAfterSetUses, totalNumUses, this);

        checkRepairabilityTag(isRepairable, itemThatRepairsThis, percentToRepairPerItem, flatDurabilityRestoredPerItem, totalNumUses, this);

    }

    private void checkContainerTag(boolean isContainer, short maxContents, CustomItem item){
        if(isContainer){
            item.isContainer = true;
            item.isStackable = false;
            Validate.inclusiveBetween(Short.MAX_VALUE, 1, maxContents);
            item.maxContents = maxContents;
            item.contents = new ArrayList<>(item.maxContents);
            item.currentContents = 0;
        }
    }

    private void checkStackableTag(boolean isStackable, short maxStackSize, CustomItem item){
        if(isStackable){
            item.isStackable = true;
            item.isContainer = false;
            Validate.inclusiveBetween(64, 1, maxStackSize);
            item.maxStackSize = maxStackSize;
            item.contents = null;
            item.maxContents = 0;
        }
    }

    private void checkWearableTag(boolean isWearable, EquipmentSlot activeSlot, CustomItem item){
        if(isWearable){
            item.isWearable = true;
            Validate.notNull(activeSlot);
            item.activeSlot = activeSlot;
        }
    }

    private void checkArmorTag(boolean isArmor, short defenseValue, EquipmentSlot activeSlot, CustomItem item){
        if(isArmor){
            item.isArmor = true;
            Validate.inclusiveBetween(Short.MAX_VALUE, 1, defenseValue);
            item.defenseValue = defenseValue;
            item.isWearable = true;
            Validate.notNull(activeSlot);
            item.activeSlot = activeSlot;
        }
    }

    private void checkDurabilityTag(boolean doesBreakAfterSetUses, short totalNumUses, CustomItem item){
        if(doesBreakAfterSetUses){
            item.doesBreakAfterSetUses = true;
            Validate.inclusiveBetween(Short.MAX_VALUE, 1, totalNumUses);
            item.totalNumUses = totalNumUses;
            item.numUsesLeft = item.totalNumUses;
        }
    }

    private void checkRepairabilityTag(boolean isRepairable, ItemStack itemThatRepairsThis,
                                       double percentToRepairPerItem, int flatDurabilityRestoredPerItem,
                                       short totalNumUses, CustomItem item){
        if(isRepairable){
            item.isRepairable = true;
            Validate.notNull(itemThatRepairsThis);
            item.itemThatRepairsThis = itemThatRepairsThis;
            item.percentToRepairPerItem = percentToRepairPerItem;
            item.flatDurabilityRestoredPerItem = flatDurabilityRestoredPerItem;
            item.totalNumUses = totalNumUses;
            item.numUsesLeft = item.totalNumUses;
        }
    }

    /** The item in game */
    private final ItemStack item;

    /** The nbt wrapper that allows for editing nbt data of the item */
    private final NBTItem nbtWrapper;

    /**
     * Determines if this item will stack with others of this type.
     *
     * @apiNote This should never be true if #isContainer is set.
     */
    private boolean isStackable;
    /**
     * Companion variable for #isStackable, Limits the ingame amount of items to this many
     *
     * @apiNote only has a value if #isStackable is true.
     */
    private short maxStackSize = 1;
    /**
     * Represents the total of items stacked within this stack.
     */
    private short currentlyStacked = 1;

    /**
     * Represents if this item can hold items.
     *
     * @apiNote Will never be true if #isStackable is set.
     */
    private boolean isContainer;
    /**
     * The most amount of items this item will contain.
     */
    private short maxContents = -1;
    /**
     * Represents the currently held amount of a container's internal itemstack.
     */
    private short currentContents = -1;
    /**
     * Reference to each of the items that this contain may hold
     *
     * @apiNote This will be null if #isContainer is false, maxContents == 0, or #isSingleUse
     */
    private List<ItemStack> contents;

    /**
     * Determines if this item will disappear after set number of uses
     *
     * @apiNote should always be true if #isWearable, #isArmor, or #isStackable are true.
     */
    private boolean doesBreakAfterSetUses;
    /**
     * Detemines if this item can be repaired
     */
    private boolean isRepairable;
    /**
     * The Itemstack that will be used/consumed to repair this item's uses
     */
    private ItemStack itemThatRepairsThis;
    /**
     * The percentage of the total durability that will be repaired upon consumption/use of #itemThatRepairsThis
     */
    private double percentToRepairPerItem = -1;
    /**
     * A flat amount of durability added back to the numUses upon consumption/use of #itemThatRepairsThis
     */
    private int flatDurabilityRestoredPerItem = -1;
    /**
     * Companion variable for #isSingleUse, Strictly used to determine the durability of this item.
     *
     * @apiNote Is always greater than 0, at least 1.
     */
    private short totalNumUses = -1;
    /**
     * The number of uses left on this item.
     */
    private short numUsesLeft = -1;

    /**
     * Detemines if this item can be placed in an armor slot
     */
    private boolean isWearable;
    /**
     * Determines the active slot in the inventory
     *
     * @apiNote values of 0-3 represent armor slots, 4 is used for offhand if 1.9+ 5 represents the hot bar.
     */
    private EquipmentSlot activeSlot;

    /**
     * Determines if this item should be treated as Armor
     *
     * @apiNote if this is true, then #isWearable is true.
     */
    private boolean isArmor;
    /**
     * Companion variable for #isArmor, Strictly used to mitigate damage from incoming sources if this is an armor.
     */
    private short defenseValue;

    /**
     * Determines if this item will drop on death
     *
     * @apiNote if this is set, then #isDroppable is true
     */
    private final boolean isDropOnDeath;

    /**
     * Determines if this item can be dropped outside the inventory
     *
     * @apiNote if this is set, then #isDroppable is true
     */
    private final boolean isDropOutsideInventory;

}
