package com.threetag.threecore.util.render;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.threetag.threecore.ThreeCore;
import com.threetag.threecore.abilities.data.ExperienceThreeData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class ExperienceIcon implements IIcon {

    private static final TexturedIcon ICON = new TexturedIcon(TexturedIcon.ICONS_TEXTURE, 128, 16, 16, 16);
    public ExperienceThreeData.Experience experience;

    public ExperienceIcon(ExperienceThreeData.Experience experience) {
        this.experience = experience;
    }

    @Override
    public void draw(Minecraft mc, int x, int y) {
        ICON.draw(mc, x, y);
        String text = this.experience.getValue() + (this.experience.isLevels() ? "L" : "");
        mc.fontRenderer.drawString(text, (float) (x + 9), (float) y + 8, 0);
        mc.fontRenderer.drawString(text, (float) (x + 7), (float) y + 8, 0);
        mc.fontRenderer.drawString(text, (float) x + 8, (float) (y + 9), 0);
        mc.fontRenderer.drawString(text, (float) x + 8, (float) (y + 7), 0);
        mc.fontRenderer.drawString(text, (float) x + 8, (float) y + 8, 8453920);
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public IIconSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements IIconSerializer<ExperienceIcon> {

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(ThreeCore.MODID, "experience");

        @Override
        public ExperienceIcon read(JsonObject json) {
            JsonPrimitive primitive = json.get("experience").getAsJsonPrimitive();
            return new ExperienceIcon(new ExperienceThreeData.Experience(primitive));
        }

        @Override
        public ExperienceIcon read(CompoundNBT nbt) {
            return new ExperienceIcon(new ExperienceThreeData.Experience(nbt));
        }

        @Override
        public CompoundNBT serialize(ExperienceIcon icon) {
            return icon.experience.serializeNBT();
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }
    }
}
