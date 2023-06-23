package net.threetag.palladium.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

@Mixin(GeoArmorRenderer.class)
public interface GeoArmorRendererInvoker {

    @Invoker(value = "fitToBiped", remap = false)
    void invokeFitToBiped();

}
