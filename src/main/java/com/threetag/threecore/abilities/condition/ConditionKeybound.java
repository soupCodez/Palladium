package com.threetag.threecore.abilities.condition;

import com.threetag.threecore.abilities.Ability;
import com.threetag.threecore.abilities.data.EnumSync;
import com.threetag.threecore.abilities.data.ThreeData;
import com.threetag.threecore.abilities.data.ThreeDataBoolean;
import net.minecraft.entity.EntityLivingBase;

/**
 * Created by Nictogen on 2019-06-08.
 */
public abstract class ConditionKeybound extends Condition
{
	public static final ThreeData<Boolean> ENABLED = new ThreeDataBoolean("enabled").setSyncType(EnumSync.SELF);

	public ConditionKeybound(ConditionType type, Ability ability)
	{
		super(type, ability);
	}

	@Override public boolean test(EntityLivingBase entity)
	{
		return this.dataManager.get(ENABLED);
	}

	@Override public void registerData()
	{
		super.registerData();
		this.dataManager.register(ENABLED, false);
		this.dataManager.set(ENABLING, true);
		this.dataManager.set(NEEDS_KEY, true);
	}

	abstract void onKeyPressed();
	abstract void onKeyReleased();

	@Override public void lastTick()
	{
		if(this.dataManager.get(ENABLED))
			this.dataManager.set(ENABLED, false);
	}
}
