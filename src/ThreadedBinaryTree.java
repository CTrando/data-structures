import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


class TBNode {
    TBNode left;
    TBNode right;
    TBNode parent;

    boolean isLeftThread;
    boolean isRightThread;

    int fVal;

    TBNode(int val) {
        fVal = val;
    }
}


class ThreadedBinaryTree {
    TBNode fHead;

    ThreadedBinaryTree(int[] contents) {
        fHead = construct(contents);
    }

    private TBNode construct(int[] contents) {
        if (contents.length == 0)
            return null;
        return construct(contents, 0, contents.length);
    }

    /**
     * Constructs tree from given array, using [low, high)
     */
    private TBNode construct(int[] contents, int low, int high) {
        if (low >= high)
            return null;

        int mid = low + (high - low) / 2;
        TBNode cur = new TBNode(contents[mid]);
        cur.left = construct(contents, low, mid);
        cur.right = construct(contents, mid + 1, high);

        if (cur.left != null) cur.left.parent = cur;
        if (cur.right != null) cur.right.parent = cur;
        return cur;
    }

    void constructThreads() {
        constructThreads(fHead);
    }

    private void constructThreads(TBNode node) {
        if (node == null)
            return;
        constructThreads(node.left);
        constructThreads(node.right);

        if (node.left == null) {
            node.isLeftThread = true;

            TBNode parent = node.parent;
            TBNode temp = node;
            while (parent != null && parent.left == temp) {
                temp = parent;
                parent = parent.parent;
            }
            node.left = parent;
        }

        if (node.right == null) {
            node.isRightThread = true;

            TBNode parent = node.parent;
            TBNode temp = node;
            while (parent != null && parent.right == temp) {
                temp = parent;
                parent = parent.parent;
            }
            node.right = parent;
        }
    }

    private TBNode next(TBNode node) {
        if (node == null)
            return null;

        // if the node is a right thread, that means in a regular binary tree then there is no right child
        if (node.isRightThread) {
            // this will return the next inorder node
            return node.right;
        }

        // if it gets down here that means the right child does exist, so want to get the leftmost node of that subtree
        TBNode tmp = node.right;
        while (tmp.left != null && !tmp.isLeftThread)
            tmp = tmp.left;
        return tmp;
    }

    List<Integer> inorder() {
        List<Integer> ret = new ArrayList<>();
        TBNode cur = fHead;
        while (cur.left != null)
            cur = cur.left;

        ret.add(cur.fVal);
        cur = next(cur);
        while (cur != null) {
            ret.add(cur.fVal);
            cur = next(cur);
        }
        return ret;
    }
}


class ThreadedBinaryTreeTest {

    @Test
    void testConstruction() {
        int[] input = new int[]{1, 2, 3, 4, 5, 6, 7};
        ThreadedBinaryTree tb = new ThreadedBinaryTree(input);

        assertEquals(4, tb.fHead.fVal);
        assertEquals(2, tb.fHead.left.fVal);
        assertEquals(6, tb.fHead.right.fVal);
    }

    @Test
    void constructThreads() {
        int[] input = new int[]{1, 2, 3, 4, 5, 6, 7};
        ThreadedBinaryTree tb = new ThreadedBinaryTree(input);
        tb.constructThreads();

        assertEquals(tb.fHead, tb.fHead.left.right.right);
        assertEquals(tb.fHead.left, tb.fHead.left.left.right);
        assertNull(tb.fHead.left.left.left);
    }

    @Test
    void testInOrder() {
        int[] input = new int[]{1, 2, 3, 4, 5, 6, 7};
        ThreadedBinaryTree tb = new ThreadedBinaryTree(input);
        tb.constructThreads();

        List<Integer> ret = tb.inorder();
        List<Integer> expected = toList(input);
        assertEquals(expected, ret);
    }

    @Test
    void testInOrderOnLargerInputs() {
        int[] input = new int[]{2, 4, 5, 2, 35, 6, 2, 1, 3, 6, 6, 2, 2, 3, 5, 6, 2, 1, 1, 5, 6};
        ThreadedBinaryTree tb = new ThreadedBinaryTree(input);
        tb.constructThreads();

        List<Integer> ret = tb.inorder();
        List<Integer> expected = toList(input);
        assertEquals(expected, ret);
    }

    @Test
    void testInOrderRandomInputs() {
        final int trials = 1000;
        for (int i = 0; i < trials; i++) {
            int[] input = generateInput(1000, 10000);

            ThreadedBinaryTree tb = new ThreadedBinaryTree(input);
            tb.constructThreads();
            List<Integer> actual = tb.inorder();
            List<Integer> expected = Arrays.stream(input).boxed().collect(Collectors.toList());

            assertTrue(isValidInorder(actual, expected));
        }
    }

    private int[] generateInput(int size, int maxVal) {
        Random rand = new Random();
        int[] input = new int[size];
        for (int j = 0; j < size; j++) {
            input[j] = rand.nextInt(maxVal);
        }
        return input;
    }

    private boolean isValidInorder(List<Integer> toBeChecked, List<Integer> original) {
        return toBeChecked.equals(original);
    }

    private List<Integer> toList(int[] input) {
        List<Integer> expected = new ArrayList<>();
        for (int i : input) expected.add(i);
        return expected;
    }
}
