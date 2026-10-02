package com.frapee;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.javatuples.Pair;

/**
 * Foundation guide to Java concurrency and asynchronous execution.
 * Demonstrates platform threads, executor tasks, Future,
 * CompletableFuture composition, and coordination primitives such as latches,
 * atomics, locks, and semaphores.
 */
public class FoundationAsync {

    /**
     * Factorial function with simple loop that will be run a sync
     * @param number input number being calculated
     * @return factorial for the number
     */
    private long factorial(long number) {
        validateFactorialInput(number);
        long result = 1;
        for (long i = number; i > 0; i--) {
            result = Math.multiplyExact(result, i);
        }
        return result;
    }

    private void validateFactorialInput(long number) {
        if (number < 0 || number > 20) {
            throw new IllegalArgumentException("Factorial input must be between 0 and 20");
        }
    }

    /**
     * Running async as simple thread
     * @param number to process factorial of
     * @return
     */
    public long useThread(int number) {
        validateFactorialInput(number);
        ThreadReturnable factorialThread = new ThreadReturnable();
        factorialThread.setNumber(number);
        Thread newThread = new Thread(factorialThread);
        newThread.start();
        try {
            newThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for factorial thread", e);
        }
        return factorialThread.getValue();
    }

    /**
     * Running async as CompletableFuture
     * @param number to process factorial of
     * @return Pair tuple containing zero polling iterations and result
     */
    public Pair<Long, Long> useCompletable(int number) {
        validateFactorialInput(number);
        CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(() -> factorial(number));
        try {
            return new Pair<Long, Long>(0L, completableFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException("Unable to complete factorial", e);
        }
    }

    /**
     * Running async as a Task from a thread pool
     * @param number to process factorial of
     * @return Pair tuple containing zero polling iterations and result
     */
    public Pair<Long, Long> useTask(int number) {
        validateFactorialInput(number);
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            Future<Long> futureTask = executor.submit(() -> factorial(number));
            try {
                return new Pair<Long, Long>(0L, futureTask.get());
            } catch (InterruptedException | ExecutionException e) {
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                throw new IllegalStateException("Unable to complete factorial task", e);
            }
        } finally {
            executor.shutdown();
        }
    }

    /**
     * CompletableFuture pipeline example using thenApply to transform a value asynchronously.
     * @param number to process factorial of
     * @return factorial result
     */
    public long useCompletablePipeline(int number) {
        validateFactorialInput(number);
        return CompletableFuture.supplyAsync(() -> (long) number)
            .thenApply(this::factorial)
            .join();
    }

    /**
     * CompletableFuture composition example using thenCombine.
     * @param left first factorial input
     * @param right second factorial input
     * @return sum of both factorial results
     */
    public long useCompletableCombine(int left, int right) {
        validateFactorialInput(left);
        validateFactorialInput(right);
        CompletableFuture<Long> leftFuture = CompletableFuture.supplyAsync(() -> factorial(left));
        CompletableFuture<Long> rightFuture = CompletableFuture.supplyAsync(() -> factorial(right));
        return leftFuture.thenCombine(rightFuture, Long::sum).join();
    }

    /**
     * CompletableFuture coordination example using allOf to await multiple tasks.
     * @param numbers factorial inputs
     * @return factorial results in input order
     */
    public long[] useCompletableAll(int[] numbers) {
        for (int number : numbers) {
            validateFactorialInput(number);
        }
        @SuppressWarnings("unchecked")
        CompletableFuture<Long>[] futures = new CompletableFuture[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            int number = numbers[i];
            futures[i] = CompletableFuture.supplyAsync(() -> factorial(number));
        }
        CompletableFuture.allOf(futures).join();
        long[] results = new long[numbers.length];
        for (int i = 0; i < futures.length; i++) {
            results[i] = futures[i].join();
        }
        return results;
    }

