package net.threetag.palladium.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.threetag.palladium.entity.FlightHandler;
import net.threetag.palladium.event.PalladiumClientEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

    @Inject(at = @At("RETURN"), method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V")
    public void setupRotations(AbstractClientPlayer player, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks, CallbackInfo ci) {
        PlayerRenderer playerRenderer = (PlayerRenderer) (Object) this;
        FlightHandler.setupRotations(player, playerRenderer, poseStack, ageInTicks, rotationYaw, partialTicks);
        PalladiumClientEvents.SETUP_PLAYER_MODEL_ROTATIONS.invoker().setupRotations(playerRenderer, player, poseStack, ageInTicks, rotationYaw, partialTicks);
    }

}
