package hdgl.store;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * 块数据类型的键值对存储服务
 * @author elm
 *
 */
public interface KeyBlobStore<TK> extends KeyValueStore<TK, InputStream> {
    
    /**
     * 读取一个键的内容，可以对返回的InputStream使用read方法来读取值。
     */
    @Override
    public InputStream get(TK key) throws IOException; 
    
    
    /**
     * 写入一个键的内容，将会从输入的InputStream中读取全部的内容。
     */
    @Override
    public void set(TK key, InputStream value, long updateTimestamp, long timestamp) throws VersionException, IOException;
    
    /**
     * 写入一个键的内容，将会从输入的InputStream中读取全部的内容。此方法可以附加更多的时间戳检查。
     */
    public void set(TK key, InputStream value, long updateTimestamp, long timestamp, TK[] relativeKeys,long[] relativeTimestamps) throws VersionException, IOException; 
    
    /**
     * 获取一个键的输出流以向此键写入内容
     * @param key 要写入的键
     * @param updateTimestamp 要设置的新时间戳
     * @param timestamp 要设置的键的旧时间戳
     * @return 一个输出流，用于向键中写入内容
     * @throws IOException 如果写入过程中出现异常
     */
    public OutputStream set(TK key, long updateTimestamp, long timestamp) throws VersionException, IOException;
    
    /**
     * 获取一个键的输出流以向此键追加内容
     * @param key 要追加的键
     * @param updateTimestamp 要设置的新时间戳
     * @param timestamp 要设置的键的旧时间戳
     * @return 一个输出流，用于向键中追加内容
     * @throws IOException 如果写入过程中出现异常
     */
    public OutputStream append(TK key, long updateTimestamp, long timestamp) throws VersionException, IOException;
    
    /**
     * 将键的内容读取到字节数组中返回
     * @param key 要读取的键
     * @return 该键的全部内容
     * @throws IOException 如果读取过程中出现异常
     */
    public byte[] getByteArray(TK key) throws IOException;
    
    /**
     * 使用字节数组设置键的内容
     * @param key 要设置的键
     * @param value 设置的内容
     * @param updateTimestamp 要设置的新时间戳
     * @param timestamp 要设置的键的旧时间戳
     * @throws IOException 如果写入过程中出现异常
     */
    public void setByteArray(TK key, byte[] value, long updateTimestamp, long timestamp) throws VersionException, IOException; 
    
    /**
     * 使用字节数组追加键的内容
     * @param key 要追加的键
     * @param value 追加的内容
     * @param updateTimestamp 要设置的新时间戳
     * @param timestamp 要设置的键的旧时间戳
     * @throws IOException 如果写入过程中出现异常
     */
    public void appendByteArray(TK key, byte[] value, long updateTimestamp, long timestamp) throws VersionException, IOException; 
    
}
