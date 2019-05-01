package nl.knmi.adaguc.services.esgfsearch.search.cache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Calendar;
import java.util.function.Function;
import java.util.function.Supplier;

import nl.knmi.adaguc.tools.Tools;
import nl.knmi.adaguc.tools.Debug;

public class DiskCache implements ICache<String> {

    private final String cacheLocation;
    private final int maxAgeInSeconds;

    /**
     * @param diskCacheLocation Location of cache on the disk
     * @param maxAgeInSeconds   Maximum allowed age for cached items
     */
    public DiskCache(String diskCacheLocation, int maxAgeInSeconds) {
        this.cacheLocation = diskCacheLocation;
        this.maxAgeInSeconds = maxAgeInSeconds;
    }

    /**
     * @param id Id to sanitize
     *
     * @return Sanitized id
     */
    private String sanitizeId(String id) {
        final String[] illegalChars = {"\\?", "&", ":", "/", "="};
        final String replacementChar = "_";

        for (String illegalChar : illegalChars) {
            id = id.replaceAll(illegalChar, replacementChar);
        }
        return id;
    }

    /**
     * Returns the stored message, null if not available or too old
     *
     * @param identifier Unique id for the cached message
     *
     * @return
     */
    public String get(String identifier) {
        identifier = sanitizeId(identifier);

        String filePath = cacheLocation + "/" + identifier;

        Function<String, CacheItem<String>> createCacheObject = (string) -> CacheItem.createItem(maxAgeInSeconds, string);
        Supplier<CacheItem<String>> createFileCacheObject = () -> {
            try {
                return createCacheObject.apply(Tools.readFile(filePath));
            } catch (IOException ignored) {}
            return null;
        };//FIXME

        if (maxAgeInSeconds == 0) return createFileCacheObject.get().getItem();

        Path fileCacheId = new File(filePath).toPath();

        BasicFileAttributes attributes;
        try {
            attributes = Files.readAttributes(fileCacheId, BasicFileAttributes.class);
        } catch (IOException e) {
            return null;
        }
        FileTime creationTime = attributes.creationTime();
        long ageInSeconds = (Calendar.getInstance().getTimeInMillis() - creationTime.toMillis()) / 1000;

        if (ageInSeconds <= maxAgeInSeconds) {
            return createFileCacheObject.get().getItem();
        }

        Tools.rm(filePath);
        return null;
    }

    /**
     * Store a string in the diskcache system identified with an id
     *
     * @param data       The data to store
     * @param identifier The identifier of this string
     */
    public void set(String identifier, String data) {
        identifier = sanitizeId(identifier);

        try {
            try {
                Tools.mksubdirs(cacheLocation);
                Tools.writeFile(cacheLocation + "/" + identifier, data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Debug.errprintln("Unable to write to cachelocation " + cacheLocation + " with identifier " + identifier);
        }
    }


}
