package net.threetag.palladium.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.LivingEntity;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.Power;
import net.threetag.palladium.power.ability.AbilityEntry;

public class HeldCondition extends Condition {

    @Override
    public boolean active(LivingEntity entity, AbilityEntry entry, Power power, IPowerHolder holder) {
        return entry.keyPressed;
    }

    @Override
    public boolean needsKey() {
        return true;
    }

    @Override
    public void onKeyPressed(LivingEntity entity, AbilityEntry entry, Power power, IPowerHolder holder) {
        entry.keyPressed = true;
    }

    @Override
    public void onKeyReleased(LivingEntity entity, AbilityEntry entry, Power power, IPowerHolder holder) {
        entry.keyPressed = false;
    }

    @Override
    public ConditionSerializer getSerializer() {
        return ConditionSerializers.HELD.get();
    }

    public static class Serializer extends ConditionSerializer {

        @Override
        public Condition make(JsonObject json) {
            return new HeldCondition();
        }

        @Override
        public ConditionContextType getContextType() {
            return ConditionContextType.ABILITIES;
        }
    }

}