package ace.actually.villagertourism;

import ace.actually.villagertourism.blocks.TourismBlock;
import ace.actually.villagertourism.blocks.TourismBlockEntity;
import ace.actually.villagertourism.entity.TouristEntity;
import ace.actually.villagertourism.entity.TouristEntityRenderer;
import ace.actually.villagertourism.item.CameraItem;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(VillagerTourism.MODID)
@Mod.EventBusSubscriber
public class VillagerTourism
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "villagertourism";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES,MODID);

    public static final RegistryObject<EntityType<TouristEntity>> TOURIST_ENTITY = ENTITIES.register("tourist",
            ()->EntityType.Builder.of(TouristEntity::new, MobCategory.MISC).sized(0.8f,2).build("villagertourism:tourist"));

    public static final RegistryObject<Block> TOURISM_BLOCK = BLOCKS.register("tourism",()->new TourismBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).randomTicks()));

    public static final RegistryObject<Item> TOURISM_BLOCK_ITEM = ITEMS.register("tourism",()->new BlockItem(TOURISM_BLOCK.get(),new Item.Properties()));
    public static final RegistryObject<Item> CAMERA = ITEMS.register("camera",()->new CameraItem(new Item.Properties()));

    public static final RegistryObject<BlockEntityType<TourismBlockEntity>> TOURISM_BLOCK_ENTITY
            = BLOCK_ENTITIES.register("tourism",()->BlockEntityType.Builder.of(TourismBlockEntity::new,TOURISM_BLOCK.get()).build(null));

    public VillagerTourism(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        BLOCKS.register(modEventBus);

        ITEMS.register(modEventBus);

        ENTITIES.register(modEventBus);

        BLOCK_ENTITIES.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.register(this);

    }

    @SubscribeEvent
    public void entityRenderer(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(TOURIST_ENTITY.get(), TouristEntityRenderer::new);
    }

    @SubscribeEvent
    public void newEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(TOURIST_ENTITY.get(), LivingEntity.createLivingAttributes().add(Attributes.FOLLOW_RANGE,10).build());
    }

    @SubscribeEvent
    public void creative(BuildCreativeModeTabContentsEvent event)
    {
        if(event.getTabKey()==CreativeModeTabs.FUNCTIONAL_BLOCKS)
        {
            event.accept(TOURISM_BLOCK_ITEM::get);
            event.accept(CAMERA.get());
        }
    }


}
