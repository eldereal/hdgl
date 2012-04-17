package hdgl.store.impl.fs;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import hdgl.store.KeyBlobStore;
import hdgl.store.KeyNotFoundException;
import hdgl.store.VersionException;

import static hdgl.util.StringHelper.filenameEncode;
import static hdgl.util.StringHelper.formatTimestamp;
import static hdgl.util.StreamHelper.transfer;

public class FsStore implements KeyBlobStore<String> {

    
    File baseDir;
    boolean throwOnKeyNotFound;
    
    public FsStore(File baseDir, boolean throwOnKeyNotFound){
        this.baseDir = baseDir!=null?baseDir:new File(".");
        this.throwOnKeyNotFound = throwOnKeyNotFound;
    }
    
    private File getFile(String key){
        String s = filenameEncode(key);
        return new File(baseDir.getAbsolutePath()+File.separatorChar+s);
    }
    
    private void createFile(File file) throws IOException{        
        if(!file.exists()){
            File d=file.getParentFile();
            if(!d.exists()){
                if(!d.mkdirs()){
                    throw new IOException("创建文件夹出错:"+d.getAbsolutePath());
                }    
            }        
            if(!file.createNewFile()){
                throw new IOException("创建文件出错:"+file.getAbsolutePath());
            }
        }
    }
    
    private File checkTimestamp(String key, long timestamp) throws VersionException{
        File file = getFile(key);
        if(file.exists()){
            long t=file.lastModified()/1000;
            if(t!=timestamp){
                throw new VersionException(file.lastModified()/1000, timestamp);
            }
        }
        return file;
    }
    
    private void setTimestamp(File file, long timestamp) throws IOException{
        if(file.exists()){
            if(file.setLastModified(timestamp*1000)){
                return;
            }
        }
        throw new IOException("修改时间戳时出错("+formatTimestamp(timestamp)+"):"+file.getAbsolutePath());
    }
    
    @Override
    public long getTimestamp(String key) throws IOException {
        File file=getFile(key);
        if(file.exists()){
            return file.lastModified()/1000;
        }else{
            if(throwOnKeyNotFound){
                throw new KeyNotFoundException(key);
            }else{
                return 0;
            }
        }
    }

    @Override
    public InputStream get(String key) throws IOException {
        File file=getFile(key);
        if(file.exists()){
            return new FileInputStream(file);
        }else{
            if(throwOnKeyNotFound){
                throw new KeyNotFoundException(key);
            }else{
                return null;
            }
        }
    }

    @Override
    public void set(String key, InputStream value, long updateTimestamp,
            long timestamp) throws VersionException, IOException {
        File file = checkTimestamp(key, timestamp);
        createFile(file);        
        OutputStream out = null;
        try{       
            out = new FileOutputStream(file);
            transfer(value, out);
        }finally{
            if(out!=null){
                out.close();
            }
            setTimestamp(file, updateTimestamp);
        }        
    }

    @Override
    public void set(String key, InputStream value, long updateTimestamp,
            long timestamp, String[] relativeKeys, long[] relativeTimestamps)
            throws VersionException, IOException {
        for(int i=0;i<relativeKeys.length;i++){
            checkTimestamp(relativeKeys[i], relativeTimestamps[i]);
        }
        set(key, value, updateTimestamp, timestamp);
    }

    @Override
    public OutputStream set(String key, final long updateTimestamp, long timestamp)
            throws VersionException, IOException {
        final File file = checkTimestamp(key, timestamp);
        createFile(file);
        return new FileOutputStream(file){
            @Override
            public void close() throws IOException{
                super.close();
                setTimestamp(file, updateTimestamp);
            }
        };
    }

    @Override
    public OutputStream append(String key, final long updateTimestamp, long timestamp)
            throws VersionException, IOException {
        final File file = checkTimestamp(key, timestamp);
        createFile(file);
        return new FileOutputStream(file, true){
            @Override
            public void close() throws IOException{
                super.close();
                setTimestamp(file, updateTimestamp);
            }
        };
    }

    @Override
    public byte[] getByteArray(String key) throws IOException {
        File file=getFile(key);
        if(file.exists()){
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            transfer(new FileInputStream(file), buf, true);
            return buf.toByteArray();
        }else{
            if(throwOnKeyNotFound){
                throw new KeyNotFoundException(key);
            }else{
                return null;
            }
        }
    }

    @Override
    public void setByteArray(String key, byte[] value, long updateTimestamp,
            long timestamp) throws VersionException, IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(value);
        set(key, in, updateTimestamp, timestamp);
    }

    @Override
    public void appendByteArray(String key, byte[] value, long updateTimestamp,
            long timestamp) throws VersionException, IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(value);
        OutputStream out =null;
        try{
            out=append(key, updateTimestamp, timestamp);
            transfer(in, out);
        }finally{
            if(out!=null){
                out.close();
            }
        }
    }

    @Override
    public boolean exists(String key) throws IOException {
        File file=getFile(key);
        return file.exists();
    }

    @Override
    public void delete(String key, long timestamp) throws VersionException,
            IOException {
        File file = checkTimestamp(key, timestamp);
        if(file.exists()){
            if(!file.delete()){
                throw new IOException("删除文件失败");
            }            
        }
    }

    @Override
    public void close() throws IOException {
                
    }

    @Override
    public long currentTimestamp() {
        long t = System.currentTimeMillis()/1000;
        if(t%2==1){
            ++t;
        }
        
        return t;
    }
    
}
