package cn.bluemobi.dylan.step.bean; /**
 * Copyright 2018 bejson.com
 */

import java.util.Date;
import java.util.List;

/**
 * Auto-generated: 2018-05-28 17:56:48
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class SportHistoryList {

    private List<SportHistoryBean> list;

    public void setList(List<SportHistoryBean> list) {
        this.list = list;
    }

    public List<SportHistoryBean> getList() {
        return list;
    }

   public class SportHistoryBean {

        private float heat;
        private long createTime;
        private float shouldStepNumber;
        private float shouldHeat;
        private float stepNumber;
        private float coefficientHeat;
        private int userId;
        private Date createDate;

       public float getHeat() {
           return heat;
       }

       public void setHeat(float heat) {
           this.heat = heat;
       }

       public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public long getCreateTime() {
            return createTime;
        }

       public float getShouldStepNumber() {
           return shouldStepNumber;
       }

       public void setShouldStepNumber(float shouldStepNumber) {
           this.shouldStepNumber = shouldStepNumber;
       }

       public float getShouldHeat() {
           return shouldHeat;
       }

       public void setShouldHeat(float shouldHeat) {
           this.shouldHeat = shouldHeat;
       }

       public float getStepNumber() {
           return stepNumber;
       }

       public void setStepNumber(float stepNumber) {
           this.stepNumber = stepNumber;
       }

       public float getCoefficientHeat() {
           return coefficientHeat;
       }

       public void setCoefficientHeat(float coefficientHeat) {
           this.coefficientHeat = coefficientHeat;
       }

       public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getUserId() {
            return userId;
        }

        public void setCreateDate(Date createDate) {
            this.createDate = createDate;
        }

        public Date getCreateDate() {
            return createDate;
        }

    }

}