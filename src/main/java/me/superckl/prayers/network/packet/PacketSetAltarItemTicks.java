package me.superckl.prayers.network.packet;

import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import me.superckl.prayers.block.entity.AltarTileEntity;
import me.superckl.prayers.client.ClientHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

@RequiredArgsConstructor
public class PacketSetAltarItemTicks {

	private final BlockPos pos;
	private final int ticks;

	public void encode(final PacketBuffer buffer) {
		buffer.writeBlockPos(this.pos);
		buffer.writeVarInt(this.ticks);
	}

	public static PacketSetAltarItemTicks decode(final PacketBuffer buffer) {
		return new PacketSetAltarItemTicks(buffer.readBlockPos(), buffer.readVarInt());
	}

	public void handle(final Supplier<NetworkEvent.Context> supplier) {
		if(supplier.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
			supplier.get().enqueueWork(() -> {
				if(!ClientHelper.getLevel().isAreaLoaded(this.pos, 0))
					return;
				final TileEntity te = ClientHelper.getLevel().getBlockEntity(this.pos);
				if(!(te instanceof AltarTileEntity))
					return;
				final AltarTileEntity aTE = (AltarTileEntity) te;
				aTE.setItemTicks(this.ticks);
			});
		supplier.get().setPacketHandled(true);
	}

}
