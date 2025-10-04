package javacore.task3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

/**
 * There are two factions: World and Wednesday and neutral object - Factory.
 */
public class Main {

  public static void main(String[] args) {

    List<Robot> repository = new ArrayList<>();

    /**
     3 task:
     done - Factory produces days 1..99 (i < 100). Factions run 1..100. Off-by-one day mismatch
     done - "Factory produces by day, factions take at night.” Currently all threads run independently with sleeps; no strict day/night barrier. This can violate the sequence
     done -Faction uses random take = rand.nextInt(5)+1 which is ≤5, but requirement reads as a cap, not randomness. Consider fixed max take of up to 5 available parts each night.
     done - new Random() inside loop is redundant; use the field rand
     */
    CyclicBarrier barrier = new CyclicBarrier(3);

    Factory factory = new Factory(repository, barrier);
    Faction world = new Faction("World", repository, barrier);
    Faction wednesday = new Faction("Wednesday", repository, barrier);

    factory.start();
    world.start();
    wednesday.start();

    try {
      factory.join();
      world.join();
      wednesday.join();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    int worldRobots = world.getRobotsBuilt();
    int wednesdayRobots = wednesday.getRobotsBuilt();

    System.out.println("\nWorld have created " + worldRobots + " robots");
    System.out.println("\nWednesday have created " + wednesdayRobots + " robots");

    if (worldRobots > wednesdayRobots) {
      System.out.println("World has the strongest army,congratulations!");
    } else if (wednesdayRobots > worldRobots) {
      System.out.println("Wednesday has the strongest army, congratulations!");
    } else {
      System.out.println("It's a draw!");
    }
  }
}
