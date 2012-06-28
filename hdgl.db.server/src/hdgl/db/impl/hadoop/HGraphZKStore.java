package hdgl.db.impl.hadoop;

import hdgl.db.graph.HdglError;
import hdgl.db.graph.HdglException;
import hdgl.db.graph.Node;
import hdgl.db.graph.Relationship;
import hdgl.db.impl.hadoop.logio.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.RegEx;

import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.eclipse.jdt.internal.compiler.env.IGenericField;

public class HGraphZKStore {

	private static final Pattern ZK_SEQ_ID_PATTERN=Pattern.compile("-?\\d{1,10}$");
	private static final String ZK_HDGL_ROOT_PATH="/hdgl";
	private String ZK_ROOT_PATH="/hdgl/%s";
	private String ZK_NODES_PATH="/hdgl/%s/n";
	private String ZK_NODES_ROOT="/hdgl/%s/n/";
	
	private String ZK_RELATIONS_PATH="/hdgl/%s/r";
	private String ZK_RELATIONS_ROOT="/hdgl/%s/r/";
	
	private String ZK_NODETYPE_PATH="/hdgl/%s/nt";	
	private String ZK_NODETYPE_ROOT="/hdgl/%s/nt/";
	
	private String ZK_RELATIONTYPE_PATH="/hdgl/%s/rt";
	private String ZK_RELATIONTYPE_ROOT="/hdgl/%s/rt/";
	
	private String ZK_SESSION_PATH = "/hdgl/%s/s";
	private String ZK_SESSION_ROOT = "/hdgl/%s/s/";
	
	private String ZK_LABEL_PATH = "/hdgl/%s/l";
	private String ZK_LABEL_ROOT = "/hdgl/%s/l/";
	private String ZK_LABEL_ID_PATH = "/hdgl/%s/l/i";
	private String ZK_LABEL_ID_ROOT = "/hdgl/%s/l/i/";
	
	private static Logger Log = Logger.getLogger(HGraphZKStore.class);
	
	Configuration configuration;
	ZooKeeper zk;
	
	Map<Class<?>, Integer> typeMap = new HashMap<Class<?>, Integer>();
	Map<String, Integer> labelIdCache=new HashMap<String, Integer>();
	String ns;
	
	public HGraphZKStore(Configuration configuration, ZooKeeper zk) throws HdglException, InterruptedException {
		this.configuration = configuration;
		this.zk = zk;	
		this.ns = HConf.getGraphNamespace(configuration);
		initConf();
		init();
	}
	
	private int zkAllocId(String base, byte[] data, CreateMode mode) throws KeeperException, InterruptedException{
		if(mode==CreateMode.EPHEMERAL){
			mode=CreateMode.EPHEMERAL_SEQUENTIAL;
		}
		if(mode==CreateMode.PERSISTENT){
			mode=CreateMode.PERSISTENT_SEQUENTIAL;
		}
		String actualPath = zk.create(base, data, Ids.OPEN_ACL_UNSAFE, mode);
		int id = Integer.parseInt(actualPath.substring(base.length()));
		if(id==0){//we will never use 0 as an id
			actualPath = zk.create(base, data, Ids.OPEN_ACL_UNSAFE, mode);
			id = Integer.parseInt(actualPath.substring(base.length()));
		}
		return id;
	}
	
