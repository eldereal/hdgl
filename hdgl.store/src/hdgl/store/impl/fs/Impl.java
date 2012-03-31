package hdgl.store.impl.fs;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hdgl.store.*;

public class Impl implements KeyBlobStoreImpl<String> {

    public static class Conf{
        public File base;
        public boolean throwOnKeyNotFound;
        
        public static Conf parse(String confString){
            Conf conf = new Conf();            
            Pattern pattern=Pattern.compile("\\s*(\\w*)\\s*=(.*)",Pattern.CASE_INSENSITIVE);
            for(String part:confString.split(";")){
                Matcher matcher = pattern.matcher(part);
                if(matcher.matches()){
                    String k=matcher.group(1);
                    String v=matcher.group(2);
                    if(k.equals("base")){
                        conf.base = new File(v);
                    }else if(k.equals("throwOnKeyNotFound")){
                        conf.throwOnKeyNotFound = v.equalsIgnoreCase("true");
                    }
                }                
            }
            return conf;
        }        
    }
    
    @Override
    public KeyBlobStore<String> create(Object conf) {
        if(conf instanceof Conf){
            return new FsStore(((Conf) conf).base, ((Conf) conf).throwOnKeyNotFound);
        }else if(conf instanceof String){
            Conf c=Conf.parse((String)conf);
            return new FsStore(c.base, c.throwOnKeyNotFound);
        }
        return new FsStore(null, false);
    }
    
}
