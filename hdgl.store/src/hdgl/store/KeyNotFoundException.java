package hdgl.store;

import java.io.IOException;

public class KeyNotFoundException extends IOException {
    
    /**
     * 
     */
    private static final long serialVersionUID = -6687856359266397341L;
    
    String key;
    
    public KeyNotFoundException(String key){
        super("找不到对应的key:"+key);
        this.key=key;
    }
}
