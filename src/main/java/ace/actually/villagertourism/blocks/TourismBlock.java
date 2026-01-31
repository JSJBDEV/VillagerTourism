package ace.actually.villagertourism.blocks;

import ace.actually.villagertourism.VillagerTourism;
import ace.actually.villagertourism.entity.TouristEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TourismBlock extends BaseEntityBlock {
    public TourismBlock(Properties p_49224_) {
        super(p_49224_);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return box(1,0,1,15,16,15);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        super.createBlockStateDefinition(p_49915_.add(BlockStateProperties.HORIZONTAL_FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_49820_) {
        return super.getStateForPlacement(p_49820_).setValue(BlockStateProperties.HORIZONTAL_FACING,p_49820_.getHorizontalDirection());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new TourismBlockEntity(p_153215_,p_153216_);
    }

    @Override
    public void onPlace(BlockState p_60566_, Level p_60567_, BlockPos p_60568_, BlockState p_60569_, boolean p_60570_) {
        super.onPlace(p_60566_, p_60567_, p_60568_, p_60569_, p_60570_);
        if(!p_60567_.isClientSide)
        {
            List<Villager> villagers = p_60567_.getEntitiesOfClass(Villager.class,new AABB(p_60568_.offset(-10,-10,-10),p_60568_.offset(10,10,10)));
            if(villagers.size()<3)
            {
                p_60567_.destroyBlock(p_60568_,true);
            }
        }

    }

    @Override
    public InteractionResult use(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {
        if(p_60504_ instanceof ServerLevel && p_60507_==InteractionHand.MAIN_HAND)
        {
            TourismBlockEntity tbe = (TourismBlockEntity) p_60504_.getBlockEntity(p_60505_);
            if(p_60506_.isCrouching())
            {
                ItemStack emeralds = new ItemStack(Items.EMERALD,tbe.getStored());
                p_60506_.addItem(emeralds);
                tbe.setStored(0);
            }
            else
            {
                p_60506_.sendSystemMessage(Component.literal("Currently stored:").append(" "+tbe.getStored()).append(" ").append("emeralds"));
                p_60506_.sendSystemMessage(Component.literal("Total Reviews:").append(" "+tbe.getTotal()));
            }


        }
        return super.use(p_60503_, p_60504_, p_60505_, p_60506_, p_60507_, p_60508_);
    }

    @Override
    public void randomTick(BlockState p_222954_, ServerLevel p_222955_, BlockPos p_222956_, RandomSource p_222957_) {
        super.randomTick(p_222954_, p_222955_, p_222956_, p_222957_);
        if(p_222955_.random.nextBoolean())
        {
            for (int i = 0; i < p_222955_.random.nextInt(4); i++) {
                TouristEntity touristEntity = new TouristEntity(VillagerTourism.TOURIST_ENTITY.get(),p_222955_);
                touristEntity.setPos(p_222956_.getX(),p_222956_.getY()+1,p_222956_.getZ());
                p_222955_.addFreshEntity(touristEntity);
                touristEntity.setTouristBlockPos(p_222956_);
            }
        }
    }
}
