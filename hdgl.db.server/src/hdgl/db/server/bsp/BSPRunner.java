package hdgl.db.server.bsp;

import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

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
	
	private static final org.apache.commons.logging.Log Log = LogFactory.getLog(BSPRunner.class);
	
	
	public BSPRunner(GraphStore graphStore, ZooKeeper zk, String zkRoot,
			int runnerCount, int clientId) {
		super();
		this.graphStore = graphStore;
		this.zk = zk;
		this.zkRoot = zkRoot;
		this.runnerCount = runnerCount;
		this.myname = NetHelper.getMyHostName()+"-"+clientId;
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
        zk.create(StringHelper.makePath(zkRoot, myname), new byte[0], Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
        Log.info("bsp node " + myname +" entering barrier " + superStep);
        List<String> list = zk.getChildren(zkRoot, false);
        if (list.size() < runnerCount) {
	        while (true) {
	            synchronized (mutex) {
	                Stat stat = zk.exists(StringHelper.makePath(zkRoot, "ready"), this);
	                if(stat==null){
	                	mutex.wait();
	                }else{
	                	Log.info("bsp node " + myname +" has entered barrier " + superStep);
	                    return true;
	                }
	            }
	        }
	    }else{	   
	    	zk.create(StringHelper.makePath(zkRoot, "ready"), new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        	Log.info("bsp node " + myname +" has entered barrier " + superStep);
        	Log.info("== all bsp node " + myname +" has entered barrier " + superStep+" ==");
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
        zk.delete(StringHelper.makePath(zkRoot, myname) + "/" + myname, 0);
        Log.info("bsp node " + myname +" leaving barrier " + superStep);
        while (true) {
            synchronized (mutex) {
                List<String> list = zk.getChildren(zkRoot, true);
                    if (list.size() > 0) {
                        mutex.wait();
                    } else {
                    	Log.info("bsp node " + myname +" has left barrier " + superStep);
                        return true;
                    }
                }
            }
    }
	
	@Override
	public void run() {
		while(true){
			Log.info("node " + myname +" working in step " + superStep);
			try {
				enter();
				leave();
			} catch (Exception e) {
				Log.error("error during barrier synchronize ", e);
			}			
		}
	}

	@Override
	public void process(WatchedEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
