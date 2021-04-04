package com.glisco.numismaticoverhaul.block;

import com.glisco.numismaticoverhaul.network.SetShopOffersS2CPacket;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShopBlock extends BlockWithEntity {

    public ShopBlock() {
        super(FabricBlockSettings.of(Material.STONE).breakByTool(FabricToolTags.PICKAXES).nonOpaque().hardness(5.0f));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new ShopBlockEntity();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            if (!player.isSneaking()) {
                player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
                ((ServerPlayerEntity) player).networkHandler.sendPacket(SetShopOffersS2CPacket.create(((ShopBlockEntity) world.getBlockEntity(pos)).getOffers()));
            } else {
                ((ShopMerchant) ((ShopBlockEntity) world.getBlockEntity(pos)).getMerchant()).updateTrades();
                ((ShopBlockEntity) world.getBlockEntity(pos)).getMerchant().setCurrentCustomer(player);
                ((ShopBlockEntity) world.getBlockEntity(pos)).getMerchant().sendOffers(player, new TranslatableText("gui.numismatic-overhaul.shop.merchant_title"), 0);
            }
        }
        return ActionResult.SUCCESS;
    }
}
