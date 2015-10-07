import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Demonstrate how threading works in Java 8. Help teach people about
 *
 * Topics that should be covered in this class: -Jumping between threads. Puts things back onto the
 * queue of the next executor; this can cause unexpected latency. -CompletableFuture.complete(). How
 * Futures run in threads in general. -IO bound vs CPU-bound tasks. NIO in Java. -Executors vs using
 * the Java 8 ForkJoinPool -Synchronization primitives, aka barriers (cyclic vs non-cyclic?),
 * latches, etc.
 */
public class JavaThreadingClass {

  public static final int MAX_PRIME = 10000;

  public static void main(String[] args) throws Exception {
    Lesson1_Threads_Part2();
    System.out.println("Done!");
  }

  /**
   * Basic Java threading. Learn how to create a thread the old-school way.
   */
  private static void Lesson1_Threads_Part1() {
    // Calculate primes on one CPU
    RunnablePrimeCalculator calc = new RunnablePrimeCalculator(100000);
    Thread n = new Thread(calc);
    n.start();
  }

  private static void Lesson1_Threads_Part2() {
    // Now do it on multiple CPUs
    for (int i = 0; i < 4; i++) {
      RunnablePrimeCalculator partCalc = new RunnablePrimeCalculator(100000);
      new Thread(partCalc).start();
    }
  }

  private static void Lesson2_Executors_Part1() {
  }

  /**
   * The one that kills your computer.
   *
   * Questions for this lesson: 1. How many threads were created? 2. How much control did you have
   * over resource usage?
   */
  private static void Lesson2_Executors_Part2() {
    Executor executor = Executors.newCachedThreadPool();
    for (int i = 0; i < 100000; i++) {
      executor.execute(new RunnablePrimeCalculator(MAX_PRIME));
    }
  }

  /**
   * The one where the program refuses to die.
   *
   * Question: why does this program refuse to die?
   */
  private static void Lesson2_Executors_Part3() {
    Executor executor = Executors.newFixedThreadPool(2);
    executor.execute(new RunnablePrimeCalculator(MAX_PRIME));
  }

  /**
   * The one in which the program dies too early.
   *
   * Question: why does this program die before calculating anything?
   */
  private static void Lesson2_Executors_Part4() {
    Executor executor = Executors.newFixedThreadPool(
        2, new ThreadFactoryBuilder()
            .setDaemon(true)
            .build());
    for (int i = 0; i < 100000; i++) {
      executor.execute(new RunnablePrimeCalculator(MAX_PRIME));
    }
  }

  /**
   * The one which finally works properly.
   */
  private static void Lesson2_Executors_Part5() {
    ExecutorService executor = Executors.newFixedThreadPool(
        2, new ThreadFactoryBuilder()
            .setDaemon(false)
            .build());
    for (int i = 0; i < 100; i++) {
      executor.execute(new RunnablePrimeCalculator(MAX_PRIME));
    }
    executor.shutdown();
  }

  /**
   * The one in which the threads have nice names.
   *
   * Questions: why do nice thread names matter?
   */
  private static void Lesson2_Executors_Part6() {
    ExecutorService executor = Executors.newFixedThreadPool(
        2, new ThreadFactoryBuilder()
            .setNameFormat("my-fun-threads-%s")
            .setDaemon(false)
            .build());
    for (int i = 0; i < 100; i++) {
      executor.execute(new RunnablePrimeCalculator(MAX_PRIME));
    }
    executor.shutdown();
  }

  /**
   * The one where we learn what Futures are.
   */
  private static void Lesson3_Futures_Part1() {
    ExecutorService executor = Executors.newFixedThreadPool(
        2, new ThreadFactoryBuilder()
            .setNameFormat("my-fun-threads-%s")
            .setDaemon(false)
            .build());
    Future<Integer> result = executor.submit(new PrimeCalculator(MAX_PRIME));
    try {
      result.get();
      // Question: when is an InterruptedException thrown?
    } catch (InterruptedException e) {
      e.printStackTrace();
      // Question: when is an ExecutionException thrown?
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    executor.shutdown();
  }

  /**
   * The one where a bunch of Futures are generated.
   */
  private static void Lesson3_Futures_Part2() {
    ExecutorService executor = Executors.newFixedThreadPool(
        2, new ThreadFactoryBuilder()
            .setNameFormat("my-fun-threads-%s")
            .setDaemon(false)
            .build());
    List<Future<Integer>> results = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      results.add(executor.submit(new PrimeCalculator(MAX_PRIME)));
    }
    try {
      int biggestPrime = 0;
      // What's the problem with this approach?
      for (Future<Integer> result : results) {
        final Integer prime = result.get();
        if (prime > biggestPrime) {
          biggestPrime = prime;
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    executor.shutdown();
  }

  /**
   * The one in which we learn about synchronization primitives.
   *
   * Questions: -What's a CyclicBarrier? What othere types of barriers are there? What are the
   * benefits/costs of each type? -What's a latch?
   */
  private static void Lesson3_Futures_Part3() {
  }

  /**
   * The one where all the complexity disappears. Almost.
   */
  private static void Lesson4_CompletableFuture_Part1() throws Exception {
    CompletableFuture<Integer>
        future1 =
        CompletableFuture.supplyAsync(() -> new PrimeCalculator(MAX_PRIME).call());
    CompletableFuture<Integer>
        future2 =
        CompletableFuture.supplyAsync(() -> new PrimeCalculator(MAX_PRIME).call());
    CompletableFuture<Integer> largestPrime = future1.thenCombine(future2, Math::max);
    System.out.println(largestPrime.get());
  }

  /**
   * The one in which we learn CompletableFuture sucks.
   *
   * Questions: -When does the calculation actually start happening in the code below? How does work
   * happen when Futures are executed? What thread do they run in? What happens when you combine two
   * futures? Many futures? What happens when you call .get() on a future? How do you handle
   * exceptions in Futures?
   */
  private static void Lesson4_CompletableFuture_Part2() throws Exception {
    List<CompletableFuture<Integer>> futures = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      futures.add(CompletableFuture.supplyAsync(() -> new PrimeCalculator(MAX_PRIME).call()));
    }
    final CompletableFuture<Integer> largestPrimeFuture = CompletableFuture
        .allOf((CompletableFuture<Integer>[]) futures.toArray())
        .thenApply(unused -> {
          int largestPrime = 0;
          for (CompletableFuture<Integer> f : futures) {
            try {
              int prime = f.get();
              if (prime > largestPrime) {
                largestPrime = prime;
              }
            } catch (InterruptedException e) {
              e.printStackTrace();
            } catch (ExecutionException e) {
              e.printStackTrace();
            }
          }
          return largestPrime;
        });
    System.out.println(largestPrimeFuture.get());
  }

  private static void LessonN_SynchronizationPrimitives() {
    // How do you do more complex synchronization across threads?
  }

  private static void LessonN_TimingOutFutures() {
    // What happens when you need to continue work despite some of your previous work not being done?
    // DeadlineCalculator pattern. Well-designed libraries should support deadlines for all async-work.
    // LESSON: time-outs should be handled below you whenever possible. Do not rely on timers!!
  }

  private static void LessonN_ForkJoinPool() {
    // What is the ForkJoinPool?
    // When should you use the ForkJoinPool vs your own?

  }

}
