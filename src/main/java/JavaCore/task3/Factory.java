package JavaCore.task3;

import java.util.List;
import java.util.Random;

public class Factory extends Thread {
    private final List<Robot> repository;
    Random rand = new Random();
    public Factory(List<Robot> repository) {
        this.repository = repository;
    }

    @Override
    public void run() {
        for (int i = 1; i < 100; i++) {
            synchronized (repository){
                int details = new Random().nextInt(10)+1;
                for (int j = 0; j < details; j++) {
                    Robot robot = Robot.values()[rand.nextInt(Robot.values().length)];
                    repository.add(robot);
                }
                if (i%5==0){
                    System.out.printf("Day %d: Factory produced %d details. There are %d details in the factory's storage%n", i, details, repository.size());
                }
            }
            try{
                Thread.sleep(40);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
