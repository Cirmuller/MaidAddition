package com.cirmuller.maidaddition.datagen;

import com.google.common.collect.Lists;
import com.ibm.icu.impl.Pair;
import org.jline.utils.ShutdownHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaskKey {
    private String task;
    /*
    Map的key表示匹配用正则表达式,value表示权重,即每成功匹配一个正则表达式，增加相应的权重，权重最高者为task。因事件之间独立性较差，用概率计算会比较复杂，在此不做赘述。
     */
    private Map<String,Float> matchString;
    private String language;
    public TaskKey(String task,Map<String,Float> matchString,String language){
        this.task=task;
        this.matchString=matchString;
        this.language=language;
    }

    public String getTask() {
        return task;
    }
    public Map<String, Float> getMatchString(){
        return this.matchString;
    }
    public String getLanguage(){
        return this.language;
    }
    public static TaskKey IDLE;
    public static TaskKey ATTACK;
    public static TaskKey BOW_ATTACK;
    public static TaskKey CROSSBOW_ATTACK;
    public static TaskKey DANMUKU_ATTACK;
    public static TaskKey TRIDENT_ATTACK;
    public static TaskKey FARM;
    public static TaskKey SUGARCANE;
    public static TaskKey MELON;
    public static TaskKey COCO;
    public static TaskKey HONEY;
    public static TaskKey GRASS;
    public static TaskKey SNOW;
    public static TaskKey FEED_OWNER;
    public static TaskKey MILK;
    public static TaskKey TORCH;
    public static TaskKey BREED_ANIMAL;
    public static TaskKey FISH;
    public static TaskKey FIRE;
    public static TaskKey GAME;
    public static TaskKey CHUNK_LOADING;
    public static TaskKey SLAVE_WORK;
    public static TaskKey SHEARS;
    static {
        IDLE=new TaskKey("idle",Map.of("\\S*闲散\\S*",1.0f,"\\S*放松\\S*",1.0f,"\\S*停止\\S*",1.0f,"\\S*不\\S{1,2}休息\\S*",-1.0f,"\\S*别休息\\S*",-1.0f,
                "\\S*休息\\S*",0.9f),"zh_cn");
        ATTACK=new TaskKey("attack",Map.of("\\S*攻击\\S*",0.6f,"\\S*进攻\\S*",0.8f),"zh_cn");
        BOW_ATTACK=new TaskKey("ranged_attack",Map.of("\\S*弓\\S*",1.0f,"\\S*攻击\\S*",0.8f,"\\S*进攻\\S*",0.8f),"zh_cn");
        CROSSBOW_ATTACK=new TaskKey("crossbow_attack",Map.of("\\S*弩\\S*",1.0f,"\\S*攻击\\S*",0.8f,"\\S*进攻\\S*",0.8f),"zh_cn");
        DANMUKU_ATTACK=new TaskKey("danmaku_attack",Map.of("\\S*弹幕\\S*",1.0f,"\\S*攻击\\S*",0.8f,"\\S*进攻\\S*",0.8f),"zh_cn");
        TRIDENT_ATTACK=new TaskKey("trident_attack",Map.of("\\S*三叉戟\\S*",1.0f,"\\S*攻击\\S*",0.8f,"粪叉",0.6f,"\\S*进攻\\S*",0.8f ),"zh_cn");
        FARM=new TaskKey("farm",Map.of("\\S*农活\\S*",0.8f,"\\S*下地\\S*干活\\S*",0.8f,"\\S*种地\\S*",0.8f,"\\S*种\\S*",0.2f ),"zh_cn");
        SUGARCANE=new TaskKey("sugar_cane",Map.of("\\S*甘蔗\\S*",1.0f ),"zh_cn");
        MELON=new TaskKey("melon",Map.of("\\S*西瓜\\S*",0.9f,"\\S*南瓜\\S*",0.9f,"\\S*瓜\\S*",0.1f ),"zh_cn");
        COCO=new TaskKey("cocoa",Map.of("\\S*可可\\*",0.9f ),"zh_cn");
        HONEY=new TaskKey("honey",Map.of("\\S*蜂蜜\\S*",0.9f,"\\S*蜜\\S*",0.1f ),"zh_cn");
        GRASS=new TaskKey("grass",Map.of("\\S*除草\\S*",0.9f,"\\S*拔草\\S*",0.9f ),"zh_cn");
        SNOW =new TaskKey("snow",Map.of("\\S*铲雪\\S*",0.9f,"\\S*除雪\\S*",0.6f ),"zh_cn");
        FEED_OWNER=new TaskKey("feed",Map.of("\\S*(关心|喂)(主人|我)\\S*",0.9f,"\\S*投喂\\S*",0.3f,"\\S*东西\\S*吃\\S*",0.5f),"zh_cn");
        MILK=new TaskKey("milk",Map.of("\\S*牛奶\\S*",0.9f,"\\S*奶\\S*",0.6f ),"zh_cn");
        TORCH=new TaskKey("torch",Map.of("\\S*火把\\S*",0.7f,"\\S*照明\\S*",0.8f ),"zh_cn");
        BREED_ANIMAL=new TaskKey("feed_animal",Map.of("\\S*繁殖动物\\S*",1.0f,"\\S*喂\\S*动物\\S*",0.9f),"zh_cn");
        FISH=new TaskKey("fishing",Map.of("\\S*钓鱼\\S*",1.0f ),"zh_cn");
        FIRE=new TaskKey("extinguishing",Map.of("\\S*灭火\\S*",1.0f ),"zh_cn");
        GAME=new TaskKey("board_games",Map.of("\\S*游戏\\S*",0.6f,"\\S*下\\S*棋\\S*",0.9f),"zh_cn");
        //CHUNK_LOADING=new TaskKey("chunk_loading_task",Map.of("\\S*加载区块\\S*",0.9f ),"zh_cn");
        SLAVE_WORK=new TaskKey("hand_crank_task",Map.of("\\S*打工\\S*",0.4f,"\\S*摇(手摇曲柄|曲柄|扳手)\\S*",0.8f),"zh_cn");
        SHEARS=new TaskKey("shears",Map.of("\\S*羊毛\\S*",0.6f),"zh_cn");
    }
}
