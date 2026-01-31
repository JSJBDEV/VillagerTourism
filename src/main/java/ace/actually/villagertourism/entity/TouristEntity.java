package ace.actually.villagertourism.entity;

import ace.actually.villagertourism.VillagerTourism;
import ace.actually.villagertourism.blocks.TourismBlockEntity;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.AABB;

import java.util.*;

public class TouristEntity extends PathfinderMob {

    private BlockPos origin = null;
    private String comments = "";
    private long spawnTime = -1;
    private  BlockPos touristBlockPos = null;
    private boolean payed = false;

    public TouristEntity(EntityType<? extends PathfinderMob> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        origin = getOnPos();
        spawnTime = level().getGameTime();
        shouldBeSaved();

        List<AbstractContraptionEntity> ridables = level().getEntitiesOfClass(
                AbstractContraptionEntity.class,
                new AABB(blockPosition().offset(-5,-5,-5),blockPosition().offset(5,5,5)));

        for(AbstractContraptionEntity ace: ridables)
        {
            Map<UUID,Integer> seatMap = ace.getContraption().getSeatMapping();
            Collection<Integer> seatIndex = seatMap.values();

            if(seatIndex.size()<ace.getContraption().getSeats().size())
            {
                int seat = random.nextInt(ace.getContraption().getSeats().size());
                while (seatIndex.contains(seat))
                {
                    seat = random.nextInt(ace.getContraption().getSeats().size());
                }


                ace.addSittingPassenger(this,seat);
                startRiding(ace,true);
                return;
            }
        }

        //if the code has gotten this far, there are no create contraptions to ride on
        List<Entity> others = level().getEntitiesOfClass(
                Entity.class,
                new AABB(blockPosition().offset(-5,-5,-5),blockPosition().offset(5,5,5)));

        for(Entity e: others)
        {
            if(!(e instanceof LivingEntity))
            {
                startRiding(e);
                return;
            }
        }
        setPersistenceRequired();

    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        if(origin!=null)
        {
            tag.putIntArray("origin",new int[]{origin.getX(),origin.getY(),origin.getZ()});
        }
        if(touristBlockPos!=null)
        {
            tag.putIntArray("touristBlockPos",new int[]{touristBlockPos.getX(),touristBlockPos.getY(),touristBlockPos.getZ()});
        }
        if(comments!=null)
        {
            tag.putString("comments",comments);
        }
        tag.putLong("spawnTime",spawnTime);
        tag.putBoolean("payed",payed);
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        int[] v =  tag.getIntArray("origin");
        if(v.length==3)
        {
            origin = new BlockPos(v[0],v[1],v[2]);
        }
        int[] w = tag.getIntArray("touristBlockPos");
        if(v.length==3)
        {
            touristBlockPos = new BlockPos(w[0],w[1],w[2]);
        }

        spawnTime = tag.getLong("spawnTime");
        comments = tag.getString("comments");
        payed = tag.getBoolean("payed");
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0,new RandomLookAroundGoal(this));
        goalSelector.addGoal(7,new RandomStrollGoal(this,0.2f));
    }

    @Override
    public void tick() {
        super.tick();
        if(!comments.contains("1000"))
        {
            if(getOnPos().distToCenterSqr(origin.getCenter())>1000000)
            {
                comments+="1000,";
                shouldBeSaved();
            }
        }
        else
        {
            //we only need to check if 2000 has been reached if 1000 has been reached
            if(!comments.contains("2000"))
            {
                if(getOnPos().distToCenterSqr(origin.getCenter())>2000000)
                {
                    comments+="2000,";
                    shouldBeSaved();
                }
            }

        }

        if(level() instanceof ServerLevel sl)
        {
            Set<Structure> structures = sl.structureManager().getAllStructuresAt(getOnPos()).keySet();
            for(Structure structure: structures)
            {
                String s = sl.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(structure).toString();
                if(!comments.contains(s))
                {
                    comments+=s+",";
                    shouldBeSaved();
                }
            }
        }
        if(level().getGameTime()>spawnTime+1000)
        {
            if(getOnPos().distToCenterSqr(origin.getCenter())<400)
            {
                if(!payed)
                {
                    int points = comments.split(",").length-1;
                    if(touristBlockPos==null)
                    {
                        ItemStack emeralds = new ItemStack(Items.EMERALD,points);
                        ItemEntity entity = new ItemEntity(level(),getX(),getY(),getZ(),emeralds);
                        level().addFreshEntity(entity);
                    }
                    else
                    {
                        TourismBlockEntity tbe = (TourismBlockEntity) level().getBlockEntity(touristBlockPos);
                        tbe.increment(points);
                    }

                    kill();
                    payed = true;
                    shouldBeSaved();
                }

            }
        }
    }

    public void setTouristBlockPos(BlockPos touristBlockPos) {
        this.touristBlockPos = touristBlockPos;
        shouldBeSaved();
    }
}
