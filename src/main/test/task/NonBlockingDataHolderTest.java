package task;

import org.testng.annotations.*;
import task.NonBlockingDataHolder;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.testng.Assert.*;

/**
 * Created by air on 10/09/15.
 */
public class NonBlockingDataHolderTest {

    NonBlockingDataHolder dataHolder = new NonBlockingDataHolder();

    @DataProvider(name = "provider")
    public Object[][] data() {
        return new Object[][]{
                {"key", new String[]{"a", "b"}}
                , {"key", new String[]{"a", "b", "c"}}
                , {"key1", new String[]{"a", "b", "c"}}
                , {"key2", new String[]{"e", "b", "c","d"}}
                , {"key3", new String[]{"a", "b", "c"}}
                , {"key", new String[]{"a", "b", "f","d","e"}}
                , {"key2", new String[]{"a", "e","f"}}
                , {"key1", new String[]{"a", "b", "c","d","e","f"}}
                , {"key8", new String[]{"a", "b", "c","d","e","f"}}
                , {"key9", new String[]{"a", "b", "c","d","f"}}
                , {"key9", new String[]{"a", "c","d","e","f"}}
                , {"key10", new String[]{"a", "b", "c","d","e","f"}}
                , {"key10", new String[]{"a", "a", "c","d","e","e"}}
                , {"key11", new String[]{"a", "b", "c","d","e","f"}}
                , {"key12", new String[]{"a", "b", "c","d","e","f"}}
                , {"key13", new String[]{"a", "b", "c","d","e","f"}}
                , {"key3", new String[]{"f", "b", "c","d","e"}}};
    }

    @Test(dataProvider = "provider",
            singleThreaded = false,
            alwaysRun = true,
            skipFailedInvocations = false,
            threadPoolSize = 4,
            invocationCount = 4)
    public void testPutOrMerge(final String key, final String[] values) throws Exception {
        dataHolder.putOrMerge(key, values);
    }

    @AfterTest
    @Test
    public void testResult() {
        Collection<String> expected = new ArrayList<String>() {{
            add("a");
            add("b");
            add("c");
            add("d");
            add("e");
            add("f");
        }};
        List<String> actual = dataHolder.get("key");
        Collections.sort(actual);
        assertEquals(actual, expected);
        actual = dataHolder.get("key1");
        Collections.sort(actual);
        assertEquals(actual, expected);
        actual = dataHolder.get("key2");
        Collections.sort(actual);
        assertEquals(actual, expected);
        actual = dataHolder.get("key3");
        Collections.sort(actual);
        assertEquals(actual, expected);
        actual = dataHolder.get("key9");
        Collections.sort(actual);
        assertEquals(actual, expected);
        actual = dataHolder.get("key8");
        Collections.sort(actual);
        assertEquals(actual, expected);
        actual = dataHolder.get("key10");
        Collections.sort(actual);
        assertEquals(actual, expected);
        actual = dataHolder.get("key11");
        Collections.sort(actual);
        assertEquals(actual, expected);
        actual = dataHolder.get("key12");
        Collections.sort(actual);
        assertEquals(actual, expected);
        actual = dataHolder.get("key13");
        Collections.sort(actual);
        assertEquals(actual, expected);
    }
}