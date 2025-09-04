package JavaCore.task3;

import lombok.Getter;

import java.util.List;
import java.util.Random;

import static JavaCore.task3.Robot.HAND;

public class Faction extends Thread{
    private final String name;
    private final List<Robot> repository;
    Random rand = new Random();
    private int heads = 0, torsos = 0, hands = 0, feet = 0;


    public Faction(String name, List<Robot> repository) {
        this.name = name;
        this.repository= repository;

    }
    @Override
    public void run() {
        for (int i = 1; i <= 100; i++) {
            int j = 0;
            int take = rand.nextInt(5)+1;
            synchronized (repository) {
                while (j < take && !repository.isEmpty()) {
                    int index = rand.nextInt(repository.size());
                    Robot part = repository.remove(index);
                    switch (part) {
                        case HEAD -> heads++;
                        case TORSO -> torsos++;
                        case HAND -> hands++;
                        case FEET -> feet++;
                    }
                    j++;
                }
                if (i%5==0){
                    System.out.printf("Night %d: %s took %d parts and also have [Heads=%d Torsos=%d Hands=%d Feet=%d] to create robots%n", i, name, j, heads, torsos, hands, feet);
                }

            }
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public int getRobotsBuilt() {
        return Math.min(Math.min(heads, torsos), Math.min(hands / 2, feet / 2));
    }


}