    /**
     * CountDownLatch example where the caller waits for all submitted tasks.
     * @param taskCount number of tasks to complete
     * @return number of tasks that completed
     */
    public int useCountDownLatch(int taskCount) {
        if (taskCount < 0) {
            throw new IllegalArgumentException("Task count must be non-negative");
        }
        CountDownLatch latch = new CountDownLatch(taskCount);
        ExecutorService executor = Executors.newFixedThreadPool(Math.max(1, taskCount));
        try {
            for (int i = 0; i < taskCount; i++) {
                executor.submit(latch::countDown);
            }
            awaitLatch(latch);
        } finally {
            executor.shutdown();
        }
        return taskCount;
    }

    /**
     * AtomicInteger example for safely updating shared state across tasks.
     * @param taskCount number of concurrent tasks
     * @param incrementsPerTask increments performed by each task
     * @return final counter value
     */
    public int useAtomicCounter(int taskCount, int incrementsPerTask) {
        validateCounterInputs(taskCount, incrementsPerTask);
        AtomicInteger counter = new AtomicInteger();
        ExecutorService executor = Executors.newFixedThreadPool(Math.max(1, taskCount));
        try {
            Future<?>[] tasks = new Future<?>[taskCount];
            for (int i = 0; i < taskCount; i++) {
                tasks[i] = executor.submit(() -> {
                    for (int increment = 0; increment < incrementsPerTask; increment++) {
                        counter.incrementAndGet();
                    }
                });
            }
            waitForTasks(tasks);
        } finally {
            executor.shutdown();
        }
        return counter.get();
    }

    /**
     * ReentrantLock example for protecting a shared counter.
     * @param taskCount number of concurrent tasks
     * @param incrementsPerTask increments performed by each task
     * @return final counter value
     */
    public int useReentrantLock(int taskCount, int incrementsPerTask) {
        validateCounterInputs(taskCount, incrementsPerTask);
        ReentrantLock lock = new ReentrantLock();
        int[] counter = {0};
        ExecutorService executor = Executors.newFixedThreadPool(Math.max(1, taskCount));
        try {
            Future<?>[] tasks = new Future<?>[taskCount];
            for (int i = 0; i < taskCount; i++) {
                tasks[i] = executor.submit(() -> {
                    for (int increment = 0; increment < incrementsPerTask; increment++) {
                        lock.lock();
                        try {
                            counter[0]++;
                        } finally {
                            lock.unlock();
                        }
                    }
                });
            }
            waitForTasks(tasks);
        } finally {
            executor.shutdown();
        }
        return counter[0];
    }

    /**
     * Semaphore example for limiting the number of tasks in a critical section.
     * @param taskCount number of concurrent tasks
     * @param permits maximum number of concurrent holders
     * @return number of tasks that completed
     */
    public int useSemaphore(int taskCount, int permits) {
        if (taskCount < 0 || permits <= 0) {
            throw new IllegalArgumentException("Task count must be non-negative and permits must be positive");
        }
        Semaphore semaphore = new Semaphore(permits);
        ExecutorService executor = Executors.newFixedThreadPool(Math.max(1, taskCount));
        try {
            Future<?>[] tasks = new Future<?>[taskCount];
            for (int i = 0; i < taskCount; i++) {
                tasks[i] = executor.submit(() -> {
                    try {
                        semaphore.acquire();
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("Interrupted while acquiring semaphore", exception);
                    }
                    try {
                        // Work would be performed while holding one permit.
                    } finally {
                        semaphore.release();
                    }
                });
            }
            waitForTasks(tasks);
        } finally {
            executor.shutdown();
        }
        return taskCount;
    }

    private void validateCounterInputs(int taskCount, int incrementsPerTask) {
        if (taskCount < 0 || incrementsPerTask < 0) {
            throw new IllegalArgumentException("Task count and increments must be non-negative");
        }
    }

    private void awaitLatch(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for tasks", exception);
        }
    }

    private void waitForTasks(Future<?>[] tasks) {
        try {
            for (Future<?> task : tasks) {
                task.get();
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for tasks", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("A task failed", exception.getCause());
        }
    }

}
