package cn.bluemobi.dylan.step.util;

public class NetRequestUtil {

    private static final String BASE_URL = "http://172.31.43.22:8080";

    /**
     * 获取当日饮食推荐
     */
    public static final String GET_TODAY_FOOD = BASE_URL + "/recipe/queryTodayFoodInfo";
    /**
     * 登陆接口
     */
    public static final String LOGIN_URL = BASE_URL + "/login/appLogin";
    /**
     * 注册地址
     */
    public static final String REGEST_URL = BASE_URL + "/user/addUserInfo";
    /**
     * 同步步数到服务器
     */
    public static final String UPLOAD_STEP_URL = BASE_URL + "/recipe/updateTodayExerciseInfo";
    /**
     * 获取用户信息
     */
    public static final String GET_USER_INFO = BASE_URL + "/user/queryUserInfo";
    /**
     * 获取今天的运动处方
     */
    public static final String GET_TODAY_SPORTS = BASE_URL + "/recipe/queryTodayExerciseInfo";
    /**
     * 更新用户信息
     */
    public static final String UPDATA_USER_INFO = BASE_URL + "/user/updateUserInfo";
    /**
     * 获取运动历史
     */
    public static final String GET_SPORTS_HISTORY = BASE_URL + "/recipe/queryStatisticsExerciseInfo";
    /**
     * 上传今日饮食图片地址
     */
    public static final String UPLOAD_FOODIMG_URL = BASE_URL + "/recipe/uploadImage";
    /**
     * 获取膳食历史
     */
    public static final String GET_FOOD_HISTORY = BASE_URL + "/recipe/queryHistoryFoodList";
    /**
     * 获取一周的膳食热量
     */
    public static final String GET_FOOD_JILU = BASE_URL + "/recipe/queryStatisticsMealInfo";
    /**
     * 上传今日饮食信息
     */
    public static final String UPLOAD_TODAYFOOD_INFON = BASE_URL + "/recipe/addTodayFoodInfo";

}
