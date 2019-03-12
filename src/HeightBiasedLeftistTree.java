import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HBLTNode {
    int sHeight;
    int fVal;
    int fSize;

    HBLTNode fRight;
    HBLTNode fLeft;
}

class HeightBiasedLeftistTree {
    private HBLTNode fHead;

    HeightBiasedLeftistTree(int val) {
        fHead = new HBLTNode();
        fHead.fVal = val;
        fHead.sHeight = 1;
        fHead.fSize = 1;
    }

    HeightBiasedLeftistTree() {
    }

    HeightBiasedLeftistTree(int[] arr) {
        if (arr.length == 0)
            return;

        LinkedList<HeightBiasedLeftistTree> result = new LinkedList<>();
        for (int i : arr) {
            result.add(new HeightBiasedLeftistTree(i));
        }
        build(result);
    }

    void build(LinkedList<HeightBiasedLeftistTree> result) {
        while (result.size() > 1) {
            HeightBiasedLeftistTree one = result.pop();
            HeightBiasedLeftistTree two = result.pop();

            one.meld(two);
            result.add(one);
        }
        fHead = result.pop().fHead;
    }

    int min() {
        return fHead.fVal;
    }

    int size() {
        return fHead == null ? 0 : fHead.fSize;
    }

    HBLTNode head() {
        return fHead;
    }

    void add(int newVal) {
        HeightBiasedLeftistTree newTree = new HeightBiasedLeftistTree(newVal);
        meld(newTree);
    }

    int pop() {
        if (fHead == null)
            throw new NoSuchElementException("No elements in the tree!");

        int ret = min();
        if (fHead.fLeft == null)
            fHead = fHead.fRight;
        else if (fHead.fRight == null)
            fHead = fHead.fLeft;
        else {
            fHead = meld(fHead.fLeft, fHead.fRight);
        }
        return ret;
    }

    void meld(HeightBiasedLeftistTree other) {
        fHead = meld(head(), other.head());
    }

    private HBLTNode meld(HBLTNode one, HBLTNode two) {
        if (one == null)
            return two;
        if (two == null)
            return one;

        // swap the two nodes to make sure "one" always has a value smaller than "two"
        if (one.fVal > two.fVal) {
            HBLTNode tmp = one;
            one = two;
            two = tmp;
        }

        one.fRight = meld(one.fRight, two);

        int rightSHeight = one.fRight == null ? 0 : one.fRight.sHeight;
        int leftSHeight = one.fLeft == null ? 0 : one.fLeft.sHeight;

        int rightSize = one.fRight == null ? 0 : one.fRight.fSize;
        int leftSize = one.fLeft == null ? 0 : one.fLeft.fSize;

        if (rightSHeight > leftSHeight) {
            HBLTNode tmp = one.fRight;
            one.fRight = one.fLeft;
            one.fLeft = tmp;
        }

        one.fSize = rightSize + leftSize + 1;
        one.sHeight = Math.min(rightSHeight, leftSHeight) + 1;
        return one;
    }
}


class HeightBiasedLeftistTreeTest {

    @Test
    void testSimpleConstruction() {
        int[] input = new int[]{1, 2, 3};
        HeightBiasedLeftistTree tree = new HeightBiasedLeftistTree(input);
        assertEquals(1, tree.min());
        assertEquals(2, tree.head().fLeft.fVal);
        assertEquals(3, tree.head().fRight.fVal);

        assertEquals(3, tree.size());
    }

    @Test
    void testComplexConstruction() {
        int[] input = new int[]{3, 4, 5, 6, 2};
        HeightBiasedLeftistTree tree = new HeightBiasedLeftistTree(input);
        assertEquals(2, tree.min());
        assertEquals(2, tree.head().sHeight);

        assertEquals(5, tree.size());
    }

    @Test
    void testReverseInput() {
        int[] input = new int[]{5, 4, 3, 2, 1};
        HeightBiasedLeftistTree tree = new HeightBiasedLeftistTree(input);
        assertEquals(1, tree.min());
        assertEquals(4, tree.head().fLeft.fVal);
        assertEquals(5, tree.head().fLeft.fLeft.fVal);
        assertEquals(2, tree.head().fRight.fVal);
        assertEquals(3, tree.head().fRight.fLeft.fVal);

        assertEquals(5, tree.size());
    }

    @Test
    void testAdd() {
        int[] input = new int[]{1, 2, 5, 10, 3};
        HeightBiasedLeftistTree tree = new HeightBiasedLeftistTree(input);
        tree.add(2);

        assertEquals(6, tree.size());
    }

    @Test
    void testAddReplaceNewMin() {
        int[] input = new int[]{1, 2, 5, 10, 3};
        HeightBiasedLeftistTree tree = new HeightBiasedLeftistTree(input);
        tree.add(-1);

        assertEquals(-1, tree.min());
        assertEquals(6, tree.size());
    }

    @Test
    void testPopSimple() {
        int[] input = new int[]{1};
        HeightBiasedLeftistTree tree = new HeightBiasedLeftistTree(input);
        int top = tree.pop();

        assertEquals(1, top);
        assertEquals(0, tree.size());
    }

