package cn.bluemobi.dylan.step.bean; /**
 * Copyright 2018 bejson.com
 */

import java.util.Date;
import java.util.List;

/**
 * Auto-generated: 2018-05-30 20:58:36
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class FoodHeatList {

    private List<FoodHeatBean> list;

    public void setList(List<FoodHeatBean> list) {
        this.list = list;
    }

    public List<FoodHeatBean> getList() {
        return list;
    }

    /**
     * Copyright 2018 bejson.com
     */

    /**
     * Auto-generated: 2018-05-30 20:58:36
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */
    public class FoodHeatBean {

        private float heat;
        private long createTime;
        private float shouldHeat;
        private int userId;
        private int recipeId;
        private Date createDate;

        public float getHeat() {
            return heat;
        }

        public void setHeat(float heat) {
            this.heat = heat;
        }

        public void setShouldHeat(float shouldHeat) {
            this.shouldHeat = shouldHeat;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public long getCreateTime() {
            return createTime;
        }

        public float getShouldHeat() {
            return shouldHeat;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getUserId() {
            return userId;
        }

        public void setRecipeId(int recipeId) {
            this.recipeId = recipeId;
        }

        public int getRecipeId() {
            return recipeId;
        }

        public void setCreateDate(Date createDate) {
            this.createDate = createDate;
        }

        public Date getCreateDate() {
            return createDate;
        }

    }

}