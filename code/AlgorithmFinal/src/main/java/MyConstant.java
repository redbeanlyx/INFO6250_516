public class MyConstant {
    public static final int itemNum = 2000;
    public static final int weightMax = 10000;
    public static final Item[] items;
    public static final int coreNum = 2;

    static {
        items = new Item[itemNum];
        for (int i = 0; i < items.length; i++) {
            int weight = MyRandom.randomInt(1, 20);
            items[i] = new Item(weight * MyRandom.randomInt(1, 10), weight);
        }
    }
}
