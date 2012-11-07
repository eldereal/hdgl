package hdgl.db.server.bsp;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import hdgl.db.server.HConf;
import hdgl.db.server.HGRegion;
import hdgl.db.store.GraphStore;
import hdgl.util.NetHelper;
import hdgl.util.StringHelper;

public class BSPRunner extends Thread implements Watcher{
	
	GraphStore graphStore;
	ZooKeeper zk;
	String zkRoot;
	int runnerCount;
	int superStep = 0;
	String myname;
	Object mutex = new Object();
	Configuration conf;
	String lockPath;
	int nodeId;
	boolean IamPivot = false;
	
	private static final org.apache.commons.logging.Log Log = LogFactory.getLog(BSPRunner.class);
	
	
	public BSPRunner(GraphStore graphStore, String zkRoot,
			int runnerCount, int clientId, Configuration conf) throws IOException {
		super();
		this.graphStore = graphStore;
		this.zk = HConf.getZooKeeper(conf, this);
		this.zkRoot = zkRoot;
		this.runnerCount = runnerCount;
		this.conf = conf;
		this.myname = "bsp";
		this.nodeId = clientId;
		this.setDaemon(false);
		Log.info("init bsp node " + myname);
	}

	/**
     * Join barrier
     *
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */

    boolean enter() throws KeeperException, InterruptedException{
    	lockPath = zk.create(StringHelper.makePath(zkRoot, myname), new byte[0], Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
        int lockNumber = StringHelper.getLastInt(lockPath); 
    	Log.info("bsp node " + nodeId +" entering barrier " + superStep);
        List<String> list = zk.getChildren(zkRoot, false);
        int maxId = -1;
        for(String cn : list){
        	int theirNumber=StringHelper.getLastInt(cn);
        	if(theirNumber>maxId) maxId = theirNumber;
        }
        if (list.size() < runnerCount|| maxId != lockNumber) {
        	IamPivot = false;
	        while (true) {
	            synchronized (mutex) {
	                if(zk.exists(StringHelper.makePath(zkRoot, "ready"), this) == null){
	                	mutex.wait();
	                }else{
	                	Log.info("bsp node " + nodeId +" has entered barrier " + superStep);
	                    return true;
	                }
	            }
	        }
	    }else{	   
	    	IamPivot = true;
	    	try{
	    		zk.create(StringHelper.makePath(zkRoot, "ready"), new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
	    	}catch(NodeExistsException ex){
	    		
	    	}
	    	Log.info("bsp node " + nodeId +" has entered barrier " + superStep);
        	Log.info("== all bsp nodes has entered barrier " + superStep+", pivot: "+nodeId+" ==");
            return true;
        }
    }
    
    /**
     * Wait until all reach barrier
     *
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    boolean leave() throws KeeperException, InterruptedException{
        zk.delete(lockPath, 0);
        Log.info("bsp node " + nodeId +" leaving barrier " + superStep);
        List<String> list = zk.getChildren(zkRoot, false);
        if (list.size() > 1 || !IamPivot) {
	        while (true) {
	            synchronized (mutex) {
	            	if(zk.exists(StringHelper.makePath(zkRoot, "ready"), this) != null){
	                	mutex.wait();
	                }else{
	                	Log.info("bsp node " + nodeId +" has left barrier " + superStep);
	                    return true;
	                }
	            }
	        }
        }else{
        	try{
        		zk.delete(StringHelper.makePath(zkRoot, "ready"), 0);
            }catch(NoNodeException ex){
	    		
	    	}
        	Log.info("bsp node " + nodeId +" has left barrier " + superStep);
        	Log.info("== all bsp nodes has left barrier " + superStep+ ", pivot: "+nodeId+" ==");
            return true;
        }
    }
	
	@Override
	public void run() {
		try{
			while(true){
				Log.info("node " + nodeId +" working in step " + superStep);			
				try {
					if(superStep > 10){
						break;
					}
					enter();
					leave();
					superStep++;
				} catch (Exception e) {
					Log.error("error during barrier synchronize ", e);
					throw new RuntimeException(e);
				}
			}
		}finally{
			try {
				zk.close();
			} catch (InterruptedException e) {
				
			}			
		}
	}

	@Override
	public void process(WatchedEvent e) {
		synchronized (mutex) {
            mutex.notify();
        }
	}
	
}
