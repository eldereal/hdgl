package hdgl.store;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class KeyBlobStoreFactory {
    
    private static HashMap<String,KeyBlobStoreImpl<?>> impls
        = new HashMap<String,KeyBlobStoreImpl<?>>();
    
    public static <TK> KeyBlobStoreImpl<TK> using(String impl, Class<TK> clazz){
        return using(impl);
    }
    
    public static <TK> KeyBlobStoreImpl<TK> using(String impl){
        if(impls.containsKey(impl)){
            @SuppressWarnings("unchecked") 
            KeyBlobStoreImpl<TK> i = (KeyBlobStoreImpl<TK>)impls.get(impl);
            return i;
        }        
        for (Package p : Package.getPackages()) {
            try {
                for (Class<?> c : getClasses(p.getName())) {
                    try {
                        if(c.getName().toLowerCase().endsWith(".impl."+impl+".impl")
                         ||c.getName().toLowerCase().endsWith(".impl."+impl+"."+impl+"keyblobstoreimpl")){
                            if(KeyBlobStoreImpl.class.isAssignableFrom(c)){
                                @SuppressWarnings("unchecked") 
                                KeyBlobStoreImpl<TK> i = (KeyBlobStoreImpl<TK>) c.newInstance();
                                impls.put(impl, i);
                                return i;
                            }
                        }
                    } catch (Exception e) {
                        
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Unsupported Implementation: "+impl);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unsupported Implementation: "+impl);
            }
        }
        throw new RuntimeException("Unsupported Implementation: "+impl);
    }
    
    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class<?>[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
    
}
