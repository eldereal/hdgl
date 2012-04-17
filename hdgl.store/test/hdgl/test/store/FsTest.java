package hdgl.test.store;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import hdgl.store.KeyBlobStore;
import hdgl.store.KeyBlobStoreFactory;
import hdgl.store.VersionException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class FsTest {

    static KeyBlobStore<String> store;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        store = KeyBlobStoreFactory.using("fs", String.class)
                .create("base=data/test");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        store.close();
    }

    
    @Test
    public void functionality() throws IOException {
        long t = 0;
        String message="Hello, World!";
        if(store.exists("test")){
            t = store.getTimestamp("test");
        }else{
            store.setByteArray("test", new byte[0], t, t);
        }
        long nt = store.currentTimestamp() - 3600;
        try{
            store.set("test", store.currentTimestamp(), t+10);
            fail("未进行时间戳检查");
        }catch(VersionException ex){
            assertEquals("抛出错误的时间戳异常",t,ex.getRequiredTimestamp());
            assertEquals("抛出错误的时间戳异常",t+10,ex.getGivenTimestamp());
        }
        store.setByteArray("test", message.getBytes(), nt+10, t);
        store.setByteArray("test", message.getBytes(), nt, nt+10);
        assertEquals("存储时间戳与读取不一致", nt,store.getTimestamp("test"));
        assertEquals("存储与读取不一致", message,new String(store.getByteArray("test")));
        
        String k2="test/2";
        long t2 = 0;
        String message2="Another Test!";
        if(store.exists(k2)){
            t2 = store.getTimestamp(k2);
        }
        long nt2 = store.currentTimestamp()- 3600;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(message2.getBytes());
        try{
            store.set(k2, inputStream, store.currentTimestamp(), t2+10);
            fail("未进行时间戳检查");
        }catch(VersionException ex){
            assertEquals("抛出错误的时间戳异常",t2,ex.getRequiredTimestamp());
            assertEquals("抛出错误的时间戳异常",t2+10,ex.getGivenTimestamp());
        }
        try{
            store.set(k2, inputStream, store.currentTimestamp(), nt2, new String[]{"test"},new long[]{nt+10});
            fail("未进行时间戳检查");
        }catch(VersionException ex){
            assertEquals("抛出错误的时间戳异常",nt,ex.getRequiredTimestamp());
            assertEquals("抛出错误的时间戳异常",nt+10,ex.getGivenTimestamp());
        }
        store.set(k2, inputStream, nt2, t2,new String[]{"test"},new long[]{nt});
        
        assertEquals("存储时间戳与读取不一致", nt2,store.getTimestamp(k2));
        assertEquals("存储与读取不一致", message2,new String(store.getByteArray(k2)));
        
        
    }

}
