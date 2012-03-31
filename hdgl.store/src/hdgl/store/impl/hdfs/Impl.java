package hdgl.store.impl.hdfs;

import hdgl.store.*;

public class Impl implements KeyBlobStoreImpl<String> {

    @Override
    public KeyBlobStore<String> create(Object conf) {
        throw new UnsupportedOperationException("hdfs not implemented");
    }
    
}