	private void initConf(){
		ZK_ROOT_PATH=String.format(ZK_ROOT_PATH,ns);
		
		ZK_NODES_PATH=String.format(ZK_NODES_PATH,ns);
		ZK_NODES_ROOT=String.format(ZK_NODES_ROOT,ns);
		
		ZK_RELATIONS_PATH=String.format(ZK_RELATIONS_PATH,ns);
		ZK_RELATIONS_ROOT=String.format(ZK_RELATIONS_ROOT,ns);
		
		ZK_NODETYPE_PATH = String.format(ZK_NODETYPE_PATH,ns);
		ZK_NODETYPE_ROOT=String.format(ZK_NODETYPE_ROOT,ns);
		ZK_RELATIONTYPE_PATH = String.format(ZK_RELATIONTYPE_PATH,ns);
		ZK_RELATIONTYPE_ROOT=String.format(ZK_RELATIONTYPE_ROOT,ns);
		
		ZK_SESSION_PATH = String.format(ZK_SESSION_PATH,ns);
		ZK_SESSION_ROOT = String.format(ZK_SESSION_ROOT,ns);
		
		ZK_LABEL_PATH = String.format(ZK_LABEL_PATH,ns);
		ZK_LABEL_ROOT = String.format(ZK_LABEL_ROOT,ns);
		ZK_LABEL_ID_PATH = String.format(ZK_LABEL_ID_PATH,ns);
		ZK_LABEL_ID_ROOT = String.format(ZK_LABEL_ID_ROOT,ns);
		
	}
	
