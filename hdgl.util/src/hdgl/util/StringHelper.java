package hdgl.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 字符串的辅助方法
 * @author elm
 *
 */
public class StringHelper {
    
    
    /**
     * 不能实例化辅助类
     */
    private StringHelper(){
        
    }
    
    /**
     * <p>将字符串编码为文件路径名<p>
     * <p>规则如下：</p>
     * <ul>
     *  <li>以下字符将被原样输出：a-z, A-Z, 0-9, _(下划线), -(减号)</li>
     *  <li>三个分隔线字符: / | \ 将会被转换为平台的路径分隔符，Windows下是'\\', Unix下是'/'</li>
     *  <li>其它字符会被转换为"$xxxx"，其中xxxx是该字符的十六进制Unicode值</li>
     * </ul>
     * @param name
     * @return
     */
    public static String filenameEncode(String name){
        StringBuilder fn=new StringBuilder();
        for(int i=0;i<name.length();i++){
            char c=name.charAt(i);
            if((c>'a'&&c<'z')||(c>'A'&&c<'Z')||(c>'0'&&c<'9')||c=='_'||c=='-'){
                fn.append(c);
            }else if(c=='/'||c=='\\'||c=='|'){
                if(fn.length()>0){
                    fn.append(".d");
                }
                fn.append(File.separatorChar);
            }else{
                int num=(int)c;
                fn.append('$');
                fn.append(String.format("%4x", num));
            }
        }
        fn.append(".k");
        return fn.toString();
    }
    
    public static String formatTimestamp(long timestamp){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(new Date(timestamp));
    }
    
}
