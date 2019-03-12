import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class STNode {
    int val;

    // ranges
    int start;
    int end;

    STNode left;
    STNode right;
}


class SegmentTree {

    private int[] fArr;
    STNode fHead;

    SegmentTree(int[] arr) {
        fArr = arr;
        buildTree();
    }

    private void buildTree() {
        if (fArr != null && fArr.length > 0)
            fHead = buildTree(0, fArr.length - 1);
    }

    /**
     * Builds a Segment Tree
     *
     * @param low  start of range inclusive
     * @param high end of range inclusive
     * @return fHead of the tree
     */
    private STNode buildTree(int low, int high) {
        if (low >= high) {
            STNode node = new STNode();
            node.val = fArr[low];
            node.start = low;
            node.end = high;
            return node;
        }

        STNode node = new STNode();
        int mid = low + (high - low) / 2;
        node.left = buildTree(low, mid);
        node.right = buildTree(mid + 1, high);
        node.start = low;
        node.end = high;

        node.val = node.left.val + node.right.val;
        return node;
    }

    /**
     * Uses the interval tree to sum over the given range
     *
     * @param start start of the range
     * @param end   end of the range
     * @return the sum
     */
    int sum(int start, int end) {
        return sum(start, end, fHead);
    }

    private int sum(int start, int end, STNode node) {
        if (start > end)
            return 0;

        if (node == null)
            return 0;

        if (start > node.end)
            return 0;
        if (end < node.start)
            return 0;

        if (start == node.start && end == node.end)
            return node.val;

        int mid = node.start + (node.end - node.start) / 2;
        return sum(start, Math.min(mid, end), node.left) + sum(Math.max(mid + 1, start), end, node.right);
    }

    void update(int index, int val) {
        update(index, val, fHead);
    }

    private void update(int index, int val, STNode node) {
        if(node == null)
            return;

        if(index < node.start || index > node.end)
            return;

        if(index == node.start && index == node.end) {
            node.val = val;
            return;
        }

        int mid = node.start + (node.end - node.start) / 2;

        if(index <= mid)
            update(index, val, node.left);
        else update(index, val, node.right);

        node.val = node.left.val + node.right.val;
    }
}


class SegmentTreeTest {
    @Test
    void testInitNoCrash() {
        int[] input = new int[]{1, 2, 3, 4, 5, 6};
        SegmentTree sg = new SegmentTree(input);
    }

    @Test
    void testSTStructure() {
        int[] input = new int[]{1, 2, 3, 4, 5, 6};
        SegmentTree sg = new SegmentTree(input);
        assertEquals(21, sg.fHead.val);
        assertEquals(6, sg.fHead.left.val);
        assertEquals(15, sg.fHead.right.val);

        assertEquals(0, sg.fHead.start);
        assertEquals(5, sg.fHead.end);

        assertEquals(0, sg.fHead.left.start);
        assertEquals(2, sg.fHead.left.end);

        assertEquals(3, sg.fHead.right.start);
        assertEquals(5, sg.fHead.right.end);
    }

    @Test
    void testSTSum() {
        int[] input = new int[]{1, 2, 3, 4, 5, 6};
        SegmentTree sg = new SegmentTree(input);

        assertEquals(3, sg.sum(0, 1));
        assertEquals(14, sg.sum(1, 4));
        assertEquals(21, sg.sum(0, 5));
        assertEquals(5, sg.sum(4, 4));
        assertEquals(18, sg.sum(2, 5));
        assertEquals(15, sg.sum(3, 5));
        assertEquals(11, sg.sum(4, 5));
    }

    @Test
    void testUpdate() {
        int[] input = new int[]{1, 2, 3, 4, 5, 6, 7};
        SegmentTree sg = new SegmentTree(input);

        assertEquals(3, sg.sum(0, 1));
        sg.update(0, 2);
        assertEquals(4, sg.sum(0, 1));
    }
}
