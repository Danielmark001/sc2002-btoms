package util;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * Comprehensive Concurrency Utility
 * Provides thread-safe operations and advanced concurrency management
 */
public final class ConcurrencyUtil {
    // Thread pool for asynchronous operations
    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors(),
        new ThreadFactory() {
            private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
            
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = defaultFactory.newThread(r);
                
                return thread;
            }
        }
    );

    // Timeout for operations
    private static final long DEFAULT_TIMEOUT = 30;
    private static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    // Private constructor to prevent instantiation
    private ConcurrencyUtil() {
        throw new AssertionError("Cannot be instantiated");
    }

    /**
     * Execute a task asynchronously
     * @param task Task to execute
     * @return CompletableFuture representing the task
     */
    public static CompletableFuture<Void> runAsync(Runnable task) {
        return CompletableFuture.runAsync(() -> {
            try {
                task.run();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, THREAD_POOL);
    }

    /**
     * Execute a task with a return value asynchronously
     * @param task Task to execute
     * @param <T> Return type
     * @return CompletableFuture with the task result
     */
    public static <T> CompletableFuture<T> supplyAsync(Supplier<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.get();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, THREAD_POOL);
    }

    /**
     * Execute a task with timeout
     * @param task Task to execute
     * @param timeout Timeout duration
     * @param unit Timeout time unit
     * @param <T> Return type
     * @return Result of the task
     * @throws TimeoutException if task exceeds timeout
     * @throws ExecutionException if task throws an exception
     * @throws InterruptedException if task is interrupted
     */
    public static <T> T executeWithTimeout(
        Callable<T> task, 
        long timeout, 
        TimeUnit unit
    ) throws TimeoutException, ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<T> future = executor.submit(task);
        
        try {
            return future.get(timeout, unit);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw e;
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * Execute a task with default timeout
     * @param task Task to execute
     * @param <T> Return type
     * @return Result of the task
     * @throws Exception if task fails or times out
     */
    public static <T> T executeWithDefaultTimeout(Callable<T> task) throws Exception {
        return executeWithTimeout(task, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT);
    }

    /**
     * Create a thread-safe singleton instance
     * @param <T> Type of singleton
     */
    public static class ThreadSafeSingleton<T> {
        private volatile T instance;
        private final Supplier<T> instanceCreator;

        public ThreadSafeSingleton(Supplier<T> creator) {
            this.instanceCreator = creator;
        }

        /**
         * Get or create singleton instance
         * @return Singleton instance
         */
        public T getInstance() {
            // Double-checked locking pattern
            if (instance == null) {
                synchronized (this) {
                    if (instance == null) {
                        instance = instanceCreator.get();
                    }
                }
            }
            return instance;
        }
    }

    /**
     * Create a thread-safe cache
     * @param <K> Key type
     * @param <V> Value type
     */
    public static class ThreadSafeCache<K, V> {
        private final ConcurrentHashMap<K, V> cache;
        private final ConcurrentHashMap<K, CompletableFuture<V>> computingCache;

        public ThreadSafeCache() {
            this.cache = new ConcurrentHashMap<>();
            this.computingCache = new ConcurrentHashMap<>();
        }

        /**
         * Get value from cache, computing if not present
         * @param key Cache key
         * @param valueComputer Function to compute value if not in cache
         * @return Cached or computed value
         */
        public V get(K key, Supplier<V> valueComputer) {
            // Check if value is already in cache
            V existingValue = cache.get(key);
            if (existingValue != null) {
                return existingValue;
            }

            // Use computeIfAbsent with thread-safe computation
            return cache.computeIfAbsent(key, k -> {
                try {
                    return valueComputer.get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        /**
         * Remove a value from cache
         * @param key Key to remove
         */
        public void remove(K key) {
            cache.remove(key);
        }

        /**
         * Clear entire cache
         */
        public void clear() {
            cache.clear();
        }
    }

    /**
     * Shutdown the thread pool
     * Should be called when application is closing
     */
    public static void shutdown() {
        THREAD_POOL.shutdown();
        try {
            if (!THREAD_POOL.awaitTermination(5, TimeUnit.SECONDS)) {
                THREAD_POOL.shutdownNow();
            }
        } catch (InterruptedException e) {
            THREAD_POOL.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // Shutdown hook to ensure thread pool closes
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(ConcurrencyUtil::shutdown));
    }
}