package hdgl.db.server.bsp;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.antlr.grammar.v3.ANTLRParser.defaultNodeOption_return;
import org.antlr.works.editor.navigation.GoToHistory;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.webapp.hamlet.HamletSpec.Dir;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import hdgl.db.exception.HdglException;
import hdgl.db.graph.Edge;
import hdgl.db.graph.Entity;
import hdgl.db.graph.Vertex;
import hdgl.db.protocol.MessagePackWritable;
import hdgl.db.protocol.MessageWritable;
import hdgl.db.query.QueryContext;
import hdgl.db.query.condition.AbstractCondition;
import hdgl.db.query.condition.BinaryCondition;
import hdgl.db.query.condition.Conjunction;
import hdgl.db.query.condition.NoRestriction;
import hdgl.db.query.expression.Condition;
import hdgl.db.query.stm.StateMachine;
import hdgl.db.server.HConf;
import hdgl.db.store.GraphStore;
import hdgl.util.StringHelper;

public class BSPRunner extends Thread implements Watcher{
	
	static final String readyFile="ready0";
	
	GraphStore graphStore;
	ZooKeeper zk;
	String barrierZkRoot;
	String dieZkRoot;
	int runnerCount;
	int superStep = 0;
	String myname;
	Object mutex = new Object();
	Configuration conf;
	String lockPath;
	String diePath;
	int nodeId;
	QueryContext ctx;
	boolean IamPivot = false;
	BSPContainer container;
	int sessionId;
	
	private static final org.apache.commons.logging.Log Log = LogFactory.getLog(BSPRunner.class);
	
	SortedMap<Long, MessageWritable> received = new TreeMap<Long, MessageWritable>();
	SortedMap<Long, MessageWritable> sent = new TreeMap<Long, MessageWritable>();
	
	public BSPRunner(GraphStore graphStore, QueryContext ctx, String zkRoot,
			int runnerCount, int clientId, int sessionId, BSPContainer container, Configuration conf) throws IOException {
		super();
		this.graphStore = graphStore;
		this.zk = HConf.getZooKeeper(conf, this);
		this.barrierZkRoot = StringHelper.makePath(zkRoot, "b");
		this.dieZkRoot = StringHelper.makePath(zkRoot, "d");
		this.runnerCount = runnerCount;
		this.conf = conf;
		this.myname = "bsp";
		this.nodeId = clientId;
		this.ctx = ctx;
		this.sessionId = sessionId;
		this.container = container;
		this.setDaemon(false);
		//Log.info("init bsp node " + myname);
	}

	public int getSuperStep(){
		return superStep;
	}
	
