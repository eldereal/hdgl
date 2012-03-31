package hdgl.store;
import java.io.Closeable;
import java.io.IOException;
/**
 * <p>用于存储、读取、查询键值对的服务接口。</p>
 * <p>该服务采用版本型并发控制，无事务，并保证最终一致性。详细说明如下：</p>
 * <ul>
 *  <li>读操作是无锁的，写操作将锁定被改写的键</li>
 *  <li>每一个键有一个时间戳属性，该属性中存储该键最后一次修改的时间</li>
 *  <li>
 *    在更新某一个键的数据时，必须提供该键最后一次修改的时间戳。
 *    为达成此目的，至少需要先检索一次时间戳。
 *    如果更新的时候，发现提供的时间戳和最新的时间戳不相等，
 *    说明该值可能被其他进程并发的更新过了，可能会导致数据的不一致。
 *    此时更新会失败，抛出VersionException异常。
 *  </li>
 *  <li>
 *    更新的时候，可以显式提供其他某些键的时间戳。
 *    如果这样做了，将会检查每一个提供的时间戳，只要有一个时间戳不符，
 *    更新就会失败。
 *  </li>
 *  <li>
 *    某一个键更新之后，并不一定能被立刻访问到。
 *    由于此接口只规定了最终一致性，用户应该假设更新之后有一段时间的非一致窗口。
 *    在这段时间内读到的仍不是最新的值。但具体实现可能会提供更强的一致性。
 *  </li>
 *  <li>
 *    还有一个要点是对时间戳的检查是强一致性的。
 *    只要有用户改写了时间戳，之后的时间戳检查会立刻使用新的值。
 *    但由于非一致窗口内另一位用户可能无法读取到新的时间戳，
 *    导致这段时间内其他用户可能无法通过检查。
 *    以上只是最弱的要求，而具体实现可能会提供更强的一致性。
 *  </li>
 *  <li>
 *    时间戳的物理意义可能会根据具体实现而变化，例如某个实现中时间戳以秒为单位，
 *    另一种实现中可能以毫秒为单位。为了通用性，用户不应该对时间戳的含义进行假设，
 *    并始终使用getTimestamp和currentTimestamp方法返回的值作为时间戳。
 *    对这些值进行修改可能会导致无意义的值，并导致更新失败。
 *  </li>
 * </ul>
 * @author elm
 * 
 * @param <TK> 键的类型
 * @param <TV> 值的类型
 */
public interface KeyValueStore<TK, TV> extends Closeable {
    
    /**
     * 获取该实现中当前时刻的时间戳
     * @return 当前时刻的时间戳
     */
    public long currentTimestamp();
    
    /**
     * 查询一个键是否存在
     * @param key 要查询的键
     * @return 如果存在，则返回true，否则返回False
     * @throws IOException 如果读写出现错误
     */
    public boolean exists(TK key) throws IOException;
    
    /**
     * 根据键查询一个值
     * @param key 要查询的键
     * @return 被查询的值
     */
    public TV get(TK key) throws KeyNotFoundException, IOException;
    
    /**
    * 查询某个键的更新时间戳
    * @param key 要查询的键
    * @return 该键在最后更新时的时间戳
    */
   public long getTimestamp(TK key) throws KeyNotFoundException, IOException;    
    
    /**
     * 设置一个键的值
     * @param key 被设置的键
     * @param value 要设置的值
     * @param updateTimestamp 要设置的时间戳
     * @param timestamp 要设置的键的旧时间戳，如果是新建一个键，那么将忽略旧的时间戳并总是成功
     * @throws VersionException 如果timestamp和旧的时间戳不相等，更新会失败并抛出此异常
     * 
     */
    public void set(TK key, TV value, long updateTimestamp, long timestamp) throws VersionException, IOException; 

    
    /**
     * 设置一个键的值，并附加更多的时间戳检查
     * @param key 被设置的键
     * @param value 要设置的值
     * @param updateTimestamp 要设置的时间戳
     * @param timestamp 要设置的键的旧时间戳
     * @param relativeKeys 其它相关的键
     * @param relativeTimestamps 其它相关的键的时间戳，本方法假设该数组长度等于relativeKeys的长度，并且不检查实际长度
     * @throws VersionException 如果timestamp和旧的时间戳不相等，或者与relativeKeys中某一个键对应的relativeTimestamps与旧时间戳不相等，更新会失败并抛出此异常
     * 
     */
    public void set(TK key, TV value, long updateTimestamp, long timestamp, TK[] relativeKeys,long[] relativeTimestamps) throws VersionException, IOException;
    
    /**
     * 删除一个键
     * @param key 被删除的键
     * @param timestamp 被删除的键的时间戳
     */
    public void delete(TK key, long timestamp) throws VersionException, IOException;
    
    @Override
    public void close() throws IOException;
}