	public void init() throws InterruptedException, HdglException{
		try{
			if(zk.exists(ZK_NODES_PATH, false)!=null){
				return;
			}
			if(zk.exists(ZK_HDGL_ROOT_PATH, false)==null){
				zk.create(ZK_HDGL_ROOT_PATH, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			zk.create(ZK_ROOT_PATH, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			
			zk.create(ZK_LABEL_PATH, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.create(ZK_LABEL_ID_PATH, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.create(ZK_NODES_PATH, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.create(ZK_NODETYPE_PATH, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.create(ZK_NODETYPE_ROOT + Node.class.getName(), intToBytes(0), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.create(ZK_RELATIONS_PATH, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.create(ZK_RELATIONTYPE_PATH, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.create(ZK_RELATIONTYPE_ROOT + Relationship.class.getName(), intToBytes(0), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			zk.create(ZK_SESSION_PATH, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			
		}catch (KeeperException e) {
			throw new HdglException(e);
		}
	}
	
	
	public int getOrAllocLabelId(String name) throws HdglException, InterruptedException{
		try{
			if(labelIdCache.containsKey(name)){
				return labelIdCache.get(name);
			}
			String path = ZK_LABEL_ROOT + name;
			Stat stat;
			int id;
			if((stat=zk.exists(path, false))!=null){
				id = bytesToInt(zk.getData(path, false, stat));
			}else{
				byte[] namebytes=name.getBytes();
				id = zkAllocId(ZK_LABEL_ID_ROOT, namebytes, CreateMode.PERSISTENT);
				zk.create(path, intToBytes(id), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);				
			}
			labelIdCache.put(name, id);
			return id;
		}catch (KeeperException e) {
			throw new HdglException(e);
		}
	}
	
	public int allocSessionId() throws HdglException, InterruptedException{
		try {
			return zkAllocId(ZK_SESSION_ROOT, new byte[0], CreateMode.EPHEMERAL);
		} catch (KeeperException e) {
			throw new HdglException(e);
		} 
	}
	
	public int allocNodeId() throws InterruptedException, HdglException{
		try{
			int id=zkAllocId(ZK_NODES_ROOT, new byte[0],CreateMode.PERSISTENT	);
			if(id>0){
				return id;
			}else{
				throw new HdglError.NodeIdsOverflowError();
			}
		}catch (KeeperException e) {
			throw new HdglException(e);
		}
	}
	
	public int allocRelationshipId() throws InterruptedException, HdglException{
		try{
			int id=zkAllocId(ZK_RELATIONS_ROOT, new byte[0],CreateMode.PERSISTENT);
			if(id>0){
				return -id;
			}else{
				throw new HdglError.RelationshipIdsOverflowError();
			}
		}catch (KeeperException e) {
			throw new HdglException(e);
		}
	}
	
	private String typenameToZKPath(Class<?> type){
		if(Node.class.isAssignableFrom(type)){
			return ZK_NODETYPE_ROOT+type.getName();
		}else if(Relationship.class.isAssignableFrom(type)){
			return ZK_RELATIONTYPE_ROOT+type.getName();
		}else{
			throw new HdglError.IllegalTypeError("type", "Node or Relationshio", type.getName());
		}
	}
	
	private int bytesToInt(byte[] data){
		return data[0]<<24 | data[1]<<16 | data[2]<<8 | data[3];
	}
	
	private byte[] intToBytes(int num){
		byte[] data=new byte[4];
		data[0] = (byte) (num>>>24);
		data[1] = (byte) (num>>>16);
		data[2] = (byte) (num>>>8);
		data[3] = (byte) (num);
		return data;
	}
	
	private int inheritLevel(int num){
		int first = num & 0x000000ff;
		if(first==0){
			return 0;
		}else{
			for(int l= 1;l<7;l++){
				int lth_id=num>>>(4+l*4) & 0x0000000f;
				if(lth_id==0){
					return l;
				}
			}
			return 7;
		}
	}
	
	/**
	 * 查询一个类型对应的Id，如果该类型在系统中不存在，则注册该类型
	 * @param type
	 * @return
	 * @throws InterruptedException 
	 * @throws HdglException 
	 */
	public int getOrAllocTypeId(Class<?> type) throws InterruptedException, HdglException{
		if(typeMap.containsKey(type)){
			return typeMap.get(type);
		}else{
			try{
				String path = typenameToZKPath(type);
				Stat s =zk.exists(path, false);
				int id;
				if(s!=null){
					byte[] data = zk.getData(path, false, s);
					id = bytesToInt(data);
				}else{
					id = allocTypeId(type);
				}
				typeMap.put(type, id);
				return id;
			}catch (KeeperException e) {
				throw new HdglException(e);
			}
		}
	}
	
	/**
	 * 一个type id由7个部分组成，每个部分代表一个继承层次，第一层长度为8,其余每个部分长度为4，因此系统中的一个Node类最多继承7层(Node算作第0层)，
	 * Node最多拥有255个子类，
	 * 除此之外的同一个NodeType类最多可以拥有15个子类
	 * @param type
	 * @return 高32位是该类型对应的层次，低32位是该类型的id
	 * @throws HdglException 
	 * @throws InterruptedException 
	 */
	private int allocTypeId(Class<?> type) throws HdglException, InterruptedException{
		if(type.equals(Node.class)||type.equals(Relationship.class)){
			return 0;
		}
		else if(Node.class.isAssignableFrom(type)||Relationship.class.isAssignableFrom(type)){
			try{
				int parentid;
				if(type.getSuperclass().equals(Node.class)){
					parentid = 0;
				}else{
					String base = typenameToZKPath(type.getSuperclass());
					Stat s = zk.exists(base, false);					
					if(s==null){
						parentid = allocTypeId(type.getSuperclass());							
					}else{
						byte[] data = zk.getData(base, false, s);
						parentid = bytesToInt(data);
					}
				}
				int parentlevel = inheritLevel(parentid);
				if(parentlevel>=7){
					throw new HdglError.MaximumInheritLevelError();
				}
				String base = typenameToZKPath(type.getSuperclass());
				int myidx=zkAllocId(base+"/", intToBytes(parentid), CreateMode.PERSISTENT);
				if(myidx>=(parentlevel==0?255:15)){
					throw new HdglError.MaximumSubclassesOfTypeError(type.getSuperclass().getName(), (parentlevel==0?255:15));
				}
				int myid;
				if(parentlevel==0){
					myid = myidx;
				}else{
					myid = parentid | myidx<<(4+parentlevel*4);
				}
				Log.debug("alloc type id for "+type.getName()+": " + String.format("0x%8x", myid) );
				zk.create(typenameToZKPath(type), intToBytes(myid), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				return myid;
			}catch (KeeperException e) {
				throw new HdglException(e);
			}
		}else{
			throw new HdglError.IllegalTypeError("type", "Node or Relationship", type.getName());
		}
	}
}
