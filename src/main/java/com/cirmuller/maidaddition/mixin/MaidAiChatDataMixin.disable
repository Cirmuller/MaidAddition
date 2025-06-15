package com.cirmuller.maidaddition.mixin;

import com.cirmuller.maidaddition.threads.CalculateTaskThread;
import com.github.tartaricacid.touhoulittlemaid.ai.manager.entity.MaidAIChatData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MaidAIChatData.class)
public class MaidAiChatDataMixin {
    @Final
    @Shadow(remap = false) protected EntityMaid maid;

    @Inject(method = "addUserHistory",at = @At("HEAD"),remap = false)
    public void addUserHistory(String message,CallbackInfo ci){
        /*
        TODO
        未来可能打算引入AI来辅助分析女仆应当更换哪些任务，因此在此使用多线程来进行ai运算。
         */
        CalculateTaskThread thread=new CalculateTaskThread(maid,message);
        thread.start();
    }
}
