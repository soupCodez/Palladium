package net.threetag.palladium.compat.geckolib.renderlayer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class GeckoRenderLayerModel extends HumanoidModel<AbstractClientPlayer> implements GeoRenderer<GeckoLayerState> {

    protected final GeoModel<GeckoLayerState> modelProvider;
    protected HumanoidModel<?> baseModel;
    protected float scaleWidth = 1;
    protected float scaleHeight = 1;

    protected Entity currentEntity = null;
    protected GeckoLayerState currentState = null;

    protected Matrix4f entityRenderTranslations = new Matrix4f();

    public String headBone = "armorHead";
    public String bodyBone = "armorBody";
    public String rightArmBone = "armorRightArm";
    public String leftArmBone = "armorLeftArm";
    public String rightLegBone = "armorRightLeg";
    public String leftLegBone = "armorLeftLeg";

    protected BakedGeoModel lastModel = null;
    protected GeoBone head = null;
    protected GeoBone body = null;
    protected GeoBone rightArm = null;
    protected GeoBone leftArm = null;
    protected GeoBone rightLeg = null;
    protected GeoBone leftLeg = null;

    public GeckoRenderLayerModel(GeckoRenderLayer renderLayer) {
        super(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        this.modelProvider = new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(GeckoLayerState animatable) {
                return renderLayer.cachedModel;
            }

            @Override
            public ResourceLocation getTextureResource(GeckoLayerState animatable) {
                return renderLayer.cachedTexture;
            }

            @Override
            public ResourceLocation getAnimationResource(GeckoLayerState animatable) {
                return renderLayer.animationLocation;
            }
        };
    }

    public void setCurrentRenderingFields(GeckoLayerState state, Entity entity, HumanoidModel<?> baseModel) {
        this.currentState = state;
        this.baseModel = baseModel;
        this.currentEntity = entity;
    }

    @Override
    public void preRender(PoseStack poseStack, GeckoLayerState animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.entityRenderTranslations = new Matrix4f(poseStack.last().pose());

        applyBaseModel(this.baseModel);
        grabRelevantBones(getGeoModel().getBakedModel(getGeoModel().getModelResource(this.currentState)));
        applyBaseTransformations(this.baseModel);
        scaleModelForRender(this.scaleWidth, this.scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource bufferSource = mc.renderBuffers().bufferSource();

        if (mc.levelRenderer.shouldShowEntityOutlines() && mc.shouldEntityAppearGlowing(this.currentEntity))
            bufferSource = mc.renderBuffers().outlineBufferSource();

        float partialTick = mc.getFrameTime();
        RenderType renderType = getRenderType(this.currentState, getTextureLocation(this.currentState), bufferSource, partialTick);
        buffer = ItemRenderer.getArmorFoilBuffer(bufferSource, renderType, false, false);

        defaultRender(poseStack, this.currentState, bufferSource, null, buffer,
                0, partialTick, packedLight);
    }

    protected void grabRelevantBones(BakedGeoModel bakedModel) {
        if (this.lastModel == bakedModel)
            return;

        this.lastModel = bakedModel;
        this.head = getHeadBone();
        this.body = getBodyBone();
        this.rightArm = getRightArmBone();
        this.leftArm = getLeftArmBone();
        this.rightLeg = getRightLegBone();
        this.leftLeg = getLeftLegBone();
    }

    protected void applyBaseModel(HumanoidModel<?> baseModel) {
        this.young = baseModel.young;
        this.crouching = baseModel.crouching;
        this.riding = baseModel.riding;
        this.rightArmPose = baseModel.rightArmPose;
        this.leftArmPose = baseModel.leftArmPose;
    }

    @Nullable
    public GeoBone getHeadBone() {
        return this.modelProvider.getBone(this.headBone).orElse(null);
    }

    @Nullable
    public GeoBone getBodyBone() {
        return this.modelProvider.getBone(this.bodyBone).orElse(null);
    }

    @Nullable
    public GeoBone getRightArmBone() {
        return this.modelProvider.getBone(this.rightArmBone).orElse(null);
    }

    @Nullable
    public GeoBone getLeftArmBone() {
        return this.modelProvider.getBone(this.leftArmBone).orElse(null);
    }

    @Nullable
    public GeoBone getRightLegBone() {
        return this.modelProvider.getBone(this.rightLegBone).orElse(null);
    }

    @Nullable
    public GeoBone getLeftLegBone() {
        return this.modelProvider.getBone(this.leftLegBone).orElse(null);
    }

//
//    public void renderArm(DataContext context, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay,
//                          float partialTick, boolean rightArm) {
//        GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelResource(this.state));
//
//        model.getBone(rightArm ? this.rightArmBone : this.leftArmBone).ifPresent(bone -> {
//            AnimationEvent<GeckoLayerState> animationEvent = new AnimationEvent<>(this.state, 0, 0,
//                    partialTick, false,
//                    List.of());
//
//            poseStack.pushPose();
//            poseStack.translate(0, 24 / 16F, 0);
//            poseStack.scale(-1, -1, 1);
//
//            this.modelProvider.setCustomAnimations(this.state, getInstanceId(this.state), animationEvent);
//            setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
//            fitToBiped();
//            RenderSystem.setShaderTexture(0, getTextureLocation(this.state));
//
//            var buffer1 = this.renderLayer.renderType.createVertexConsumer(bufferSource, getTextureLocation(this.state), context.getItem().hasFoil());
//            Color renderColor = getRenderColor(this.state, 0, poseStack, null, buffer1, packedLight);
//
//            setCurrentRTB(bufferSource);
//            renderEarly(this.state, poseStack, partialTick, bufferSource, buffer1, packedLight,
//                    packedOverlay, renderColor.getRed() / 255f, renderColor.getGreen() / 255f,
//                    renderColor.getBlue() / 255f, renderColor.getAlpha() / 255f);
//
//            renderLate(this.state, poseStack, partialTick, bufferSource, buffer1, packedLight,
//                    packedOverlay, renderColor.getRed() / 255f, renderColor.getGreen() / 255f,
//                    renderColor.getBlue() / 255f, renderColor.getAlpha() / 255f);
//            this.renderRecursively(bone,
//                    poseStack, buffer1, packedLight, packedOverlay, renderColor.getRed() / 255f, renderColor.getGreen() / 255f,
//                    renderColor.getBlue() / 255f, renderColor.getAlpha() / 255f);
//            setCurrentModelRenderCycle(EModelRenderCycle.REPEATED);
//
//            poseStack.popPose();
//        });
//    }

    protected void applyBaseTransformations(HumanoidModel<?> baseModel) {
        if (this.head != null) {
            ModelPart headPart = baseModel.head;

            RenderUtils.matchModelPartRot(headPart, this.head);
            copyScaleAndVisibility(headPart, this.head);
            this.head.updatePosition(headPart.x, -headPart.y, headPart.z);
        };

        if (this.body != null) {
            ModelPart bodyPart = baseModel.body;

            RenderUtils.matchModelPartRot(bodyPart, this.body);
            copyScaleAndVisibility(bodyPart, this.body);
            this.body.updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);
        }

        if (this.rightArm != null) {
            ModelPart rightArmPart = baseModel.rightArm;

            RenderUtils.matchModelPartRot(rightArmPart, this.rightArm);
            copyScaleAndVisibility(rightArmPart, this.rightArm);
            this.rightArm.updatePosition(rightArmPart.x + 5, 2 - rightArmPart.y, rightArmPart.z);
        }

        if (this.leftArm != null) {
            ModelPart leftArmPart = baseModel.leftArm;

            RenderUtils.matchModelPartRot(leftArmPart, this.leftArm);
            copyScaleAndVisibility(leftArmPart, this.leftArm);
            this.leftArm.updatePosition(leftArmPart.x - 5f, 2f - leftArmPart.y, leftArmPart.z);
        }

        if (this.rightLeg != null) {
            ModelPart rightLegPart = baseModel.rightLeg;

            RenderUtils.matchModelPartRot(rightLegPart, this.rightLeg);
            copyScaleAndVisibility(rightLegPart, this.rightLeg);
            this.rightLeg.updatePosition(rightLegPart.x + 2, 12 - rightLegPart.y, rightLegPart.z);
        }

        if (this.leftLeg != null) {
            ModelPart leftLegPart = baseModel.leftLeg;

            RenderUtils.matchModelPartRot(leftLegPart, this.leftLeg);
            copyScaleAndVisibility(leftLegPart, this.leftLeg);
            this.leftLeg.updatePosition(leftLegPart.x - 2, 12 - leftLegPart.y, leftLegPart.z);
        }
    }

    public static void copyScaleAndVisibility(ModelPart from, CoreGeoBone to) {
        to.setScaleX(from.xScale);
        to.setScaleY(from.yScale);
        to.setScaleZ(from.zScale);
        to.setHidden(!from.visible);
    }

    @Override
    public GeoModel<GeckoLayerState> getGeoModel() {
        return this.modelProvider;
    }

    @Override
    public GeckoLayerState getAnimatable() {
        return this.currentState;
    }

    @Override
    public void fireCompileRenderLayersEvent() {

    }

    @Override
    public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return true;
    }

    @Override
    public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {

    }

    @Override
    public void updateAnimatedTextureFrame(GeckoLayerState animatable) {
        if (this.currentEntity != null)
            AnimatableTexture.setAndUpdate(getTextureLocation(animatable), this.currentEntity.getId() + this.currentEntity.tickCount);
    }
}