package de.hysky.skyblocker.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import de.hysky.skyblocker.skyblock.dungeon.device.SimonSays;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin implements BlockView {
	@Inject(method = "handleBlockUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z"))
	private void skyblocker$beforeBlockUpdate(CallbackInfo ci, @Local(argsOnly = true) BlockPos pos, @Share("old") LocalRef<@Nullable BlockState> oldState) {
		oldState.set(getBlockState(pos));
	}

	/**
	 * @implNote The {@code pos} can be mutable when this is called by chunk delta updates, so if you want to copy it into memory
	 * (e.g. store it in a field/list/map) make sure to duplicate it via {@link BlockPos#toImmutable()}.
	 */
	@Inject(method = "handleBlockUpdate", at = @At("RETURN"))
	private void skyblocker$afterBlockUpdate(CallbackInfo ci, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) BlockState state, @Share("old") LocalRef<@Nullable BlockState> oldState) {
		SimonSays.onBlockUpdate(pos, state, oldState.get());
	}
}
