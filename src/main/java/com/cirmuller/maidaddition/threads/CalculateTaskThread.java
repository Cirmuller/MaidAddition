package com.cirmuller.maidaddition.threads;

import com.cirmuller.maidaddition.MaidAddition;
import com.cirmuller.maidaddition.datagen.AIData;
import com.cirmuller.maidaddition.datagen.TaskKey;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibm.icu.impl.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.data.PackOutput;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public class CalculateTaskThread extends Thread{
    private EntityMaid maid;
    private String message;
    private static Logger logger= LogManager.getLogger(MaidAddition.MODID);
    private static String AISite= String.format(".\\config\\%s\\ai.json",MaidAddition.MODID);
    private static List<TaskKey> listOfTask=new ArrayList<>();
    public static void init(){
        File file=new File(AISite);
        if(!file.exists()){
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                OutputStream ostream = new FileOutputStream(file);
                AIData aiData=new AIData(new PackOutput(Path.of(AISite)));
                String json=aiData.getJsonString();
                ostream.write(json.getBytes(StandardCharsets.UTF_8));
                ostream.close();

            }catch (IOException e){
                e.printStackTrace();
            }
        }
        try{
            InputStream istream=new FileInputStream(file);
            String json;
            json = new String(istream.readAllBytes(),StandardCharsets.UTF_8);
            Gson gson = new Gson();
            java.lang.reflect.Type listType = new TypeToken<ArrayList<TaskKey>>() {
            }.getType();
            listOfTask = gson.fromJson(json, listType);
            istream.close();


        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        logger.info("AIData output dir: "+ file.getPath());
    }
    public CalculateTaskThread(EntityMaid maid,String message){
        this.maid=maid;
        this.message=message;
    }

    @Override
    public void run(){
        IMaidTask task=calculateTask();
        synchronized (maid){
            if(task!=null){
                maid.setTask(task);
                logger.info(String.format("Successfully set maid%d's task!",maid.getId()));
            }
        }
    }

    /*
        TODO
     */
    private IMaidTask calculateTask(){
        Map<TaskKey,Float> taskKeysWithScore=new HashMap<>();
        listOfTask.forEach((taskKey)->{
            final float[] score = {0.0f};
            taskKey.getMatchString().forEach(
                    (key,value)->{
                        if(Pattern.matches(key,message)){
                            score[0] = score[0] +value;
                        }
                    }
            );
            taskKeysWithScore.put(taskKey,score[0]);
        });
        TaskKey highestScore=taskKeysWithScore.keySet().stream().sorted((a,b)->{
            if(taskKeysWithScore.get(a)-taskKeysWithScore.get(b)<-0.05){
                return 1;
            }
            else if(taskKeysWithScore.get(a)-taskKeysWithScore.get(b)>0.05) {
                return -1;
            }
            else {
                return 0;
            }
        }).toList().get(0);
        if(taskKeysWithScore.get(highestScore)>0.2){
            IMaidTask result;
            if((result=TaskManager.findTask(new ResourceLocation(TouhouLittleMaid.MOD_ID,highestScore.getTask())).orElse(null))!=null){
                return result;
            }
            else if((result=TaskManager.findTask(new ResourceLocation(MaidAddition.MODID,highestScore.getTask())).orElse(null))!=null){
                return result;
            }else{
                return null;
            }

        }
        else{
            return null;
        }

    }
}
