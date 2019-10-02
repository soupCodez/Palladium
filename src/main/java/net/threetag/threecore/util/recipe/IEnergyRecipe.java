package net.threetag.threecore.util.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;

public interface IEnergyRecipe<T extends IInventory> extends IRecipe<T> {

    int getRequiredEnergy();

}
