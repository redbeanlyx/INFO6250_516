import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MyTest {
    private static MyAlgorithm myAlgorithm;

    @BeforeClass
    public static void setMyAlgorithm() {
        myAlgorithm = new MyAlgorithm();
    }

    @Test
    public void testRandomInt() {
        for (int i = 0; i < 10000; i++) {
            int start = 1;
            int end = 10;
            int result = MyRandom.randomInt(start, end);
            assertTrue(result >= start);
            assertTrue(result <= end);
        }
    }

    @Test
    public void testRandomIntNums() {
        for (int i = 0; i < 100; i++) {
            int random = MyRandom.randomInt(1, 10);
            int[] nums = MyRandom.randomIntNums(1, random, random);
            for (int j = 0; j < nums.length; j++) {
                for (int k = j + 1; k < nums.length; k++) {
                    assertTrue(nums[j] != nums[k]);
                }
            }
        }
    }

    @Test
    public void testInitDNA() {
        DNA dna = myAlgorithm.initDNA();
        assertTrue(dna.sequence != null);
    }

    @Test
    public void testCorrectionDNA() {
        for (int i = 0; i < MyParameter.populationNum; i++) {
            DNA dna = myAlgorithm.initDNA();
            myAlgorithm.correctionDNA(dna);
            assertTrue(dna.weight <= MyConstant.weightMax);
        }
    }

    @Test
    public void testCopyDNA() {
        DNA oldDNA = myAlgorithm.initDNA();
        DNA newDNA = myAlgorithm.copyDNA(oldDNA);
        for (int i = 0; i < MyConstant.itemNum; i++) {
            assertEquals(oldDNA.sequence[i], newDNA.sequence[i]);
        }
        assertEquals(oldDNA.generation, newDNA.generation);
        assertEquals(oldDNA.weight, newDNA.weight);
        assertEquals(oldDNA.value, newDNA.value);
        newDNA.generation++;
        assertTrue(oldDNA.generation != newDNA.generation);
    }

    @Test
    public void testInitPopulation() {
        DNA[] DNAList = myAlgorithm.initPopulation();
        for (int i = 0; i < MyParameter.populationNum; i++) {
            assertTrue(DNAList[i].weight <= MyConstant.weightMax);
        }
    }

    @Test
    public void testSelect() {
        DNA[] DNAList = myAlgorithm.initPopulation();
        myAlgorithm.select(DNAList);
        for (int i = 0; i < MyParameter.populationNum - 1; i++) {
            assertTrue(DNAList[i].value >= DNAList[i + 1].value);
        }
    }

    @Test
    public void testCrossOver() {
        DNA dna1 = myAlgorithm.initDNA();
        DNA dna2 = myAlgorithm.initDNA();
        DNA[] result = myAlgorithm.crossOver(dna1, dna2);
        for (int k = 0; k < result.length - 1; k = k + 2) {
            for (int i = 0; i < MyConstant.itemNum; i++) {
                if (dna1.sequence[i] != result[k].sequence[i]) {
                    for (int j = 0; j < i; j++) {
                        assertEquals(dna1.sequence[j], result[k].sequence[j]);
                        assertEquals(dna2.sequence[j], result[k + 1].sequence[j]);
                    }
                    for (int j = i; j < MyConstant.itemNum; j++) {
                        assertEquals(dna2.sequence[j], result[k].sequence[j]);
                        assertEquals(dna1.sequence[j], result[k + 1].sequence[j]);
                    }
                    break;
                }
            }
        }
        if (MyParameter.fecundity % 2 == 1) {
            for (int i = 0; i < MyConstant.itemNum; i++) {
                if (dna1.sequence[i] != result[result.length - 1].sequence[i]) {
                    for (int j = 0; j < i; j++) {
                        assertEquals(dna1.sequence[j], result[result.length - 1].sequence[j]);
                    }
                    for (int j = i; j < MyConstant.itemNum; j++) {
                        assertEquals(dna2.sequence[j], result[result.length - 1].sequence[j]);
                    }
                    break;
                }
            }
        }
    }

    @Test
    public void testMutation() {
        DNA dna = myAlgorithm.initDNA();
        DNA oldDNA = myAlgorithm.copyDNA(dna);
        myAlgorithm.mutation(dna);
        int count = 0;
        for (int i = 0; i < MyConstant.itemNum; i++) {
            if (dna.sequence[i] != oldDNA.sequence[i]) {
                count++;
            }
        }
        assertEquals(1, count);
    }

    @Test
    public void testParallelEvolve() {
        DNA[] oldList = myAlgorithm.initPopulation();
        myAlgorithm.select(oldList);
        DNA[] newList = myAlgorithm.parallelEvolve(oldList);
        for (int i = 0; i < MyParameter.populationNum; i++) {
            assertTrue(newList[i] != null);
        }
    }

    @Test
    public void testGeneticAlgorithm() {
        int result = myAlgorithm.geneticAlgorithm();
        assertTrue(result <= MyConstant.weightMax * 10);
        System.out.println("Max: " + MyConstant.weightMax * 10 + " Result: " + result);
    }
}