    @Test
    void testPopForHeapSort() {
        int[] input = new int[]{1, 2, 5, 10, 3};
        HeightBiasedLeftistTree tree = new HeightBiasedLeftistTree(input);

        assertEquals(1, tree.pop());
        assertEquals(2, tree.pop());
        assertEquals(3, tree.pop());
        assertEquals(5, tree.pop());
        assertEquals(10, tree.pop());
    }

    @Test
    void testPopExceptionOnNoElements() {
        int[] input = new int[]{1};
        HeightBiasedLeftistTree tree = new HeightBiasedLeftistTree(input);
        tree.pop();

        Assertions.assertThrows(NoSuchElementException.class, tree::pop);
    }

    @Test
    void testMinRuntimeComparison() {
        int[] firstArr = generateInput(1000000, 100000);
        HeightBiasedLeftistTree hblt = new HeightBiasedLeftistTree(firstArr);

        measureRuntime(hblt::min,
                "Duration for height biased leftist tree min %s nanoseconds");

        List<Integer> firstList = convertToList(firstArr);
        PriorityQueue<Integer> pq = new PriorityQueue<>(firstList);

        measureRuntime(pq::peek, "Duration for priority queue min %s nanoseconds");
    }

    @Test
    void testPopRuntimeComparison() {
        int[] firstArr = generateInput(1000000, 100000);
        HeightBiasedLeftistTree hblt = new HeightBiasedLeftistTree(firstArr);

        measureRuntime(hblt::pop,
                "Duration for height biased leftist tree pop %s nanoseconds");

        List<Integer> firstList = convertToList(firstArr);
        PriorityQueue<Integer> pq = new PriorityQueue<>(firstList);

        measureRuntime(pq::poll, "Duration for priority queue pop %s nanoseconds");
    }

    @Test
    void testBuildRuntimeComparison() {
        int[] firstArr = generateInput(1000000, 100000);

        LinkedList<HeightBiasedLeftistTree> tmp = new LinkedList<>();
        for (int i : firstArr) {
            tmp.add(new HeightBiasedLeftistTree(i));
        }
        HeightBiasedLeftistTree hblt = new HeightBiasedLeftistTree();

        measureRuntime(() -> hblt.build(tmp),
                "Duration for height biased leftist tree build %s nanoseconds");

        List<Integer> firstList = convertToList(firstArr);

        measureRuntime(() -> new PriorityQueue<>(firstList),
                "Duration for priority queue build %s nanoseconds");
    }

    @Test
    void testMergeHBLTRuntime() {
        int[] firstArr = generateInput(1000000, 100000);
        int[] secondArr = generateInput(1000000, 100000);

        HeightBiasedLeftistTree hblt = new HeightBiasedLeftistTree(firstArr);
        HeightBiasedLeftistTree hblt2 = new HeightBiasedLeftistTree(secondArr);

        measureRuntime(() -> hblt.meld(hblt2), "Duration for height biased leftist tree merge %s nanoseconds");
    }

    @Test
    void testMergeIsFasterWithHBLT() {
        int[] firstArr = generateInput(1000000, 100000);
        int[] secondArr = generateInput(1000000, 100000);

        List<Integer> firstInput = convertToList(firstArr);
        List<Integer> secondInput = convertToList(secondArr);

        PriorityQueue<Integer> pq = new PriorityQueue<>(firstInput);
        PriorityQueue<Integer> pq2 = new PriorityQueue<>(secondInput);

        long startTime = System.nanoTime();
        while (!pq2.isEmpty()) {
            pq.add(pq2.poll());
        }

        long endTime = System.nanoTime();
        long pqDuration = (endTime - startTime);

        HeightBiasedLeftistTree hblt = new HeightBiasedLeftistTree(firstArr);
        HeightBiasedLeftistTree hblt2 = new HeightBiasedLeftistTree(secondArr);

        startTime = System.nanoTime();
        hblt.meld(hblt2);
        endTime = System.nanoTime();

        long hbltDuration = (endTime - startTime);

        System.out.println(String.format("Duration for priority queue merge %s nanoseconds", pqDuration));
        System.out.println(String.format("Duration for height biased leftist tree merge %s nanoseconds", hbltDuration));

        assertTrue(pqDuration >= hbltDuration);

        assertEquals(hblt.size(), pq.size());

        // make sure we have the same values after the merge!
        List<Integer> pqList = new ArrayList<>();
        List<Integer> hbltList = new ArrayList<>();

        while (!pq.isEmpty()) {
            pqList.add(pq.poll());
            hbltList.add(hblt.pop());
        }

        assertEquals(pqList, hbltList);
    }

    private List<Integer> convertToList(int[] input) {
        List<Integer> ret = new ArrayList<>();
        for (int i : input)
            ret.add(i);
        return ret;
    }

    private int[] generateInput(int size, int maxVal) {
        Random rand = new Random();
        int[] input = new int[size];
        for (int j = 0; j < size; j++) {
            input[j] = rand.nextInt(maxVal);
        }
        return input;
    }

    private void measureRuntime(Runnable r, String msg) {
        long startTime = System.nanoTime();
        r.run();
        long endTime = System.nanoTime();
        System.out.println(String.format(msg, (endTime - startTime)));
    }
}