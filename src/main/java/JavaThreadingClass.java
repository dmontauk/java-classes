import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

  public static void main(String[] args) throws Exception {
    Lesson1_Threads_Part1();
    System.out.println("Done!");
  }

  /**
   * Basic Java threading. Learn how to create a thread the old-school way.
   */
  private static void Lesson1_Threads_Part1() {
    // Calculate primes on one CPU
    PrimeCalculator calc = new PrimeCalculator(100000);
    Thread n = new Thread(calc);
    n.start();
  }

  private static void Lesson1_Threads_Part2() {
    // Now do it on multiple CPUs
    for (int i = 0; i < 4; i++) {
      PrimeCalculator partCalc = new PrimeCalculator(100000);
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
      executor.execute(new PrimeCalculator(10000));
    }
  }

  /**
   * The one where the program refuses to die.
   *
   * Question: why does this program refuse to die?
   */
  private static void Lesson2_Executors_Part3() {
    Executor executor = Executors.newFixedThreadPool(2);
    executor.execute(new PrimeCalculator(10000));
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
      executor.execute(new PrimeCalculator(10000));
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
      executor.execute(new PrimeCalculator(10000));
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
      executor.execute(new PrimeCalculator(10000));
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
    for (int i = 0; i < 100; i++) {
      executor.execute(new PrimeCalculator(10000));
    }
    executor.shutdown();

    // How does work happen when Futures are executed? What thread do they run in?
    // What happens when you combine two futures? Many futures?
    // What happens when you call .get() on a future?
    // How do you handle exceptions in Futures?
    //
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
