package task;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by air on 09/09/15.
 * Doesn't supports resize
 */
public class NonBlockingDataHolder implements IDataHolder {

    /**
     * AtomicReference keeps reference to next collision
     */
    static final class Node extends AtomicReference<Node> {

        private Node(String key, List<String> values) {
            this.key = key;
            this.values = values;
        }

        private String key;
        private List<String> values;
        private AtomicBoolean mergeInProgress = new AtomicBoolean();
    }

    private Node[] arr = new Node[10];

    /**
     *
     * @param key
     * @param values
     */
    @Override
    public void putOrMerge(String key, String[] values) {
        if (key == null) {
            return;
        }
        int hash = hash(key);
        Node n = arr[hash];
        if (n == null) {
            arr[hash] = new Node(key, Arrays.asList(values));
        } else put(null, n, key, values);
    }

    private int hash(String key) {
        return key.hashCode() % (arr.length - 1);
    }

    /**
     * Not thread safe
     * @param key
     * @return
     */
    public List<String> get(String key) {
        Node n = arr[hash(key)];
        List<String> values = null;
        while (n != null) {
            if (n.key.equals(key)) {
                values = n.values;
                break;
            }
            n = n.get();
        }
        return values;
    }


    private void put(Node prev, Node n, String key, String[] values) {
        if (n == null) {
            prev.compareAndSet(null, new Node(key, Arrays.asList(values)));
        } else if (n.key.equals(key)) {
            merge(n, key, values);
        } else {
            put(n, n.get(), key, values);
        }
    }

    private void merge(Node node, String key, String[] values) {
        if (node.mergeInProgress.compareAndSet(false, true)) {
            for (String v : values) {
                boolean absent = true;
                for (String value : node.values) {
                    if (v.hashCode() == value.hashCode() && v.equals(value)) {// equals strings has equ hc
                        absent = false;
                        break;
                    }
                }
                if (absent) {
                    node.values.add(v);
                }
            }
            node.mergeInProgress.compareAndSet(true, false);
        } else {
            merge(node, key, values);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node node : arr) {
            while (node != null) {
                sb.append(node.key).append(":");
                for (String value : node.values) {
                    sb.append(value).append(',');
                }
                sb.setCharAt(sb.length()-1, ';');
                node = node.get();
            }

        }
        return sb.toString();
    }
}
