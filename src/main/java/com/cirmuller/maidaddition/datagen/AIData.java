package com.cirmuller.maidaddition.datagen;

import com.cirmuller.maidaddition.MaidAddition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import com.cirmuller.maidaddition.datagen.TaskKey;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PathPackResources;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.LanguageProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AIData implements DataProvider{

    List<TaskKey> listOfTaskKey;
    PackOutput output;
    Logger logger= LogManager.getLogger(MaidAddition.MODID);
    public AIData(PackOutput output){
        listOfTaskKey=new ArrayList<>();
        this.output=output;
    }

    private void addlistOfTaskKey(){
        listOfTaskKey.add(TaskKey.IDLE);
        listOfTaskKey.add(TaskKey.ATTACK);
        listOfTaskKey.add(TaskKey.BOW_ATTACK);
        listOfTaskKey.add(TaskKey.CROSSBOW_ATTACK);
        listOfTaskKey.add(TaskKey.DANMUKU_ATTACK);
        listOfTaskKey.add(TaskKey.TRIDENT_ATTACK);
        listOfTaskKey.add(TaskKey.FARM);
        listOfTaskKey.add(TaskKey.SUGARCANE);
        listOfTaskKey.add(TaskKey.MELON);
        listOfTaskKey.add(TaskKey.COCO);
        listOfTaskKey.add(TaskKey.HONEY);
        listOfTaskKey.add(TaskKey.GRASS);
        listOfTaskKey.add(TaskKey.SNOW);
        listOfTaskKey.add(TaskKey.FEED_OWNER);
        listOfTaskKey.add(TaskKey.MILK);
        listOfTaskKey.add(TaskKey.TORCH);
        listOfTaskKey.add(TaskKey.BREED_ANIMAL);
        listOfTaskKey.add(TaskKey.FISH);
        listOfTaskKey.add(TaskKey.FIRE);
        listOfTaskKey.add(TaskKey.GAME);
        listOfTaskKey.add(TaskKey.SHEARS);
        //listOfTaskKey.add(TaskKey.CHUNK_LOADING);
        listOfTaskKey.add(TaskKey.SLAVE_WORK);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        String json = getJsonString();
        //return CompletableFuture.allOf((CompletableFuture[])List.of(DataProvider.saveStable(cachedOutput, JsonParser.parseString(json),output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve("ai").resolve("lang").resolve("zh_cn.json")))
        //       .toArray((x)->{return new CompletableFuture[x];}));
        logger.info("Output dir of AIData: "+output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve("ai").resolve("lang").resolve("zh_cn.json"));
        return DataProvider.saveStable(cachedOutput, JsonParser.parseString(json),output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve("ai").resolve("lang").resolve("zh_cn.json"));


    }

    @Override
    public String getName() {
        return "AIData";
    }

    public String getJsonString(){
        this.addlistOfTaskKey();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(listOfTaskKey);
    }
}
