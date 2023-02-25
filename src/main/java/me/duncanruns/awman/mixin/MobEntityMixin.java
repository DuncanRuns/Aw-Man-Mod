package me.duncanruns.awman.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    private boolean creeperSpawnChecked = false;

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void creeperSpawnerMixin(CallbackInfo ci) {
        if (creeperSpawnChecked) return;
        else if (world.isClient || getScoreboardTags().contains("noMoreCreeper")) {
            creeperSpawnChecked = true;
            return;
        }

        creeperSpawnChecked = true;
        getScoreboardTags().add("noMoreCreeper");

        MobEntity thisMobEntity = (MobEntity) (Object) this;

        CreeperEntity creeper = new CreeperEntity(EntityType.CREEPER, world);
        creeper.refreshPositionAndAngles(this.getBlockPos(), 0f, 0f);
        if (thisMobEntity instanceof PassiveEntity || thisMobEntity.isPersistent()) {
            creeper.setPersistent();
        }
        creeper.getScoreboardTags().add("noMoreCreeper");
        world.spawnEntity(creeper);

    }
}
