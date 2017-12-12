package com.jjyh.it.utiles.network;


/**
 * ClassName:ThreadPoolProxyFactory. <br/>
 * Function: 线程池工厂. <br/>
 * Date:     2017年03月06日 19:54<br/>
 *
 * @author suxinwei
 */
public class ThreadPoolProxyFactory {
    static ThreadPoolProxy mProxy;

    /**
     * 创建线程池代理
     */
    public static ThreadPoolProxy createPoolProxy() {
        if (mProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mProxy == null) {
                    mProxy = new ThreadPoolProxy(5);
                }
            }
        }
        return mProxy;
    }

}
