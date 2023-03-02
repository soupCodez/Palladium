package net.threetag.palladium.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IAddonItem {

    void setTooltip(List<Component> lines);

    AddonAttributeContainer getAttributeContainer();

    void setRenderLayerContainer(RenderLayerContainer container);

    RenderLayerContainer getRenderLayerContainer();

    class RenderLayerContainer {

        private final Map<String, List<ResourceLocation>> layers = new HashMap<>();

        public RenderLayerContainer addLayer(String slotKey, ResourceLocation layerId) {
            this.layers.computeIfAbsent(slotKey, s -> new ArrayList<>()).add(layerId);
            return this;
        }

        public List<ResourceLocation> get(String slotKey) {
            return this.layers.getOrDefault(slotKey, new ArrayList<>());
        }

    }

}
