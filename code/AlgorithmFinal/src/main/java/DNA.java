public class DNA {
    public boolean[] sequence;
    public int weight;
    public int value;
    public int generation;

    public DNA() {
        sequence = new boolean[MyConstant.itemNum];
        generation = 0;
    }

    public void calc() {
        int weightResult = 0;
        int valueResult = 0;
        for (int i = 0; i < sequence.length; i++) {
            if (sequence[i]) {
                weightResult = weightResult + MyConstant.items[i].weight;
                valueResult = valueResult + MyConstant.items[i].value;
            }
        }
        weight = weightResult;
        value = valueResult;
    }
}
