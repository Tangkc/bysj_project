package cn.bluemobi.dylan.step.bean; /**
 * Copyright 2018 bejson.com
 */

import java.util.Date;
import java.util.List;


/**
 * Auto-generated: 2018-05-30 18:35:49
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class FoodHistoryList {

    private List<FoodHistory> list;

    public void setList(List<FoodHistory> list) {
        this.list = list;
    }

    public List<FoodHistory> getList() {
        return list;
    }

    /**
     * Copyright 2018 bejson.com
     */

    /**
     * Auto-generated: 2018-05-30 18:35:49
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */
    public class FoodHistory {

        private String id;
        private long createTime;
        private String updateTime;
        private String isDelete;
        private int recipeId;
        private int userId;
        private String foodIds;
        private int iconId;
        private int heat;
        private Date date;
        private String iconUrl;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setIsDelete(String isDelete) {
            this.isDelete = isDelete;
        }

        public String getIsDelete() {
            return isDelete;
        }

        public void setRecipeId(int recipeId) {
            this.recipeId = recipeId;
        }

        public int getRecipeId() {
            return recipeId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getUserId() {
            return userId;
        }

        public void setFoodIds(String foodIds) {
            this.foodIds = foodIds;
        }

        public String getFoodIds() {
            return foodIds;
        }

        public void setIconId(int iconId) {
            this.iconId = iconId;
        }

        public int getIconId() {
            return iconId;
        }

        public void setHeat(int heat) {
            this.heat = heat;
        }

        public int getHeat() {
            return heat;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Date getDate() {
            return date;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public String getIconUrl() {
            return iconUrl;
        }

    }

}