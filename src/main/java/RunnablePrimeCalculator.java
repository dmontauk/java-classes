class RunnablePrimeCalculator implements Runnable {

  private final PrimeCalculator calc;

  public RunnablePrimeCalculator(int max) {
    calc = new PrimeCalculator(max);
  }

  @Override
  public void run() {
    calc.call();
  }
}
