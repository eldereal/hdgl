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
	
	private static final org.apache.commons.logging.Log Log = LogFactory.getLog(BSPRunner.class);
	
	
	public BSPRunner(GraphStore graphStore, String zkRoot,
			int runnerCount, int clientId, Configuration conf) throws IOException {
		super();
		this.graphStore = graphStore;
		this.zk = HConf.getZooKeeper(conf, this);
		this.zkRoot = zkRoot;
		this.runnerCount = runnerCount;
		this.conf = conf;
		this.myname = NetHelper.getMyHostName()+"-"+clientId;
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
        Log.info("bsp node " + myname +" entering barrier " + superStep);
        List<String> list = zk.getChildren(zkRoot, false);
        if (list.size() < runnerCount) {
	        while (true) {
	            synchronized (mutex) {
	                if(zk.exists(StringHelper.makePath(zkRoot, "ready"), this) == null){
	                	mutex.wait();
	                }else{
	                	Log.info("bsp node " + myname +" has entered barrier " + superStep);
	                    return true;
	                }
	            }
	        }
	    }else{	   
	    	try{
	    		zk.create(StringHelper.makePath(zkRoot, "ready"), new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
	    	}catch(NodeExistsException ex){
	    		
	    	}
	    	Log.info("bsp node " + myname +" has entered barrier " + superStep);
        	Log.info("== all bsp nodes has entered barrier " + superStep+" ==");
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
        Log.info("bsp node " + myname +" leaving barrier " + superStep);
        List<String> list = zk.getChildren(zkRoot, false);
        if (list.size() > 1) {
	        while (true) {
	            synchronized (mutex) {
	            	if(zk.exists(StringHelper.makePath(zkRoot, "ready"), this) != null){
	                	mutex.wait();
	                }else{
	                	Log.info("bsp node " + myname +" has left barrier " + superStep);
	                    return true;
	                }
	            }
	        }
        }else{
        	try{
        		zk.delete(StringHelper.makePath(zkRoot, "ready"), 0);
            }catch(NoNodeException ex){
	    		
	    	}
        	Log.info("bsp node " + myname +" has left barrier " + superStep);
        	Log.info("== all bsp nodes has left barrier " + superStep+" ==");
            return true;
        }
    }
	
	@Override
	public void run() {
		try{
			while(true){
				Log.info("node " + myname +" working in step " + superStep);			
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
