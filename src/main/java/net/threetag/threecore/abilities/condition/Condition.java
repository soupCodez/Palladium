package net.threetag.threecore.abilities.condition;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import net.threetag.threecore.abilities.Ability;
import net.threetag.threecore.abilities.client.gui.AbilitiesScreen;
import net.threetag.threecore.util.threedata.*;

import java.util.UUID;

public abstract class Condition implements INBTSerializable<CompoundNBT>, IThreeDataHolder {

    public final Ability ability;
    protected final ConditionType type;
    UUID id;
    protected ThreeDataManager dataManager = new ThreeDataManager(this);

    public static final ThreeData<ITextComponent> CUSTOM_TITLE = new TextComponentThreeData("custom_title").setSyncType(EnumSync.SELF).enableSetting("custom_title", "A custom display name for the condition.");
    public static final ThreeData<Boolean> INVERT = new BooleanThreeData("invert").enableSetting("invert", "Lets you invert the condition");
    public static final ThreeData<Boolean> ENABLING = new BooleanThreeData("enabling").setSyncType(EnumSync.SELF).enableSetting("enabling", "If this condition enables. If false it instead decides whether the ability is unlocked.");
    public static final ThreeData<Boolean> NEEDS_KEY = new BooleanThreeData("needs_key").setSyncType(EnumSync.SELF);

    public Condition(ConditionType type, Ability ability) {
        this.type = type;
        this.ability = ability;
        this.registerData();
    }

    public void registerData() {
        this.dataManager.register(CUSTOM_TITLE, new StringTextComponent("empty"));
        this.dataManager.register(INVERT, false);
        this.dataManager.register(ENABLING, false);
        this.dataManager.register(NEEDS_KEY, false);
    }

    public final ITextComponent getDisplayName() {
        ITextComponent custom = this.dataManager.get(CUSTOM_TITLE);

        if (custom instanceof StringTextComponent && ((StringTextComponent) custom).getText().equalsIgnoreCase("empty")) {
            return createTitle();
        } else {
            return custom;
        }
    }

    public ITextComponent createTitle() {
        return new TranslationTextComponent(Util.makeTranslationKey("ability.condition", this.type.getRegistryName()) + (this.dataManager.get(INVERT) ? ".not" : ""));
    }

    public final UUID getUniqueId() {
        return id;
    }

    public void readFromJson(JsonObject json) {
        this.dataManager.readFromJson(json);
    }

    public ThreeDataManager getDataManager() {
        return dataManager;
    }

    public abstract boolean test(LivingEntity entity);

    public void firstTick() {
    }

    public void lastTick() {
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("ConditionType", this.type.getRegistryName().toString());
        nbt.put("Data", this.dataManager.serializeNBT());
        nbt.put("UUID", NBTUtil.writeUniqueId(this.id));
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.dataManager.deserializeNBT(nbt.getCompound("Data"));
        this.id = NBTUtil.readUniqueId(nbt.getCompound("UUID"));
    }

    @Override
    public <T> void update(ThreeData<T> data, T value) {
        ability.update(data, value);
    }

    @Override
    public void setDirty() {
        ability.setDirty();
    }

    @OnlyIn(Dist.CLIENT)
    public Screen getScreen(AbilitiesScreen screen) {
        return null;
    }
}
