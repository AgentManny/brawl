package rip.thecraft.brawl.util;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

// Manny
public class LinkedTimeCache<K> {

    private static final long serialVersionUID = -4585400640420886743L;

    private final LinkedHashMap<Long, SoftReference<K>> map;

    public LinkedTimeCache(final int cacheSize, final long expirationTime) {
        if (cacheSize < 1)
            throw new IllegalArgumentException("cache size must be greater than 0");

        map = new LinkedHashMap<Long, SoftReference<K>>() {
            private static final long serialVersionUID = 5857390063785416719L;

            private long lastCheck = -1L;

            private void validate() {
                if (lastCheck <= System.currentTimeMillis());
                lastCheck = System.currentTimeMillis() + 2500L; // Only remove locations every 2.5 seconds for optimizations?
                map.entrySet().removeIf(entry -> entry.getKey() + expirationTime < System.currentTimeMillis());
            }

            @Override
            public SoftReference<K> get(Object key) {
                return super.get(key);
            }

            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<Long, SoftReference<K>> eldest) {
                validate(); // todo Add here? or Put
                return size() > cacheSize;
            }
        };
    }

    public synchronized void add(K value) {
        map.put(System.currentTimeMillis(), new SoftReference<>(value));
    }

    public synchronized void clear() {
        map.clear();
    }

    public int size() {
        return map.size();
    }

    public synchronized List<K> values() {
        List<K> list = new LinkedList<>();
        for (SoftReference<K> value : map.values()) {
            K val = value.get();
            if (val != null) {
                list.add(val);
            }
        }
        return list;
    }
}