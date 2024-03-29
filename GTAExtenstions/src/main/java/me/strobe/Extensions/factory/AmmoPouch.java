package me.strobe.Extensions.factory;

import me.strobe.Extensions.Main;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * This is AmmoPouch in Project: (Gta-Core) : But you already knew that
 *
 * @author G.P of Prentice Productions
 * @version 1.0
 * Created On    : 2/25/2022, 10:18 AM
 * Last Edit     : 2/25/2022, 10:18 AM (Update Me!)
 * Time to Write : (Rough Estimate)
 *
 * (Class Description)
 */
public class AmmoPouch extends CustomItem {

    private ItemStack ammo;

    public AmmoPouch(ItemStack representation, String ammoType, short capacity){
        super(representation, false, (short) 0,
                true, false,
                (short) 0, false, null,
                0, 0,
                true, EquipmentSlot.HAND, false,
                (short) 0, true, true, false, capacity);

        this.ammo = Main.getCsUtility().generateWeapon(ammoType);
    }

    private AmmoPouch(){}

    @Override
    public void use() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void dropContents() {

    }

    @Override
    public void dropItem() {

    }

    @Override
    public void openContainer() {

    }

    @Override
    public void repair() {

    }


    //TOOD: Later
    @Override
    public Map<String, Object> serialize() {
        return null;
    }
}
