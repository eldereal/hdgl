package hdgl.db.server.bsp;

import static org.junit.Assert.*;
import hdgl.db.conf.GraphConf;
import hdgl.db.server.HConf;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

public class BSPTest {

	@Test
	public void test() throws Exception {
		ZooKeeper zk=HConf.getZooKeeper(GraphConf.getDefault(), null);
		String root="/bsptest";
		try {
            Stat s = zk.exists(root, false);
            if (s == null) {
                zk.create(root, new byte[0], Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);
            }
        } catch (NodeExistsException e) {
           
        } 
		for(int i=0;i<10;i++){
			new BSPRunner(null, zk, root, 10, i).start();
		}
	}

}
