
package com.solut.menu.mixin;

import com.solut.menu.AdminFeatures;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderManager.class)
public abstract class BlockRenderManagerMixin {

    @Inject(
        method = "renderBlock",
        at = @At("HEAD"),
        cancellable = true
    )
    private void soluteMenu$filterBlocks(
            BlockState state,
            BlockPos pos,
            BlockRenderView world,
            MatrixStack matrices,
            VertexConsumer vertexConsumer,
            boolean cull,
            Random random,
            CallbackInfo ci) {

        if (!AdminFeatures.isXrayEnabled()) {
            return;
        }

        boolean ore =
                state.isIn(BlockTags.COAL_ORES)
                || state.isIn(BlockTags.IRON_ORES)
                || state.isIn(BlockTags.COPPER_ORES)
                || state.isIn(BlockTags.GOLD_ORES)
                || state.isIn(BlockTags.REDSTONE_ORES)
                || state.isIn(BlockTags.LAPIS_ORES)
                || state.isIn(BlockTags.DIAMOND_ORES)
                || state.isIn(BlockTags.EMERALD_ORES)
                || state.isOf(Blocks.ANCIENT_DEBRIS);

        if (!ore) {
            ci.cancel();
        }
    }
}
