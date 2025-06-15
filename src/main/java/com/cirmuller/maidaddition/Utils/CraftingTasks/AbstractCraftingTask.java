package com.cirmuller.maidaddition.Utils.CraftingTasks;

import com.cirmuller.maidaddition.MaidAddition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbstractCraftingTask {


    public enum CraftingCategory{
        /**
         *  本类的作用是标记前一个合成配方的类型，分别为普通合成、染色合成与转化合成。由于材料清单的计算是使用递归方法（动态规划），
         * 因此本类的目的是用于计算材料清单时剪枝，防止计算材料清单时出现诸如铁块-铁锭-铁块这样的循环路径。
         * {@link #NORMAL} 普通合成
         * {@link #TRANSFORM} 转换合成，即原材料只有一种类型的合成
         * {@link #DYE} 染色合成，如各种羊毛的染色
         * {@link #RESULT} 合成的最终结果
         * {@link #ILLEGAL} 非法的合成，包括连续两次染色、循环转换
         **/
        NORMAL,
        DYE,
        TRANSFORM,
        RESULT,
        ILLEGAL;
    }

    protected Ingredient resultIngredient;
    protected AbstractCraftingTask preAbstractCraftingTask;
    protected CraftingCategory preCraftingCategory;

    protected CraftingRecipe preRecipe;
    protected List<CraftingRecipe> allPossibleRoutes;//存储所有合成产物为resultIngredient的配方，此时还没有根据{@link CraftingCategory}剪枝

    protected ServerLevel level;

    protected int count;
    protected static boolean initialized=false;
    protected static List<CraftingRecipe> recipes;
    protected static Logger logger= LogManager.getLogger(MaidAddition.MODID);
    public AbstractCraftingTask(AbstractCraftingTask abstractCraftingTask){
        this.resultIngredient=abstractCraftingTask.resultIngredient;
        this.preAbstractCraftingTask=abstractCraftingTask.preAbstractCraftingTask;
        this.preCraftingCategory=abstractCraftingTask.preCraftingCategory;
        this.preRecipe=abstractCraftingTask.preRecipe;
        this.level=abstractCraftingTask.level;
        this.allPossibleRoutes=abstractCraftingTask.allPossibleRoutes;
        this.count= abstractCraftingTask.count;

    }

    public AbstractCraftingTask(@NonNull ServerLevel level,
                                AbstractCraftingTask parentAbstractCraftingTask,
                                CraftingRecipe parentCraftingRecipe,
                                @Nullable Ingredient ingredientToCraft,
                                @Nullable Integer count){
        this.level=level;
        this.preAbstractCraftingTask = parentAbstractCraftingTask;
        this.preRecipe=parentCraftingRecipe;
        this.resultIngredient=ingredientToCraft;
        if(!initialized){
            recipes=this.level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
            initialized=true;
        }
        this.count=(count==null)?(this.initAndGetCount()):count;
        preCraftingCategory=this.initAndGetPreCraftingCategory();
        allPossibleRoutes= initAndGetAllPossibleRoutes();
    }
    public AbstractCraftingTask(@NonNull ServerLevel level, Item item, Integer count){
        this(level,null,null,Ingredient.of(item),count);
    }

    private int initAndGetCount(){
        int preCountDemand= preAbstractCraftingTask.count;
        int preResultCountPerCrafting=preRecipe.getResultItem(level.registryAccess()).getCount();
        int preResumePerCrafting=getTheNumberOfIngredientResumedPerPreCrafting();
        int craftingTimes=(preCountDemand%preResultCountPerCrafting==0)?(preCountDemand/preResultCountPerCrafting):((preCountDemand/preResultCountPerCrafting)+1);
        return preResumePerCrafting*craftingTimes;
    }


    private CraftingCategory initAndGetPreCraftingCategory(){
        if(preAbstractCraftingTask ==null){
            this.preCraftingCategory=CraftingCategory.RESULT;
            return CraftingCategory.RESULT;
        }

        List<Ingredient> differentIngredient= createDifferentIngredient(this.preRecipe.getIngredients());
        if(differentIngredient.size()==1){
            this.preCraftingCategory=CraftingCategory.TRANSFORM;
            boolean hasCycle= this.checkCycleInTransform(this);
            if(hasCycle){
                this.preCraftingCategory=CraftingCategory.ILLEGAL;
                return CraftingCategory.ILLEGAL;
            }else{
                this.preCraftingCategory=CraftingCategory.TRANSFORM;
                return CraftingCategory.TRANSFORM;
            }

        }else if(differentIngredient.size()==2){
            if(differentIngredient.get(0).getItems()[0].getItem() instanceof DyeItem||
            differentIngredient.get(1).getItems()[0].getItem() instanceof DyeItem){
                this.preCraftingCategory=CraftingCategory.DYE;
                if(preAbstractCraftingTask.preCraftingCategory==CraftingCategory.DYE){
                    this.preCraftingCategory=CraftingCategory.ILLEGAL;
                    return CraftingCategory.ILLEGAL;
                }
                else{
                    return CraftingCategory.DYE;
                }
            }
            else{
                this.preCraftingCategory=CraftingCategory.NORMAL;
                return CraftingCategory.NORMAL;
            }
        }
        else{
            this.preCraftingCategory=CraftingCategory.NORMAL;
            return CraftingCategory.NORMAL;
        }
    }
    private boolean checkCycleInTransform(AbstractCraftingTask rootTask){
        if(preAbstractCraftingTask ==null){
            return false;
        }
        else if(preCraftingCategory==CraftingCategory.TRANSFORM){
            if(isSameIngredient(preAbstractCraftingTask.resultIngredient,rootTask.resultIngredient)){
                return true;
            }
            else{
                return preAbstractCraftingTask.checkCycleInTransform(rootTask);
            }
        }
        else{
            return false;
        }
    }
    public static List<Ingredient> createDifferentIngredient(List<Ingredient> ingredients){
        List<Ingredient> result=new ArrayList<>(ingredients.size());
        for(Ingredient ingredient:ingredients){
            boolean isContainedInResult=false;
            for(Ingredient resultIngredient:result){
                if(isSameIngredient(resultIngredient,ingredient)){
                    isContainedInResult=true;
                    break;
                }
            }
            if(!isContainedInResult){
                result.add(ingredient);
            }
        }
        return result;
    }


    /**
     * 本方法通过传入合成配方生成相应的合成任务列表
     * @param recipe 合成配方
     * @return 本方法根据合成配方生成相应的返回值。特别注意的是，如果合成配方中含有非法的合成，则返回null
     **/
    public List<AbstractCraftingTask> createAbstractCraftingTasksList(CraftingRecipe recipe){
        List<Ingredient> differentIngredient= createDifferentIngredient(recipe.getIngredients());
        List<AbstractCraftingTask> result=new ArrayList<>(differentIngredient.size());
        differentIngredient.forEach((ingredient)->{
            result.add(new AbstractCraftingTask(level,this,recipe,ingredient,null));
        });
        for(AbstractCraftingTask task:result){
            if(task.preCraftingCategory==CraftingCategory.ILLEGAL){
                return null;
            }
        }
        return result;

    }

    private int getTheNumberOfIngredientResumedPerPreCrafting(){
        int result=0;
        List<Ingredient> ingredients=preRecipe.getIngredients();
        for(Ingredient ingredient:ingredients){
            if(isSameIngredient(ingredient,resultIngredient)){
                result++;
            }
        }
        return result;
    }
    private List<CraftingRecipe> initAndGetAllPossibleRoutes(){
        List<CraftingRecipe> result=new ArrayList<>(3);
        for(CraftingRecipe recipe:recipes){
            if(resultIngredient.test(recipe.getResultItem(level.registryAccess()))){
                result.add(recipe);
            }
        }
        return result;
    }
    public static boolean isSameIngredient(Ingredient a,Ingredient b){
        List<Item> aItem=new ArrayList<>(a.getItems().length);
        List<Item> bItem=new ArrayList<>(b.getItems().length);
        Arrays.stream(a.getItems()).toList().forEach((itemStack -> aItem.add(itemStack.getItem())));
        Arrays.stream(b.getItems()).toList().forEach((itemStack -> bItem.add(itemStack.getItem())));
        return isEqualItemList(aItem,bItem);
    }

    public static boolean isEqualItemList(List<Item> a,List<Item> b){
        for(Item item:a){
            if(!b.contains(item)){
                return false;
            }
        }
        for(Item item:b){
            if(!a.contains(item)){
                return false;
            }
        }
        return true;
    }
    public int getCount(){
        return count;
    }

    public boolean isLegalCrafting(){
        return preCraftingCategory!=CraftingCategory.ILLEGAL;
    }
    public ItemList getMaterialsList(){
        List<ItemList> allPossibleItemList=new ArrayList<>();
        for(CraftingRecipe recipe:allPossibleRoutes){
            List<AbstractCraftingTask> tasks= createAbstractCraftingTasksList(recipe);
            if(tasks!=null){
                ItemList currentItemList=new ItemList();
                for(AbstractCraftingTask task:tasks){
                    currentItemList.addAll(task.getMaterialsList());
                }
                allPossibleItemList.add(currentItemList);
            }
        }
        if(allPossibleItemList.isEmpty()){
            ItemList result=new ItemList();
            result.add(new ItemStack(this.resultIngredient.getItems()[0].getItem(),this.count));
            return result;

        }
        else{
            return ItemList.getAbundantItemList(allPossibleItemList);
        }
    }


}
