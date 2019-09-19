package net.threetag.threecore.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.threetag.threecore.ThreeCore;
import net.threetag.threecore.ThreeCoreCommonConfig;
import net.threetag.threecore.base.block.GrinderBlock;
import net.threetag.threecore.base.block.HydraulicPressBlock;
import net.threetag.threecore.base.block.VibraniumBlock;
import net.threetag.threecore.base.client.gui.GrinderScreen;
import net.threetag.threecore.base.client.gui.HydraulicPressScreen;
import net.threetag.threecore.base.inventory.GrinderContainer;
import net.threetag.threecore.base.inventory.HydraulicPressContainer;
import net.threetag.threecore.base.item.CapacitorItem;
import net.threetag.threecore.base.item.HammerItem;
import net.threetag.threecore.base.recipe.GrinderRecipe;
import net.threetag.threecore.base.recipe.PressingRecipe;
import net.threetag.threecore.base.tileentity.GrinderTileEntity;
import net.threetag.threecore.base.tileentity.HydraulicPressTileEntity;
import net.threetag.threecore.util.item.ItemGroupRegistry;

@ObjectHolder(ThreeCore.MODID)
public class ThreeCoreBase {

    public ThreeCoreBase() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent e) {
        //RecipeUtil.generateThreeCoreRecipes();

        // Ores
        ForgeRegistries.BIOMES.getValues().forEach((b) -> addOreFeature(b, COPPER_ORE.getDefaultState(), ThreeCoreCommonConfig.MATERIALS.COPPER));
        ForgeRegistries.BIOMES.getValues().forEach((b) -> addOreFeature(b, TIN_ORE.getDefaultState(), ThreeCoreCommonConfig.MATERIALS.TIN));
        ForgeRegistries.BIOMES.getValues().forEach((b) -> addOreFeature(b, LEAD_ORE.getDefaultState(), ThreeCoreCommonConfig.MATERIALS.LEAD));
        ForgeRegistries.BIOMES.getValues().forEach((b) -> addOreFeature(b, SILVER_ORE.getDefaultState(), ThreeCoreCommonConfig.MATERIALS.SILVER));
        ForgeRegistries.BIOMES.getValues().forEach((b) -> addOreFeature(b, PALLADIUM_ORE.getDefaultState(), ThreeCoreCommonConfig.MATERIALS.PALLADIUM));
        ForgeRegistries.BIOMES.getValues().forEach((b) -> addOreFeature(b, VIBRANIUM_ORE.getDefaultState(), ThreeCoreCommonConfig.MATERIALS.VIBRANIUM));
        ForgeRegistries.BIOMES.getValues().forEach((b) -> addOreFeature(b, OSMIUM_ORE.getDefaultState(), ThreeCoreCommonConfig.MATERIALS.OSMIUM));
        ForgeRegistries.BIOMES.getValues().forEach((b) -> addOreFeature(b, URANIUM_ORE.getDefaultState(), ThreeCoreCommonConfig.MATERIALS.URANIUM));
        ForgeRegistries.BIOMES.getValues().forEach((b) -> addOreFeature(b, TITANIUM_ORE.getDefaultState(), ThreeCoreCommonConfig.MATERIALS.TITANIUM));
        ForgeRegistries.BIOMES.getValues().forEach((b) -> addOreFeature(b, IRIDIUM_ORE.getDefaultState(), ThreeCoreCommonConfig.MATERIALS.IRIDIUM));
        ForgeRegistries.BIOMES.getValues().forEach((b) -> addOreFeature(b, URU_ORE.getDefaultState(), ThreeCoreCommonConfig.MATERIALS.URU));

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            // Screens
            ScreenManager.registerFactory(GRINDER_CONTAINER, GrinderScreen::new);
            ScreenManager.registerFactory(HYDRAULIC_PRESS_CONTAINER, HydraulicPressScreen::new);

            // TESR
            try {
                ClientRegistry.bindTileEntitySpecialRenderer(HydraulicPressTileEntity.class, (TileEntityRenderer<HydraulicPressTileEntity>) Class.forName("net.threetag.threecore.base.client.renderer.tileentity.HydraulicPressTileEntityRenderer").newInstance());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void addOreFeature(Biome biome, BlockState ore, ThreeCoreCommonConfig.Materials.OreConfig config) {
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, ore, config.size.get()), Placement.COUNT_RANGE, new CountRangeConfig(config.count.get(), config.minHeight.get(), 0, config.maxHeight.get() - config.minHeight.get())));
    }

    // Machines
    @ObjectHolder("grinder")
    public static final Block GRINDER = null;
    @ObjectHolder("grinder")
    public static final TileEntityType<GrinderTileEntity> GRINDER_TILE_ENTITY = null;
    @ObjectHolder("grinder")
    public static final ContainerType<GrinderContainer> GRINDER_CONTAINER = null;
    @ObjectHolder("grinding")
    public static final IRecipeSerializer<GrinderRecipe> GRINDER_RECIPE_SERIALIZER = null;

    @ObjectHolder("hydraulic_press")
    public static final Block HYDRAULIC_PRESS = null;
    @ObjectHolder("hydraulic_press")
    public static final TileEntityType<HydraulicPressTileEntity> HYDRAULIC_PRESS_TILE_ENTITY = null;
    @ObjectHolder("hydraulic_press")
    public static final ContainerType<HydraulicPressContainer> HYDRAULIC_PRESS_CONTAINER = null;
    @ObjectHolder("pressing")
    public static final IRecipeSerializer<PressingRecipe> PRESSING_RECIPE_SERIALIZER = null;

    // Misc Items
    @ObjectHolder("hammer")
    public static final Item HAMMER = null;
    @ObjectHolder("plate_cast")
    public static final Item PLATE_CAST = null;
    @ObjectHolder("capacitor")
    public static final Item CAPACITOR = null;
    @ObjectHolder("advanced_capacitor")
    public static final Item ADVANCED_CAPACITOR = null;
    @ObjectHolder("circuit")
    public static final Item CIRCUIT = null;
    @ObjectHolder("advanced_circuit")
    public static final Item ADVANCED_CIRCUIT = null;

    // Storage Blocks
    // TODO Harvest levels
    @ObjectHolder("copper_block")
    public static final Block COPPER_BLOCK = null;
    @ObjectHolder("tin_block")
    public static final Block TIN_BLOCK = null;
    @ObjectHolder("lead_block")
    public static final Block LEAD_BLOCK = null;
    @ObjectHolder("silver_block")
    public static final Block SILVER_BLOCK = null;
    @ObjectHolder("palladium_block")
    public static final Block PALLADIUM_BLOCK = null;
    @ObjectHolder("vibranium_block")
    public static final Block VIBRANIUM_BLOCK = null;
    @ObjectHolder("osmium_block")
    public static final Block OSMIUM_BLOCK = null;
    @ObjectHolder("uranium_block")
    public static final Block URANIUM_BLOCK = null;
    @ObjectHolder("titanium_block")
    public static final Block TITANIUM_BLOCK = null;
    @ObjectHolder("iridium_block")
    public static final Block IRIDIUM_BLOCK = null;
    @ObjectHolder("uru_block")
    public static final Block URU_BLOCK = null;
    @ObjectHolder("bronze_block")
    public static final Block BRONZE_BLOCK = null;
    @ObjectHolder("intertium_block")
    public static final Block INTERTIUM_BLOCK = null;
    @ObjectHolder("steel_block")
    public static final Block STEEL_BLOCK = null;
    @ObjectHolder("gold_titanium_alloy_block")
    public static final Block GOLD_TITANIUM_ALLOY_BLOCK = null;
    @ObjectHolder("adamantium_block")
    public static final Block ADAMANTIUM_BLOCK = null;

    // Ores
    @ObjectHolder("copper_ore")
    public static final Block COPPER_ORE = null;
    @ObjectHolder("tin_ore")
    public static final Block TIN_ORE = null;
    @ObjectHolder("lead_ore")
    public static final Block LEAD_ORE = null;
    @ObjectHolder("silver_ore")
    public static final Block SILVER_ORE = null;
    @ObjectHolder("palladium_ore")
    public static final Block PALLADIUM_ORE = null;
    @ObjectHolder("vibranium_ore")
    public static final Block VIBRANIUM_ORE = null;
    @ObjectHolder("osmium_ore")
    public static final Block OSMIUM_ORE = null;
    @ObjectHolder("uranium_ore")
    public static final Block URANIUM_ORE = null;
    @ObjectHolder("titanium_ore")
    public static final Block TITANIUM_ORE = null;
    @ObjectHolder("iridium_ore")
    public static final Block IRIDIUM_ORE = null;
    @ObjectHolder("uru_ore")
    public static final Block URU_ORE = null;

    // Ingots
    @ObjectHolder("copper_ingot")
    public static final Block COPPER_INGOT = null;
    @ObjectHolder("tin_ingot")
    public static final Block TIN_INGOT = null;
    @ObjectHolder("lead_ingot")
    public static final Block LEAD_INGOT = null;
    @ObjectHolder("silver_ingot")
    public static final Block SILVER_INGOT = null;
    @ObjectHolder("palladium_ingot")
    public static final Block PALLADIUM_INGOT = null;
    @ObjectHolder("vibranium_ingot")
    public static final Block VIBRANIUM_INGOT = null;
    @ObjectHolder("osmium_ingot")
    public static final Block OSMIUM_INGOT = null;
    @ObjectHolder("uranium_ingot")
    public static final Block URANIUM_INGOT = null;
    @ObjectHolder("titanium_ingot")
    public static final Block TITANIUM_INGOT = null;
    @ObjectHolder("iridium_ingot")
    public static final Block IRIDIUM_INGOT = null;
    @ObjectHolder("uru_ingot")
    public static final Block URU_INGOT = null;
    @ObjectHolder("bronze_ingot")
    public static final Block BRONZE_INGOT = null;
    @ObjectHolder("intertium_ingot")
    public static final Block INTERTIUM_INGOT = null;
    @ObjectHolder("steel_ingot")
    public static final Block STEEL_INGOT = null;
    @ObjectHolder("gold_titanium_alloy_ingot")
    public static final Block GOLD_TITANIUM_ALLOY_INGOT = null;
    @ObjectHolder("adamantium_ingot")
    public static final Block ADAMANTIUM_INGOT = null;

    // Nuggets
    @ObjectHolder("copper_nugget")
    public static final Block COPPER_NUGGET = null;
    @ObjectHolder("tin_nugget")
    public static final Block TIN_NUGGET = null;
    @ObjectHolder("lead_nugget")
    public static final Block LEAD_NUGGET = null;
    @ObjectHolder("silver_nugget")
    public static final Block SILVER_NUGGET = null;
    @ObjectHolder("palladium_nugget")
    public static final Block PALLADIUM_NUGGET = null;
    @ObjectHolder("vibranium_nugget")
    public static final Block VIBRANIUM_NUGGET = null;
    @ObjectHolder("osmium_nugget")
    public static final Block OSMIUM_NUGGET = null;
    @ObjectHolder("uranium_nugget")
    public static final Block URANIUM_NUGGET = null;
    @ObjectHolder("titanium_nugget")
    public static final Block TITANIUM_NUGGET = null;
    @ObjectHolder("iridium_nugget")
    public static final Block IRIDIUM_NUGGET = null;
    @ObjectHolder("uru_nugget")
    public static final Block URU_NUGGET = null;
    @ObjectHolder("bronze_nugget")
    public static final Block BRONZE_NUGGET = null;
    @ObjectHolder("intertium_nugget")
    public static final Block INTERTIUM_NUGGET = null;
    @ObjectHolder("steel_nugget")
    public static final Block STEEL_NUGGET = null;
    @ObjectHolder("gold_titanium_alloy_nugget")
    public static final Block GOLD_TITANIUM_ALLOY_NUGGET = null;
    @ObjectHolder("adamantium_nugget")
    public static final Block ADAMANTIUM_NUGGET = null;

    // Dusts
    @ObjectHolder("iron_dust")
    public static final Item IRON_DUST = null;
    @ObjectHolder("gold_dust")
    public static final Item GOLD_DUST = null;
    @ObjectHolder("copper_dust")
    public static final Block COPPER_DUST = null;
    @ObjectHolder("tin_dust")
    public static final Block TIN_DUST = null;
    @ObjectHolder("lead_dust")
    public static final Block LEAD_DUST = null;
    @ObjectHolder("silver_dust")
    public static final Block SILVER_DUST = null;
    @ObjectHolder("palladium_dust")
    public static final Block PALLADIUM_DUST = null;
    @ObjectHolder("vibranium_dust")
    public static final Block VIBRANIUM_DUST = null;
    @ObjectHolder("osmium_dust")
    public static final Block OSMIUM_DUST = null;
    @ObjectHolder("uranium_dust")
    public static final Block URANIUM_DUST = null;
    @ObjectHolder("titanium_dust")
    public static final Block TITANIUM_DUST = null;
    @ObjectHolder("iridium_dust")
    public static final Block IRIDIUM_DUST = null;
    @ObjectHolder("uru_dust")
    public static final Block URU_DUST = null;
    @ObjectHolder("bronze_dust")
    public static final Block BRONZE_DUST = null;
    @ObjectHolder("intertium_dust")
    public static final Block INTERTIUM_DUST = null;
    @ObjectHolder("steel_dust")
    public static final Block STEEL_DUST = null;
    @ObjectHolder("gold_titanium_alloy_dust")
    public static final Block GOLD_TITANIUM_ALLOY_DUST = null;
    @ObjectHolder("adamantium_dust")
    public static final Block ADAMANTIUM_DUST = null;

    // Plates
    @ObjectHolder("iron_plate")
    public static final Item IRON_PLATE = null;
    @ObjectHolder("gold_plate")
    public static final Item GOLD_PLATE = null;
    @ObjectHolder("copper_plate")
    public static final Block COPPER_PLATE = null;
    @ObjectHolder("tin_plate")
    public static final Block TIN_PLATE = null;
    @ObjectHolder("lead_plate")
    public static final Block LEAD_PLATE = null;
    @ObjectHolder("silver_plate")
    public static final Block SILVER_PLATE = null;
    @ObjectHolder("palladium_plate")
    public static final Block PALLADIUM_PLATE = null;
    @ObjectHolder("vibranium_plate")
    public static final Block VIBRANIUM_PLATE = null;
    @ObjectHolder("osmium_plate")
    public static final Block OSMIUM_PLATE = null;
    @ObjectHolder("uranium_plate")
    public static final Block URANIUM_PLATE = null;
    @ObjectHolder("titanium_plate")
    public static final Block TITANIUM_PLATE = null;
    @ObjectHolder("iridium_plate")
    public static final Block IRIDIUM_PLATE = null;
    @ObjectHolder("uru_plate")
    public static final Block URU_PLATE = null;
    @ObjectHolder("bronze_plate")
    public static final Block BRONZE_PLATE = null;
    @ObjectHolder("intertium_plate")
    public static final Block INTERTIUM_PLATE = null;
    @ObjectHolder("steel_plate")
    public static final Block STEEL_PLATE = null;
    @ObjectHolder("gold_titanium_alloy_plate")
    public static final Block GOLD_TITANIUM_ALLOY_PLATE = null;
    @ObjectHolder("adamantium_plate")
    public static final Block ADAMANTIUM_PLATE = null;

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> e) {
        IForgeRegistry<Block> registry = e.getRegistry();

        registry.register(new GrinderBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(5.0F, 6.0F)).setRegistryName(ThreeCore.MODID, "grinder"));
        registry.register(new HydraulicPressBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(5.0F, 6.0F)).setRegistryName(ThreeCore.MODID, "hydraulic_press"));

        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(1).hardnessAndResistance(5.0F, 6.0F)).setRegistryName(ThreeCore.MODID, "copper_block"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(1).hardnessAndResistance(5.0F, 6.0F)).setRegistryName(ThreeCore.MODID, "tin_block"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(2).hardnessAndResistance(4.0F, 12.0F)).setRegistryName(ThreeCore.MODID, "lead_block"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(2).hardnessAndResistance(5.0F, 6.0F)).setRegistryName(ThreeCore.MODID, "silver_block"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(2).hardnessAndResistance(5.0F, 6.0F)).setRegistryName(ThreeCore.MODID, "palladium_block"));
        registry.register(new VibraniumBlock(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(3).hardnessAndResistance(15.0F, 18.0F)).setRegistryName(ThreeCore.MODID, "vibranium_block"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(2).hardnessAndResistance(5.0F, 6.0F)).setRegistryName(ThreeCore.MODID, "osmium_block"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(2).hardnessAndResistance(5.0F, 6.0F)).setRegistryName(ThreeCore.MODID, "uranium_block"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(3).hardnessAndResistance(10.0F, 12.0F)).setRegistryName(ThreeCore.MODID, "titanium_block"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(3).hardnessAndResistance(10.0F, 15.0F)).setRegistryName(ThreeCore.MODID, "iridium_block"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(3).hardnessAndResistance(10.0F, 15.0F)).setRegistryName(ThreeCore.MODID, "uru_block"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(2).hardnessAndResistance(5.0F, 6.0F)).setRegistryName(ThreeCore.MODID, "bronze_block"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(2).hardnessAndResistance(5.0F, 6.0F)).setRegistryName(ThreeCore.MODID, "intertium_block"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(2).hardnessAndResistance(5.0F, 6.0F)).setRegistryName(ThreeCore.MODID, "steel_block"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(3).hardnessAndResistance(7.0F, 8.0F)).setRegistryName(ThreeCore.MODID, "gold_titanium_alloy_block"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(3).hardnessAndResistance(15F, 18.0F)).setRegistryName(ThreeCore.MODID, "adamantium_block"));

        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(1).hardnessAndResistance(3.0F, 5.0F)).setRegistryName(ThreeCore.MODID, "copper_ore"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(1).hardnessAndResistance(3.0F, 5.0F)).setRegistryName(ThreeCore.MODID, "tin_ore"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(2).hardnessAndResistance(3.0F, 5.0F)).setRegistryName(ThreeCore.MODID, "lead_ore"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(2).hardnessAndResistance(3.0F, 5.0F)).setRegistryName(ThreeCore.MODID, "silver_ore"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(2).hardnessAndResistance(3.0F, 5.0F)).setRegistryName(ThreeCore.MODID, "palladium_ore"));
        registry.register(new VibraniumBlock(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(3).hardnessAndResistance(3.0F, 5.0F).lightValue(4)).setRegistryName(ThreeCore.MODID, "vibranium_ore"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(2).hardnessAndResistance(3.0F, 5.0F)).setRegistryName(ThreeCore.MODID, "osmium_ore"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(2).hardnessAndResistance(3.0F, 5.0F)).setRegistryName(ThreeCore.MODID, "uranium_ore"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(3).hardnessAndResistance(3.0F, 5.0F)).setRegistryName(ThreeCore.MODID, "titanium_ore"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(3).hardnessAndResistance(3.0F, 5.0F)).setRegistryName(ThreeCore.MODID, "iridium_ore"));
        registry.register(new Block(Block.Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE).harvestLevel(3).hardnessAndResistance(3.0F, 5.0F)).setRegistryName(ThreeCore.MODID, "uru_ore"));
    }

    @SubscribeEvent
    public void registerTileEntityTypes(RegistryEvent.Register<TileEntityType<?>> e) {
        e.getRegistry().register(TileEntityType.Builder.create(GrinderTileEntity::new, GRINDER).build(null).setRegistryName(ThreeCore.MODID, "grinder"));
        e.getRegistry().register(TileEntityType.Builder.create(HydraulicPressTileEntity::new, HYDRAULIC_PRESS).build(null).setRegistryName(ThreeCore.MODID, "hydraulic_press"));
    }

    @SubscribeEvent
    public void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> e) {
        e.getRegistry().register(new ContainerType<>(GrinderContainer::new).setRegistryName(ThreeCore.MODID, "grinder"));
        e.getRegistry().register(new ContainerType<>(HydraulicPressContainer::new).setRegistryName(ThreeCore.MODID, "hydraulic_press"));
    }

    @SubscribeEvent
    public void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> e) {
        e.getRegistry().register(new GrinderRecipe.Serializer().setRegistryName(ThreeCore.MODID, "grinding"));
        e.getRegistry().register(new PressingRecipe.Serializer().setRegistryName(ThreeCore.MODID, "pressing"));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> e) {
        IForgeRegistry<Item> registry = e.getRegistry();

        registry.register(makeItem(GRINDER, ItemGroupRegistry.getItemGroup(ItemGroupRegistry.TECHNOLOGY)));
        registry.register(makeItem(HYDRAULIC_PRESS, ItemGroupRegistry.getItemGroup(ItemGroupRegistry.TECHNOLOGY)));
        registry.register(new HammerItem(4.5F, -2.75F, ItemTier.IRON, new Item.Properties().group(ItemGroup.TOOLS).maxStackSize(1).maxDamage(16)).setRegistryName(ThreeCore.MODID, "hammer"));
        registry.register(new Item(new Item.Properties().group(ItemGroupRegistry.getItemGroup(ItemGroupRegistry.TECHNOLOGY))).setRegistryName(ThreeCore.MODID, "plate_cast"));
        registry.register(new CapacitorItem(new Item.Properties().group(ItemGroupRegistry.getItemGroup(ItemGroupRegistry.TECHNOLOGY)).maxStackSize(1), 40000, 100).setRegistryName(ThreeCore.MODID, "capacitor"));
        registry.register(new CapacitorItem(new Item.Properties().group(ItemGroupRegistry.getItemGroup(ItemGroupRegistry.TECHNOLOGY)).maxStackSize(1), 100000, 200).setRegistryName(ThreeCore.MODID, "advanced_capacitor"));
        registry.register(new Item(new Item.Properties().group(ItemGroupRegistry.getItemGroup(ItemGroupRegistry.TECHNOLOGY))).setRegistryName(ThreeCore.MODID, "circuit"));
        registry.register(new Item(new Item.Properties().group(ItemGroupRegistry.getItemGroup(ItemGroupRegistry.TECHNOLOGY))).setRegistryName(ThreeCore.MODID, "advanced_circuit"));

        registry.register(makeItem(COPPER_BLOCK));
        registry.register(makeItem(TIN_BLOCK));
        registry.register(makeItem(LEAD_BLOCK));
        registry.register(makeItem(SILVER_BLOCK));
        registry.register(makeItem(PALLADIUM_BLOCK));
        registry.register(makeItem(VIBRANIUM_BLOCK, Rarity.RARE));
        registry.register(makeItem(OSMIUM_BLOCK));
        registry.register(makeItem(URANIUM_BLOCK));
        registry.register(makeItem(TITANIUM_BLOCK));
        registry.register(makeItem(IRIDIUM_BLOCK, Rarity.UNCOMMON));
        registry.register(makeItem(URU_BLOCK, Rarity.EPIC));
        registry.register(makeItem(BRONZE_BLOCK));
        registry.register(makeItem(INTERTIUM_BLOCK));
        registry.register(makeItem(STEEL_BLOCK));
        registry.register(makeItem(GOLD_TITANIUM_ALLOY_BLOCK));
        registry.register(makeItem(ADAMANTIUM_BLOCK, Rarity.RARE));

        registry.register(makeItem(COPPER_ORE));
        registry.register(makeItem(TIN_ORE));
        registry.register(makeItem(LEAD_ORE));
        registry.register(makeItem(SILVER_ORE));
        registry.register(makeItem(PALLADIUM_ORE));
        registry.register(makeItem(VIBRANIUM_ORE, Rarity.RARE));
        registry.register(makeItem(OSMIUM_ORE));
        registry.register(makeItem(URANIUM_ORE));
        registry.register(makeItem(TITANIUM_ORE));
        registry.register(makeItem(IRIDIUM_ORE, Rarity.UNCOMMON));
        registry.register(makeItem(URU_ORE, Rarity.EPIC));

        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "copper_ingot"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "tin_ingot"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "lead_ingot"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "silver_ingot"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "palladium_ingot"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS).rarity(Rarity.RARE)).setRegistryName(ThreeCore.MODID, "vibranium_ingot"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "osmium_ingot"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "uranium_ingot"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "titanium_ingot"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS).rarity(Rarity.UNCOMMON)).setRegistryName(ThreeCore.MODID, "iridium_ingot"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS).rarity(Rarity.EPIC)).setRegistryName(ThreeCore.MODID, "uru_ingot"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "bronze_ingot"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "intertium_ingot"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "steel_ingot"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "gold_titanium_alloy_ingot"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS).rarity(Rarity.RARE)).setRegistryName(ThreeCore.MODID, "adamantium_ingot"));

        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "copper_nugget"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "tin_nugget"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "lead_nugget"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "silver_nugget"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "palladium_nugget"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS).rarity(Rarity.RARE)).setRegistryName(ThreeCore.MODID, "vibranium_nugget"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "osmium_nugget"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "uranium_nugget"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "titanium_nugget"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS).rarity(Rarity.UNCOMMON)).setRegistryName(ThreeCore.MODID, "iridium_nugget"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS).rarity(Rarity.EPIC)).setRegistryName(ThreeCore.MODID, "uru_nugget"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "bronze_nugget"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "intertium_nugget"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "steel_nugget"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "gold_titanium_alloy_nugget"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS).rarity(Rarity.RARE)).setRegistryName(ThreeCore.MODID, "adamantium_nugget"));

        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "iron_dust"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "gold_dust"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "copper_dust"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "tin_dust"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "lead_dust"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "silver_dust"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "palladium_dust"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS).rarity(Rarity.RARE)).setRegistryName(ThreeCore.MODID, "vibranium_dust"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "osmium_dust"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "uranium_dust"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "titanium_dust"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS).rarity(Rarity.UNCOMMON)).setRegistryName(ThreeCore.MODID, "iridium_dust"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS).rarity(Rarity.EPIC)).setRegistryName(ThreeCore.MODID, "uru_dust"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "bronze_dust"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "intertium_dust"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "steel_dust"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "gold_titanium_alloy_dust"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS).rarity(Rarity.RARE)).setRegistryName(ThreeCore.MODID, "adamantium_dust"));

        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "iron_plate"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "gold_plate"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "copper_plate"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "tin_plate"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "lead_plate"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "silver_plate"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "palladium_plate"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS).rarity(Rarity.RARE)).setRegistryName(ThreeCore.MODID, "vibranium_plate"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "osmium_plate"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "uranium_plate"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "titanium_plate"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS).rarity(Rarity.UNCOMMON)).setRegistryName(ThreeCore.MODID, "iridium_plate"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS).rarity(Rarity.EPIC)).setRegistryName(ThreeCore.MODID, "uru_plate"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "bronze_plate"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "intertium_plate"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "steel_plate"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(ThreeCore.MODID, "gold_titanium_alloy_plate"));
        registry.register(new Item(new Item.Properties().group(ItemGroup.MATERIALS).rarity(Rarity.RARE)).setRegistryName(ThreeCore.MODID, "adamantium_plate"));
    }

    public static Item makeItem(Block block) {
        return new BlockItem(block, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)).setRegistryName(block.getRegistryName());
    }

    public static Item makeItem(Block block, Rarity rarity) {
        return new BlockItem(block, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS).rarity(rarity)).setRegistryName(block.getRegistryName());
    }

    public static Item makeItem(Block block, ItemGroup itemGroup) {
        return new BlockItem(block, new Item.Properties().group(itemGroup)).setRegistryName(block.getRegistryName());
    }

}