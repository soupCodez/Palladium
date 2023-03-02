package net.threetag.palladium.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.LivingEntity;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.Power;
import net.threetag.palladium.power.ability.AbilityEntry;
import net.threetag.palladium.util.property.FloatProperty;
import net.threetag.palladium.util.property.PalladiumProperty;

public class HealthCondition extends Condition {

    private final float minHealth, maxHealth;

    public HealthCondition(float minHealth, float maxHealth) {
        this.minHealth = minHealth;
        this.maxHealth = maxHealth;
    }

    @Override
    public boolean active(LivingEntity entity, AbilityEntry entry, Power power, IPowerHolder holder) {
        return entity.getHealth() >= this.minHealth && entity.getHealth() <= this.maxHealth;
    }

    @Override
    public ConditionSerializer getSerializer() {
        return ConditionSerializers.HEALTH.get();
    }

    public static class Serializer extends ConditionSerializer {

        public static final PalladiumProperty<Float> MIN_HEALTH = new FloatProperty("min_health").configurable("Minimum required amount of health");
        public static final PalladiumProperty<Float> MAX_HEALTH = new FloatProperty("max_health").configurable("Maximum required amount of health");

        public Serializer() {
            this.withProperty(MIN_HEALTH, 0F);
            this.withProperty(MAX_HEALTH, Float.MAX_VALUE);
        }

        @Override
        public Condition make(JsonObject json) {
            return new HealthCondition(getProperty(json, MIN_HEALTH), getProperty(json, MAX_HEALTH));
        }

        @Override
        public String getDocumentationDescription() {
            return "Checks if the entity has a certain amount of health";
        }
    }
}
