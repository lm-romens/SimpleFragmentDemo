package cn.edu.neusoft.simplefragmentdemo.ui;

/**
 * @author liang mei
 * @create 2017/4/8
 * @description
 */

public class SinglePattenTest {

    private static volatile SinglePattenTest instance=null;

    public static SinglePattenTest getInstance(){
        SinglePattenTest pattenTest=instance;
        if (pattenTest==null) {
            synchronized (SinglePattenTest.class) {
                pattenTest = instance;
                if (pattenTest == null) {
                    instance = pattenTest = new SinglePattenTest();
                }
            }
        }
        return pattenTest;
    }

    public static void destory(){
        instance=null;
    }


}
