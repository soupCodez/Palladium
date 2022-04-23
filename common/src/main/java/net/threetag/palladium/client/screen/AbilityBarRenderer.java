package net.threetag.palladium.client.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.PalladiumConfig;
import net.threetag.palladium.client.PalladiumKeyMappings;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.Power;
import net.threetag.palladium.power.PowerManager;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityColor;
import net.threetag.palladium.power.ability.AbilityEntry;

import java.util.ArrayList;
import java.util.List;

public class AbilityBarRenderer implements IIngameOverlay {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Palladium.MOD_ID, "textures/gui/ability_bar.png");
    public static List<AbilityList> ABILITY_LISTS = new ArrayList<>();
    public static int SELECTED = 0;

    public AbilityBarRenderer() {
        ClientTickEvent.CLIENT_POST.register(instance -> updateCurrentLists());
    }

    public static AbilityList getSelectedList() {
        if (ABILITY_LISTS.isEmpty()) {
            return null;
        } else {
            if (SELECTED >= ABILITY_LISTS.size() || SELECTED < 0) {
                SELECTED = 0;
            }
            return ABILITY_LISTS.get(SELECTED);
        }
    }

    @Override
    public void render(Gui gui, PoseStack poseStack, float partialTicks, int width, int height) {
        if (ABILITY_LISTS.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Position position = PalladiumConfig.getAbilityBarPosition();
        AbilityList list = getSelectedList();

        if (position == Position.HIDDEN || list == null) {
            return;
        }

        if (position.top && mc.options.renderDebug) {
            return;
        }

        if (!position.top && mc.screen instanceof ChatScreen) {
            position = position.left ? Position.TOP_LEFT : Position.TOP_RIGHT;
        }

        if (mc.player != null) {
            int indicatorWidth = 52;
            int indicatorHeight = 28;

            poseStack.pushPose();
            translateIndicatorBackground(poseStack, mc.getWindow(), position, indicatorWidth, indicatorHeight);
            renderIndicator(list, mc, poseStack, position, TEXTURE, ABILITY_LISTS.size() > 1);
            poseStack.popPose();

            poseStack.pushPose();
            translateAbilitiesBackground(poseStack, mc.getWindow(), position, indicatorHeight, 24, 112);
            renderAbilitiesBackground(mc, poseStack, position, list, TEXTURE);
            renderAbilitiesOverlay(mc, poseStack, position, list, TEXTURE);
            poseStack.popPose();
        }
    }

    private static void translateIndicatorBackground(PoseStack poseStack, Window window, Position position, int width, int height) {
        if (!position.top) {
            poseStack.translate(0, window.getGuiScaledHeight() - height, 0);
        }

        if (!position.left) {
            poseStack.translate(window.getGuiScaledWidth() - width, 0, 0);
        }
    }

    private static void renderIndicator(AbilityList list, Minecraft minecraft, PoseStack poseStack, Position position, ResourceLocation texture, boolean showKey) {
        // Background
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.gui.blit(poseStack, 0, 0, position.left ? 52 : 0, position.top ? 28 : 0, 52, 28);

        // Icon
        list.power.getIcon().draw(minecraft, poseStack, showKey ? (position.left ? 30 : 6) : (position.left ? 17 : 19), position.top ? 5 : 7);

        // Button
        if (showKey) {
            FormattedText properties = minecraft.font.substrByWidth(PalladiumKeyMappings.SWITCH_ABILITY_LIST.getTranslatedKeyMessage(), 10);
            int length = minecraft.font.width(properties) + 10;
            if (properties instanceof BaseComponent)
                minecraft.font.draw(poseStack, (Component) properties, (position.left ? 15 : 37) - length / 2F + 10, position.top ? 10 : 12, 0xffffffff);

            RenderSystem.setShaderTexture(0, texture);
            minecraft.gui.blit(poseStack, (position.left ? 15 : 37) - length / 2, position.top ? 9 : 11, 78, 56, 7, 9);
        }
    }

    private static void translateAbilitiesBackground(PoseStack poseStack, Window window, Position position, int indicatorHeight, int abilitiesWidth, int abilitiesHeight) {
        if (position.top) {
            poseStack.translate(!position.left ? window.getGuiScaledWidth() - abilitiesWidth : 0, indicatorHeight - 1, 0);
        } else {
            poseStack.translate(!position.left ? window.getGuiScaledWidth() - abilitiesWidth : 0, window.getGuiScaledHeight() - indicatorHeight - abilitiesHeight + 1, 0);
        }
    }

    private static void renderAbilitiesBackground(Minecraft minecraft, PoseStack poseStack, Position position, AbilityList list, ResourceLocation texture) {
        boolean showName = minecraft.screen instanceof ChatScreen;

        for (int i = 0; i < 5; i++) {
            Lighting.setupFor3DItems();
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, texture);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            minecraft.gui.blit(poseStack, 3, i * 22 + 3, 60, 56, 18, 18);

            if (list != null) {
                AbilityEntry entry = list.getAbilities()[i];

                if (entry != null) {
                    if (entry.isEnabled()) {
                        if(entry.cooldown == 0) {
                            minecraft.gui.blit(poseStack, 3, i * 22 + 3, 42, 56, 18, 18);
                        } else {
                            int height = (int) (((float) entry.cooldown / (float) entry.maxCooldown) * 18);
                            minecraft.gui.blit(poseStack, 3, i * 22 + 3, 24, 56, 18, 18);
                            minecraft.gui.blit(poseStack, 3, i * 22 + 3 + (18 - height), 42, 74 - height, 18, height);
                        }
                    } else {
                        minecraft.gui.blit(poseStack, 3, i * 22 + 3, 24, entry.isUnlocked() ? 56 : 74, 18, 18);
                    }

                    if (!entry.isUnlocked()) {
                        minecraft.gui.blit(poseStack, 3, i * 22 + 3, 42, 74, 18, 18);
                    } else {
                        entry.getProperty(Ability.ICON).draw(minecraft, poseStack, 4, 4 + i * 22);
                    }

                    // Ability Name
                    if (showName) {
                        Tesselator tes = Tesselator.getInstance();
                        BufferBuilder bb = tes.getBuilder();
                        Component name = entry.getConfiguration().getDisplayName();
                        int width = minecraft.font.width(name);
                        renderBlackBox(bb, tes, poseStack, minecraft.screen, position.left ? 24 : -width - 10, i * 22 + 5, 10 + width, 14, 0.5F);
                        minecraft.font.draw(poseStack, name, position.left ? 29 : -width - 5, i * 22 + 8, 0xffffffff);
                    }
                } else {
                    minecraft.gui.blit(poseStack, 3, i * 22 + 3, 60, 56, 18, 18);
                }
            }
        }

        // Overlay
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.gui.blit(poseStack, 0, 0, 0, 56, 24, 112);
    }

    private static void renderAbilitiesOverlay(Minecraft minecraft, PoseStack poseStack, Position position, AbilityList list, ResourceLocation texture) {
        // Overlay
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.gui.blit(poseStack, 0, 0, 0, 56, 24, 112);

        // Colored Frames + Keys
        for (int i = 0; i < list.abilities.length; i++) {
            AbilityEntry ability = list.abilities[i];

            if (ability != null) {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, texture);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                Lighting.setupFor3DItems();
                RenderSystem.enableBlend();

                if (!ability.isUnlocked()) {
                    minecraft.gui.blit(poseStack, 3, i * 22 + 3, 42, 74, 18, 18);
                }

                AbilityColor color = ability.getProperty(Ability.COLOR);
                minecraft.gui.blit(poseStack, 0, i * 22, color.getX(), color.getY(), 24, 24);

                if (ability.getConfiguration().needsKey() && ability.isUnlocked()) {
                    Component key = PalladiumKeyMappings.ABILITY_KEYS[i].getTranslatedKeyMessage();
                    poseStack.pushPose();
                    poseStack.translate(0, 0, minecraft.getItemRenderer().blitOffset + 200);
                    GuiComponent.drawString(poseStack, minecraft.font, key, 5 + 19 - 2 - minecraft.font.width(key), 5 + i * 22 + 6 + 3, 0xffffff);
                    poseStack.popPose();
                }
            }
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void renderBlackBox(BufferBuilder bb, Tesselator tesselator, PoseStack matrixStack, GuiComponent gui, int x, int y, int width, int height, float opacity) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        bb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bb.vertex(matrixStack.last().pose(), x + width, y, gui.getBlitOffset()).color(0F, 0F, 0F, opacity).endVertex();
        bb.vertex(matrixStack.last().pose(), x, y, gui.getBlitOffset()).color(0F, 0F, 0F, opacity).endVertex();
        bb.vertex(matrixStack.last().pose(), x, y + height, gui.getBlitOffset()).color(0F, 0F, 0F, opacity).endVertex();
        bb.vertex(matrixStack.last().pose(), x + width, y + height, gui.getBlitOffset()).color(0F, 0F, 0F, opacity).endVertex();
        tesselator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void updateCurrentLists() {
        if (Minecraft.getInstance() != null && Minecraft.getInstance().player != null) {
            ABILITY_LISTS = getAbilityLists();

            if (SELECTED >= ABILITY_LISTS.size()) {
                SELECTED = ABILITY_LISTS.size() - 1;
            }
        }
    }

    public static void scroll(boolean up) {
        if (up)
            SELECTED++;
        else
            SELECTED--;

        if (SELECTED >= ABILITY_LISTS.size()) {
            SELECTED = 0;
        } else if (SELECTED < 0) {
            SELECTED = ABILITY_LISTS.size() - 1;
        }
    }

    public static List<AbilityList> getAbilityLists() {
        List<AbilityList> lists = new ArrayList<>();

        // TODO skins

        for (IPowerHolder holder : PowerManager.getPowerHandler(Minecraft.getInstance().player).getPowerHolders().values()) {
            List<AbilityList> containerList = new ArrayList<>();
            List<AbilityList> remainingLists = new ArrayList<>();
            List<AbilityEntry> remaining = new ArrayList<>();
            for (AbilityEntry abilityEntry : holder.getAbilities().values()) {
                int i = abilityEntry.getProperty(Ability.LIST_INDEX);

                if (abilityEntry.getConfiguration().needsKey() && !abilityEntry.getProperty(Ability.HIDDEN)) {
                    if (i >= 0) {
                        int listIndex = Math.floorDiv(i, 5);
                        int index = i % 5;

                        while (!(containerList.size() - 1 >= listIndex)) {
                            containerList.add(new AbilityList(holder.getPower()));
                        }

                        AbilityList abilityList = containerList.get(listIndex);
                        abilityList.addAbility(index, abilityEntry);
                    } else {
                        remaining.add(abilityEntry);
                    }
                }
            }

            for (int i = 0; i < remaining.size(); i++) {
                AbilityEntry abilityEntry = remaining.get(i);
                int listIndex = Math.floorDiv(i, 5);
                int index = i % 5;

                while (!(remainingLists.size() - 1 >= listIndex)) {
                    remainingLists.add(new AbilityList(holder.getPower()));
                }

                AbilityList abilityList = remainingLists.get(listIndex);
                abilityList.addAbility(index, abilityEntry);
            }

            for (AbilityList list : containerList) {
                if (!list.isEmpty()) {
                    lists.add(list);
                }
            }

            for (AbilityList list : remainingLists) {
                if (!list.isEmpty()) {
                    lists.add(list);
                }
            }
        }

        return lists;
    }

    public static class AbilityList {

        private final Power power;
        private final AbilityEntry[] abilities = new AbilityEntry[5];
        private ResourceLocation texture;

        public AbilityList(Power power) {
            this.power = power;
        }

        public Power getPower() {
            return power;
        }

        public AbilityList addAbility(int index, AbilityEntry ability) {
            this.abilities[index] = ability;
            return this;
        }

        public boolean addAbility(AbilityEntry ability) {
            for (int i = 0; i < this.abilities.length; i++) {
                if (this.abilities[i] == null) {
                    this.abilities[i] = ability;
                    return true;
                }
            }
            return false;
        }

        public AbilityList setTexture(ResourceLocation texture) {
            this.texture = texture;
            return this;
        }

        public boolean isEmpty() {
            for (AbilityEntry ability : this.abilities) {
                if (ability != null) {
                    return false;
                }
            }
            return true;
        }

        public AbilityEntry[] getAbilities() {
            return abilities;
        }
    }

    public enum Position {

        TOP_LEFT(true, true),
        TOP_RIGHT(false, true),
        BOTTOM_LEFT(true, false),
        BOTTOM_RIGHT(false, false),
        HIDDEN(false, false);

        private final boolean left, top;

        Position(boolean left, boolean top) {
            this.left = left;
            this.top = top;
        }
    }


}
