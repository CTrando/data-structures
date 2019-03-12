import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SkewHeapNode {
    SkewHeapNode fLeft;
    SkewHeapNode fRight;

    int fVal;
}

class SkewHeap {

    SkewHeapNode fHead;
    int fSize;

    SkewHeap() {
        fHead = new SkewHeapNode();
    }

    SkewHeap(int val) {
        fHead = new SkewHeapNode();
        fHead.fVal = val;
        fSize = 1;
    }

    SkewHeap(int[] arr) {
        if (arr.length == 0)
            return;

        LinkedList<SkewHeap> result = new LinkedList<>();
        for (int i : arr) {
            result.add(new SkewHeap(i));
        }
        build(result);
    }

    void build(LinkedList<SkewHeap> result) {
        while (result.size() > 1) {
            SkewHeap one = result.pop();
            SkewHeap two = result.pop();

            one.meld(two);
            result.add(one);
        }
        SkewHeap ret = result.pop();
        fSize = ret.fSize;
        fHead = ret.fHead;
    }

    void meld(SkewHeap other) {
        SkewHeapNode ret = meld(fHead, other.fHead);
        fSize += other.fSize;
        fHead = ret;
    }

    void insert(int val) {
        SkewHeap newHeap = new SkewHeap(val);
        meld(newHeap);
    }

    int min() {
        return fHead.fVal;
    }

    int pop() {
        int ret = min();
        fHead = meld(fHead.fLeft, fHead.fRight);
        fSize--;
        return ret;
    }

    SkewHeapNode meld(SkewHeapNode one, SkewHeapNode two) {
        if (two == null)
            return one;
        if (one == null)
            return two;

        if (one.fVal > two.fVal) {
            SkewHeapNode tmp = one;
            one = two;
            two = tmp;
        }

        SkewHeapNode tmp = one.fLeft;
        one.fLeft = one.fRight;
        one.fRight = tmp;

        tmp = two.fLeft;
        two.fLeft = two.fRight;
        two.fRight = tmp;

        one.fLeft = meld(one.fLeft, two);
        return one;
    }

    int size() {
        return fSize;
    }

    boolean isEmpty() {
        return fSize <= 0;
    }
}


class SkewHeapTest {

    @Test
    void testSimpleConstruction() {
        int[] input = new int[]{1, 2, 3};
        SkewHeap heap = new SkewHeap(input);
        assertEquals(1, heap.min());
        assertEquals(3, heap.size());
    }

    @Test
    void testComplexConstruction() {
        int[] input = new int[]{3, 4, 5, 6, 2};
        SkewHeap heap = new SkewHeap(input);
        assertEquals(2, heap.min());

        assertEquals(5, heap.size());
    }

    @Test
    void testReverseInput() {
        int[] input = new int[]{5, 4, 3, 2, 1};
        SkewHeap heap = new SkewHeap(input);
        assertEquals(1, heap.min());
        assertEquals(5, heap.size());
    }

    @Test
    void testAdd() {
        int[] input = new int[]{1, 2, 5, 10, 3};
        SkewHeap tree = new SkewHeap(input);
        tree.insert(2);

        assertEquals(6, tree.size());
    }

    @Test
    void testAddReplaceNewMin() {
        int[] input = new int[]{1, 2, 5, 10, 3};
        SkewHeap heap = new SkewHeap(input);
        heap.insert(-1);

        assertEquals(-1, heap.min());
        assertEquals(6, heap.size());
    }

    @Test
    void testPopSimple() {
        int[] input = new int[]{1};
        SkewHeap tree = new SkewHeap(input);
        int top = tree.pop();

        assertEquals(1, top);
        assertEquals(0, tree.size());
    }

    @Test
    void testPopForHeapSort() {
        int[] input = new int[]{1, 2, 5, 10, 3};
        SkewHeap heap = new SkewHeap(input);

        assertEquals(1, heap.pop());
        assertEquals(2, heap.pop());
        assertEquals(3, heap.pop());
        assertEquals(5, heap.pop());
        assertEquals(10, heap.pop());
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
        SkewHeap heap = new SkewHeap(firstArr);

        measureRuntime(heap::min,
                "Duration for skew heap min %s nanoseconds");

        List<Integer> firstList = convertToList(firstArr);
        PriorityQueue<Integer> pq = new PriorityQueue<>(firstList);

        measureRuntime(pq::peek, "Duration for priority queue min %s nanoseconds");
    }

    @Test
    void testPopRuntimeComparison() {
        int[] firstArr = generateInput(1000000, 100000);
        SkewHeap heap = new SkewHeap(firstArr);

        measureRuntime(heap::pop,
                "Duration for skew heap pop %s nanoseconds");

        List<Integer> firstList = convertToList(firstArr);
        PriorityQueue<Integer> pq = new PriorityQueue<>(firstList);

        measureRuntime(pq::poll, "Duration for priority queue pop %s nanoseconds");
    }

    @Test
    void testBuildRuntimeComparison() {
        int[] firstArr = generateInput(1000000, 100000);

        LinkedList<SkewHeap> tmp = new LinkedList<>();
        for (int i : firstArr) {
            tmp.add(new SkewHeap(i));
        }
        SkewHeap heap = new SkewHeap();

        measureRuntime(() -> heap.build(tmp),
                "Duration for Skew Heap build %s nanoseconds");

        List<Integer> firstList = convertToList(firstArr);

        measureRuntime(() -> new PriorityQueue<>(firstList),
                "Duration for priority queue build %s nanoseconds");
    }

    @Test
    void testMergeSkewHeapRuntime() {
        int[] firstArr = generateInput(1000000, 100000);
        int[] secondArr = generateInput(1000000, 100000);

        SkewHeap heap = new SkewHeap(firstArr);
        SkewHeap heap2 = new SkewHeap(secondArr);

        measureRuntime(() -> heap.meld(heap2), "Duration for Skew Heap merge %s nanoseconds");
    }

    @Test
    void testMergeIsFasterWithSkewHeap() {
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

        SkewHeap heap = new SkewHeap(firstArr);
        SkewHeap heap2 = new SkewHeap(secondArr);

        startTime = System.nanoTime();
        heap.meld(heap2);
        endTime = System.nanoTime();

        long skewHeapDuration = (endTime - startTime);

        System.out.println(String.format("Duration for priority queue merge %s nanoseconds", pqDuration));
        System.out.println(String.format("Duration for Skew Heap merge %s nanoseconds", skewHeapDuration));

        assertTrue(pqDuration >= skewHeapDuration);

        assertEquals(heap.size(), pq.size());

        // make sure we have the same values after the merge!
        List<Integer> pqList = new ArrayList<>();
        List<Integer> skewHeapList = new ArrayList<>();

        measureRuntime(() -> {
            while (!pq.isEmpty()) {
                pqList.add(pq.poll());
            }
        }, "Duration for priority queue heapsort %s nanoseconds");

        measureRuntime(() -> {
            while (!heap.isEmpty()) {
                skewHeapList.add(heap.pop());
            }
        }, "Duration for Skew Heap heapsort %s nanoseconds");
        assertEquals(pqList, skewHeapList);
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