	boolean die() throws KeeperException, InterruptedException{
		diePath = zk.create(StringHelper.makePath(dieZkRoot, myname), new byte[0], Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
		//Log.info("bsp node " + nodeId +" is dying");
		return zk.getChildren(dieZkRoot, false).size() >= runnerCount;
	}
	
	void alive() throws KeeperException, InterruptedException{
		if(diePath!=null){
			zk.delete(diePath, 0);
		}
		//Log.info("bsp node " + nodeId +" is alive");
	}
	
	
	void enterNoWait() throws KeeperException, InterruptedException{
		lockPath = zk.create(StringHelper.makePath(barrierZkRoot, myname), new byte[0], Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
		//Log.info("bsp node " + nodeId +" entering barrier " + superStep);
	}

	void enterWait() throws KeeperException, InterruptedException{
    	
        int lockNumber = StringHelper.getLastInt(lockPath); 
    	List<String> list = zk.getChildren(barrierZkRoot, false);
        int maxId = -1;
        for(String cn : list){
        	int theirNumber=StringHelper.getLastInt(cn);
        	if(theirNumber>maxId) maxId = theirNumber;
        }
        if (list.size() < runnerCount|| maxId != lockNumber) {
        	IamPivot = false;
	        while (true) {
	            synchronized (mutex) {
	                if(zk.exists(StringHelper.makePath(barrierZkRoot, readyFile), this) == null){
	                	mutex.wait();
	                }else{
	                	//Log.info("bsp node " + nodeId +" has entered barrier " + superStep);
	                    return;
	                }
	            }
	        }
	    }else{	   
	    	IamPivot = true;
	    	zk.create(StringHelper.makePath(barrierZkRoot, readyFile), new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
	    	//Log.info("bsp node " + nodeId +" has entered barrier " + superStep);
        	//Log.info("== all bsp nodes has entered barrier " + superStep+", pivot: "+nodeId+" ==");
            return;
        }
    }
    
	void leaveNoWait() throws InterruptedException, KeeperException{
		zk.delete(lockPath, 0);
		//Log.info("bsp node " + nodeId +" leaving barrier " + superStep);
	}
	
    boolean leaveWait() throws KeeperException, InterruptedException{
        if(IamPivot){
        	while(true){
        		synchronized(mutex){
		        	if(zk.getChildren(barrierZkRoot, true).size()>1){
		        		mutex.wait();
		        	}else{
		        		zk.delete(StringHelper.makePath(barrierZkRoot, readyFile), 0);
		            	//Log.info("bsp node " + nodeId +" has left barrier " + superStep);
		            	//Log.info("== all bsp nodes has left barrier " + superStep+ ", pivot: "+nodeId+" ==");
		                return true;
		        	}
        		}
        	}        	
        } else {
	        while (true) {
	            synchronized (mutex) {
	            	if(zk.exists(StringHelper.makePath(barrierZkRoot, readyFile), this) != null){
	                	mutex.wait();
	                }else{
	                	//Log.info("bsp node " + nodeId +" has left barrier " + superStep);
	                    return true;
	                }
	            }
	        }
        }
    }
    
    void doQuery() throws IOException{
    	if(superStep==0){
    		long ovid,vid=0;
    		int orid,rid=0;
    		for(Map.Entry<Long, Integer> r:ctx.getIdMap().entrySet()){
    			ovid = vid;
    			orid = rid;
    			vid = r.getKey();
    			rid = r.getValue();
    			if(orid == nodeId){
    				int state = ctx.getStateMachine().getStartState();
    				long[] nullpath = new long[0];
    				for(long id = ovid;id<vid;id++){
    					sendMessageToVertex(id, state, nullpath);
    				}
    			}
    		}
    		if(rid==nodeId){
    			int state = ctx.getStateMachine().getStartState();
				long[] nullpath = new long[0];
				long maxid = graphStore.getVertexCount()+1;
				for(long id = vid;id<maxid;id++){
					sendMessageToVertex(id, state, nullpath);
				}
    		}
    	}else{
    		for(Map.Entry<Long, MessageWritable> msg:received.entrySet()){
    			//Log.info(msg.getKey()+" received message");
    			doQueryForVertex(msg.getKey(), msg.getValue());
    		}
    	}
    }
    
    boolean needParse(AbstractCondition cond){
    	if(cond instanceof NoRestriction){
    		return false;
    	}else if(cond instanceof BinaryCondition){
    		return !((BinaryCondition) cond).getLabel().equalsIgnoreCase("id");
    	}else if(cond instanceof Conjunction){
    		for(AbstractCondition subcond:((Conjunction) cond).getConditions()){
    			if(needParse(subcond)){
    				return true;
    			}
    		}
    		return false;
    	}else{
    		return true;
    	}
    }
    
    boolean test(AbstractCondition cond, Entity e){
    	if(cond instanceof NoRestriction){
    		return true;
    	}else if(cond instanceof Conjunction){
    		for(AbstractCondition subcond:((Conjunction) cond).getConditions()){
    			if(!test(subcond, e)){
    				return false;
    			}
    		}
    		return true;
    	}else{
    		throw new HdglException("unknown condition");
    	}
    }
    
    void doQueryForVertex(long vid, MessageWritable msg) throws IOException{
    	Vertex v = null;
    	for(int i = 0;i<msg.size();i++){
    		int stateId = msg.getState(i);
    		long[] path = msg.getPath(i);
    		StateMachine.State state = ctx.getStateMachine().getState(stateId);
    		for(StateMachine.Condition cond:state.getConditions()){
    			if(v == null && needParse(cond.getTest())){
    				v = graphStore.parseVertex(vid);
    			}
    			if(test(cond.getTest(), v)){
    				for(StateMachine.Transition t:cond.getTransitions()){
    					switch(t.getType()){
    					case In:
    						if(v==null) v = graphStore.parseVertex(vid);
    						e:for(Edge e:v.getInEdges()){
    							if(test(t.getTest(), e)){
    								long ovid=e.getInVertex().getId();
    								for(long p:path){
    									if(ovid==p){
    										continue e;
    									}
    								}
    								long[] newpath=new long[path.length+2];
    	    						System.arraycopy(newpath, 0, path, 0, path.length);
    	    						newpath[path.length] = vid;
    	    						newpath[path.length+1] = e.getId();
    	    						int newState = t.getToState();
    	    						sendMessageToVertex(ovid, newState, newpath);
    							}
    						}
    						break;
    					case Out:
    						if(v==null) v = graphStore.parseVertex(vid);
    						e:for(Edge e:v.getOutEdges()){
    							if(test(t.getTest(), e)){
    								long ovid=e.getOutVertex().getId();
    								for(long p:path){
    									if(ovid==p){
    										continue e;
    									}
    								}
    								long[] newpath=new long[path.length+2];
    	    						System.arraycopy(path, 0, newpath, 0, path.length);
    	    						newpath[path.length] = vid;
    	    						newpath[path.length+1] = e.getId();
    	    						int newState = t.getToState();
    	    						sendMessageToVertex(ovid, newState, newpath);
    							}
    						}
    						break;
    					case Backtrack:
    						throw new HdglException("not implemented");
    					case Success:
    						long[] result=new long[path.length+1];
    						System.arraycopy(path, 0, result, 0, path.length);
    						result[path.length] = vid;
    						container.sendResult(sessionId, result);
    						break;
    					default:
    							
    					}
    				}    				
    				break;
    			}
    		}
    	}
    }
    
    synchronized public void receiveMessages(MessagePackWritable msgs){
    	for(int i=0;i<msgs.size();i++){
    		long vid=msgs.getReceiver(i);
    		MessageWritable msg=msgs.getMessage(i);
    		if(received.containsKey(vid)){    			
    			received.get(vid).addAll(msg);
    		}else{
    			received.put(vid, msg);
    		}    		
    	}
    }
    
    void sendMessageToVertex(long vertexId, int newstate, long[] path){
    	MessageWritable msg;
    	if(!sent.containsKey(vertexId)){
    		msg = new MessageWritable();
    		sent.put(vertexId, msg);
    	}else{
    		msg = sent.get(vertexId);
    	}
    	msg.add(newstate, path);
    }
    
    void packAndSendMessage() throws IOException{
    	Map<Integer, MessagePackWritable> packs=new HashMap<Integer, MessagePackWritable>();
    	Iterator<Map.Entry<Long, Integer>> nodePos = ctx.getIdMap().entrySet().iterator();
    	Map.Entry<Long, Integer> range = nodePos.next();
    	long minId = range.getKey();
    	long maxId;
    	int currentRegion=range.getValue();
    	int nextRegion;
    	if(nodePos.hasNext()){
    		range = nodePos.next();
    		maxId = range.getKey();
    		nextRegion = range.getValue();
    	}else{
    		maxId = graphStore.getVertexCount()+1;
    		nextRegion = nodeId;
    	}
    	for(Map.Entry<Long, MessageWritable> m:sent.entrySet()){
    		long id = m.getKey();
    		int destRegionId;
    		if(id<minId){
    			destRegionId = nodeId;
    		}else if(id>=minId&&id<maxId){
    			destRegionId = currentRegion;
    		}else{
    			while(id>maxId){
    				minId = maxId;
		    		currentRegion = nextRegion;
    				if(nodePos.hasNext()){
    		    		range = nodePos.next();    		    		
    		    		maxId = range.getKey();
    		    		nextRegion = range.getValue();
    		    	}else{
    		    		maxId = graphStore.getVertexCount()+1;
    		    		nextRegion = nodeId;
    		    	}
    			}
    			if(id<minId){
        			destRegionId = nodeId;
        		}else{
        			destRegionId = currentRegion;
        		}
    		}
    		MessagePackWritable pack;
    		if(packs.containsKey(destRegionId)){
    			pack = packs.get(destRegionId);
    		}else{
    			pack = new MessagePackWritable();
    			packs.put(destRegionId, pack);
    		}
    		pack.add(m.getKey(), m.getValue());    		
    	}
    	sent.clear();
    	for(Map.Entry<Integer, MessagePackWritable> pack:packs.entrySet()){
    		if(pack.getKey()!=nodeId){
    			container.sendMessagePack(sessionId, pack.getKey(), pack.getValue());
    		}else{
    			receiveMessages(pack.getValue());
    		}
    	}
    }
	
	@Override
	public void run() {
		try{
			if(zk.exists(barrierZkRoot, false)==null){
				zk.create(barrierZkRoot, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			if(zk.exists(dieZkRoot, false)==null){
				zk.create(dieZkRoot, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			while(true){
				//Log.info("node " + nodeId +" working in step " + superStep);
				try {
					doQuery();
					received.clear();
					enterNoWait();
					packAndSendMessage();
					enterWait();
					if(!received.isEmpty()){
						alive();
					}
					leaveNoWait();
					leaveWait();
					if(received.isEmpty()){
						if(die()){
							//Log.info("node " + nodeId +" has died");
							container.finish(sessionId);
							break;
						}
					}
					superStep++;
					container.superStepFinish(sessionId, superStep - 1);
				} catch (Exception e) {
					//Log.error("error during barrier synchronize ", e);
					throw new RuntimeException(e);
				}
			}
		} catch (KeeperException e) {
			throw new HdglException(e);
		} catch (InterruptedException e) {
			throw new HdglException(e);
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
