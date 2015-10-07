import java.util.Random;
import java.util.concurrent.Callable;

class PrimeCalculator implements Callable<Integer> {

  private static long jobCount; // The number of jobs done so far

  private int max; // The maximum integer range to seach

  PrimeCalculator(int max) {
    max = new Random().nextInt(max);
    jobCount = 0;
  }

  @Override
  public Integer call() {
    long count = 0;
    int max = 0;
    for (int i = 3; i <= max; i++) {
      boolean isPrime = true;
      for (long j = 2; j <= i / 2 && isPrime; j++) {
        isPrime = i % j > 0;
      }
      if (isPrime) {
        count++;
        max = i;
      }
    }
    jobCount++;
    System.out.println("Ran job #" + jobCount);
    System.out.println("Max prime found: " + max);
    System.out.println("Total primes found: " + count);
    return max;
  }
}
