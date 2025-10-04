package javacore.task3;

import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Factory extends Thread {

  private final List<Robot> repository;
  Random rand = new Random();
  CyclicBarrier barrier;

  public Factory(List<Robot> repository, CyclicBarrier barrier) {
    this.repository = repository;
    this.barrier = barrier;
  }

  @Override
  public void run() {

    for (int i = 1; i <= 100; i++) {

      synchronized (repository) {
        int details = rand.nextInt(10) + 1;
        for (int j = 0; j < details; j++) {
          Robot robot = Robot.values()[rand.nextInt(Robot.values().length)];
          repository.add(robot);
        }

        if (i % 5 == 0) {
          System.out.printf(
              "Day %d: Factory produced %d details. There are %d details in the factory's storage%n",
              i, details, repository.size());
        }
      }

      try {
        barrier.await();
      } catch (InterruptedException | BrokenBarrierException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
