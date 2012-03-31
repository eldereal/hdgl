package hdgl.store.test;

import static org.junit.Assert.*;

import org.junit.Test;

import hdgl.store.*;

public class FactoryTest {

    @Test
    public void test() {
        Object object = KeyBlobStoreFactory.using("fs");
        assertEquals("fs creation failed", hdgl.store.impl.fs.Impl.class, object.getClass());
        if(object!=KeyBlobStoreFactory.using("fs")){
            fail("Singleton assert not satisfied.");
        }
        Object o2 = KeyBlobStoreFactory.using("hdfs");
        assertEquals("hdfs creation failed", hdgl.store.impl.hdfs.Impl.class, o2.getClass());
        
    }
    
    public static void main(String[] args){
        new FactoryTest().test();
    }

}
