package com.cirmuller.maidaddition.capability;


import com.cirmuller.maidaddition.api.IChunkLoadingCapability;
import com.cirmuller.maidaddition.api.ICraftingAndCarryingCapability;
import net.minecraftforge.common.capabilities.*;



public class ModCapability {
    public static final Capability<IChunkLoadingCapability> CHUNK_LOADING_CAPABILITY= CapabilityManager.get(new CapabilityToken<>(){});







}
