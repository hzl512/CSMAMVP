/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ab.task.thread;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

import android.os.Handler;
import android.os.Message;

import com.ab.task.AbTaskItem;
import com.ab.task.AbTaskListListener;
import com.ab.task.AbTaskObjectListener;
import com.ab.util.AbLogUtil;

// TODO: Auto-generated Javadoc

/**
 * © 2012 amsoft.cn
 * 名称：AbTaskQueue.java 
 * 描述：线程队列.
 *
 * @author 还如一梦中
 * @version v1.0
 * @date：2011-11-10 下午11:52:13
 */
public class AbTaskQueue extends Thread { 
	
	/** 等待执行的任务. 用 LinkedList增删效率高*/
	private LinkedList<AbTaskItem> taskItemList = null;
  	
  	/** 停止的标记. */
	private boolean quit = false;
	
	/**  存放返回的任务结果. */
    private HashMap<String,Object> result;
	
	/** 执行完成后的消息句柄. */
    private Handler handler = new Handler() { 
        @Override 
        public void handleMessage(Message msg) { 
        	AbTaskItem item = (AbTaskItem)msg.obj; 
        	if(item.getListener() instanceof AbTaskListListener){
        		((AbTaskListListener)item.getListener()).update((List<?>)result.get(item.toString())); 
        	}else if(item.getListener() instanceof AbTaskObjectListener){
        		((AbTaskObjectListener)item.getListener()).update(result.get(item.toString())); 
        	}else{
        		item.getListener().update(); 
        	}
        	result.remove(item.toString());
        } 
    }; 
    
    /**
     * 
     * 构造.
     * @return
     */
    public static AbTaskQueue newInstance() { 
        AbTaskQueue abTaskQueue = new AbTaskQueue();
        return abTaskQueue;
    } 
	
	/**
	 * 构造执行线程队列.
	 */
    private AbTaskQueue() {
    	quit = false;
    	taskItemList = new LinkedList<AbTaskItem>();
    	result = new HashMap<String,Object>();
    	//从线程池中获取
    	Executor mExecutorService  = AbThreadFactory.getExecutorService();
    	mExecutorService.execute(this); 
    }
    
    /**
     * 开始一个执行任务.
     *
     * @param item 执行单位
     */
    public void execute(AbTaskItem item) { 
         addTaskItem(item); 
    } 
    
    
    /**
     * 开始一个执行任务并清除原来队列.
     * @param item 执行单位
     * @param cancel 清空之前的任务
     */
    public void execute(AbTaskItem item,boolean cancel) { 
	    if(cancel){
	    	 cancel(true);
	    }
    	addTaskItem(item); 
    } 
     
    /**
     * 描述：添加到执行线程队列.
     *
     * @param item 执行单位
     */
    private synchronized void addTaskItem(AbTaskItem item) { 
    	taskItemList.add(item);
    	//添加了执行项就激活本线程 
        this.notify();
        
    } 
 
    /**
     * 描述：线程运行.
     *
     * @see java.lang.Thread#run()
     */
    @Override 
    public void run() { 
        while(!quit) { 
        	try {
        	    while(taskItemList.size() > 0){
            
					AbTaskItem item = taskItemList.remove(0);
					//定义了回调
				    if (item!=null && item.getListener() != null) {
				        if(item.getListener() instanceof AbTaskListListener){
                            result.put(item.toString(), ((AbTaskListListener)item.getListener()).getList());
                        }else if(item.getListener() instanceof AbTaskObjectListener){
                            result.put(item.toString(), ((AbTaskObjectListener)item.getListener()).getObject());
                        }else{
                        	item.getListener().get();
                            result.put(item.toString(), null);
                        }
				    	//交由UI线程处理 
				        Message msg = handler.obtainMessage(); 
				        msg.obj = item; 
				        handler.sendMessage(msg); 
				    } 
				    
				    //停止后清空
				    if(quit){
				    	taskItemList.clear();
				    	return;
				    }
        	    }
        	    try {
					//没有执行项时等待 
					synchronized(this) { 
					    this.wait();
					}
				} catch (InterruptedException e) {
					AbLogUtil.e("AbTaskQueue","收到线程中断请求");
					e.printStackTrace();
					//被中断的是退出就结束，否则继续
					if (quit) {
						taskItemList.clear();
	                    return;
	                }
	                continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
        } 
    } 
    
    /**
     * 描述：终止队列释放线程.
     *
     * @param interrupt the may interrupt if running
     */
    public void cancel(boolean interrupt){
		try {
			quit  = true;
			if(interrupt){
				interrupted();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public LinkedList<AbTaskItem> getTaskItemList() {
		return taskItemList;
	}

	public int getTaskItemListSize() {
		return taskItemList.size();
	}
    
}

