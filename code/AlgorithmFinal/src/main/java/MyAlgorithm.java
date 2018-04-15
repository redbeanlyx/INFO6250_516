import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyAlgorithm {

    public DNA initDNA() {
        DNA dna = new DNA();
        for (int j = 0; j < MyConstant.itemNum; j++) {
            dna.sequence[j] = MyRandom.randomBoolean();
        }
        dna.generation = 1;
        return dna;
    }

    public void correctionDNA(DNA dna) {
        dna.calc();
        while (dna.weight > MyConstant.weightMax) {
            int index = MyRandom.randomInt(0, MyConstant.itemNum - 1);
            if (dna.sequence[index]) {
                dna.sequence[index] = false;
            }
            dna.calc();
        }
    }

    public DNA copyDNA(DNA dna) {
        DNA temp = new DNA();
        for (int i = 0; i < MyConstant.itemNum; i++) {
            temp.sequence[i] = dna.sequence[i];
        }
        temp.generation = dna.generation;
        temp.weight = dna.weight;
        temp.value = dna.value;
        return temp;
    }

    public DNA[] initPopulation() {
        DNA[] DNAList = new DNA[MyParameter.populationNum];
        for (int i = 0; i < DNAList.length; i++) {
            DNAList[i] = initDNA();
            correctionDNA(DNAList[i]);
        }
        return DNAList;
    }

    public void select(DNA[] DNAList) {
        Arrays.sort(DNAList, (dna1, dna2) -> dna2.value - dna1.value);
    }

    public DNA[] crossOver(DNA dna1, DNA dna2) {
        DNA[] result = new DNA[MyParameter.fecundity];
        for (int i = 0; i < MyParameter.fecundity - 1; i = i + 2) {
            result[i] = new DNA();
            result[i + 1] = new DNA();
            int random = MyRandom.randomInt(1, MyConstant.itemNum - 1);
            for (int j = 0; j < random; j++) {
                result[i].sequence[j] = dna1.sequence[j];
                result[i + 1].sequence[j] = dna2.sequence[j];
            }
            for (int j = random; j < MyConstant.itemNum; j++) {
                result[i].sequence[j] = dna2.sequence[j];
                result[i + 1].sequence[j] = dna1.sequence[j];
            }
        }
        if (MyParameter.fecundity % 2 == 1) {
            result[result.length - 1] = new DNA();
            int random = MyRandom.randomInt(1, MyConstant.itemNum - 1);
            for (int j = 0; j < random; j++) {
                result[result.length - 1].sequence[j] = dna1.sequence[j];
            }
            for (int j = random; j < MyConstant.itemNum; j++) {
                result[result.length - 1].sequence[j] = dna2.sequence[j];
            }
        }
        return result;
    }

    public void mutation(DNA dna) {
        int index = MyRandom.randomInt(0, MyConstant.itemNum - 1);
        if (dna.sequence[index]) {
            dna.sequence[index] = false;
        } else {
            dna.sequence[index] = true;
        }
    }

    private class MyLock {
        public int index;
    }

    private class Evolve implements Runnable {
        private DNA dna1;
        private DNA dna2;
        private DNA[] DNAList;
        private MyLock myLock;

        private Evolve(DNA dna1, DNA dna2, DNA[] DNAList, MyLock myLock) {
            this.dna1 = dna1;
            this.dna2 = dna2;
            this.DNAList = DNAList;
            this.myLock = myLock;
        }

        @Override
        public void run() {
            DNA[] result;
            if (dna1.generation >= MyParameter.maturity && dna2.generation >= MyParameter.maturity && Math.random() < MyParameter.crossOverPro) {
                result = crossOver(dna1, dna2);
            } else {
                result = new DNA[2];
                result[0] = copyDNA(dna1);
                result[1] = copyDNA(dna2);
            }
            for (int i = 0; i < result.length; i++) {

                if (Math.random() < MyParameter.mutationPro) {
                    mutation(result[i]);
                }
                correctionDNA(result[i]);
                result[i].generation++;
            }
            synchronized (myLock) {
                for (int i = 0; i < result.length; i++) {
                    if (myLock.index >= MyParameter.populationNum) {
                        break;
                    }
                    DNAList[myLock.index] = result[i];
                    myLock.index++;
                }
            }
        }
    }

    public DNA[] parallelEvolve(DNA[] oldList) {
        DNA[] newList = new DNA[MyParameter.populationNum];
        ExecutorService threadPool = Executors.newFixedThreadPool(MyConstant.coreNum);
        MyLock myLock = new MyLock();
        myLock.index = 1;
        threadPool.execute(() -> {
            newList[0] = copyDNA(oldList[0]);
            newList[0].generation++;
        });
        for (int i = 1; i < MyParameter.populationNum && myLock.index < MyParameter.populationNum; i++) {
            int[] twoNums = MyRandom.randomIntNums(0, (int) (MyParameter.populationNum * MyParameter.survivePro) - 1, 2);
            threadPool.execute(new Evolve(oldList[twoNums[0]], oldList[twoNums[1]], newList, myLock));
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return newList;
    }

    public int geneticAlgorithm() {
        File file = new File("result.csv");
        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        DNA[] oldDNAList = initPopulation();
        select(oldDNAList);
        for (int i = 0; i < MyParameter.generationNum; i++) {
            DNA[] newDNAList = parallelEvolve(oldDNAList);
            select(newDNAList);
            oldDNAList = newDNAList;
            try {
                bw.write(oldDNAList[0].value+"");
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return oldDNAList[0].value;
    }
}
