package hdgl.store;

import java.io.IOException;

/**
 * 该异常表明在更新键值的时候发生了版本错误，需要重新提交更新
 * @author elm
 *
 */
public class VersionException extends IOException {

    /**
     * 
     */
    private static final long serialVersionUID = 6524708894465476914L;

    long requiredTimestamp;
    long givenTimestamp;
    
    public VersionException(long requiredTimestamp, long givenTimestamp) {
        super("提交的时间戳错误，需要"+requiredTimestamp+"，提交"+givenTimestamp);
        this.requiredTimestamp = requiredTimestamp;
        this.givenTimestamp = givenTimestamp;
    }

    public long getRequiredTimestamp() {
        return requiredTimestamp;
    }

    public long getGivenTimestamp() {
        return givenTimestamp;
    }
}
