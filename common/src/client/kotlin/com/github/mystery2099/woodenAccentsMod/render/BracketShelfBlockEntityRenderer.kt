package com.github.mystery2099.woodenAccentsMod.render

import com.github.mystery2099.woodenAccentsMod.block.custom.BracketShelfBlock
import com.github.mystery2099.woodenAccentsMod.block.entity.custom.BracketShelfBlockEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemDisplayContext
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis


/**
 * Renders stored items against the shelf's front face. Slots run left to right
 * from the viewer's perspective; the first slot lies clockwise from [BracketShelfBlock.facing].
 */
class BracketShelfBlockEntityRenderer(context: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<BracketShelfBlockEntity> {

    private val itemRenderer = context.itemRenderer

    override fun render(
        blockEntity: BracketShelfBlockEntity,
        tickDelta: Float,
        matrices: PoseStack,
        vertexConsumers: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        val world = blockEntity.level ?: return
        val facing = blockEntity.blockState.getValue(BracketShelfBlock.facing)
        val leftDirection = facing.clockWise
        val slotsCount = BracketShelfBlockEntity.SLOT_COUNT

        for (slot in 0 until slotsCount) {
            val stack = blockEntity.getItem(slot)
            if (stack.isEmpty) continue

            val model = itemRenderer.getModel(stack, world, null, slot)
            val scale = if (model.isGui3d) BLOCK_ITEM_SCALE else FLAT_ITEM_SCALE

            // Offset from the center toward the first slot.
            val slotOffset = (1.0 - slot) / slotsCount
            matrices.pushPose()
            matrices.translate(
                0.5 + leftDirection.stepX * slotOffset - facing.stepX * DEPTH_OFFSET,
                ITEM_Y,
                0.5 + leftDirection.stepZ * slotOffset - facing.stepZ * DEPTH_OFFSET
            )
            matrices.mulPose(Axis.YP.rotationDegrees(itemYaw(facing)))
            matrices.scale(scale, scale, scale)
            itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                light,
                OverlayTexture.NO_OVERLAY,
                matrices,
                vertexConsumers,
                world,
                0
            )
            matrices.popPose()
        }
    }

    /** Rotates items to face the viewer, as vanilla lecterns do. */
    private fun itemYaw(facing: Direction): Float = -facing.clockWise.toYRot()

    companion object {
        /** Y position used to render shelf items. */
        private const val ITEM_Y = 0.875

        /** Moves items toward the wall, clear of the shelf's front edge. */
        private const val DEPTH_OFFSET = 0.1875
        private const val BLOCK_ITEM_SCALE = 0.5f
        private const val FLAT_ITEM_SCALE = 0.625f
    }
}
