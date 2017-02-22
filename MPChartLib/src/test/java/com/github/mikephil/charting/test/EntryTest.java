package com.github.mikephil.charting.test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.github.mikephil.charting.data.Entry;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a test class for {@link Entry}.
 *
 * @author patrick.kleindienst (@PaddySmalls)
 */

public class EntryTest {

  @Test
  public void testEntryInstanceIsEqualToItself() {
    Entry entry = new Entry(0.0f, 0.0f);

    assertThat(entry.equals(entry), is(true));
  }

  @Test
  public void testTwoEmptyEntryInstancesAreEqual() {
    Entry entry1 = new Entry();
    Entry entry2 = new Entry();

    assertThat(entry1.equals(entry2), is(true));
  }

  @Test
  public void testEntriesWithSamePropValuesAreEqual() {
    Entry entry1 = new Entry(1.0f, 0.0f);
    Entry entry2 = new Entry(1.0f, 0.0f);

    assertThat(entry1.equals(entry2), is(true));
  }

  @Test
  public void testEntriesWithDifferentPropValuesAreNotEqual() {
    // Different x value, same y value.
    Entry entry1 = new Entry(1.0f, 0.0f);
    Entry entry2 = new Entry(0.0f, 0.0f);

    assertThat(entry1.equals(entry2), is(false));

    // Same x value, different y value.
    entry1 = new Entry(0.0f, 1.0f);
    entry2 = new Entry(0.0f, 0.0f);

    assertThat(entry1.equals(entry2), is(false));

    // Different x and y value.
    entry1 = new Entry(1.0f, 1.0f);
    entry2 = new Entry(0.0f, 0.0f);

    assertThat(entry1.equals(entry2), is(false));
  }

  @Test
  public void testListsContainingEqualEntriesAreEqual() {
    List<Entry> entries1 = new LinkedList<>();
    entries1.addAll(Arrays.asList(new Entry(1.0f, 0.0f), new Entry(0.0f, 1.0f)));

    List<Entry> entries2 = new LinkedList<>();
    entries2.addAll(Arrays.asList(new Entry(1.0f, 0.0f), new Entry(0.0f, 1.0f)));

    assertThat(entries1.equals(entries2), is(true));
  }

  @Test
  public void testEmptyEntryProducesHashcodeDifferentFromZero() {
    Entry entry = new Entry();

    assertThat(entry.hashCode(), is(not(0)));
  }

  @Test
  public void testSameEmptyEntryInstanceProducesSameHashcode() {
    Entry entry = new Entry();

    assertThat(entry.hashCode() == entry.hashCode(), equalTo(true));
  }

  @Test
  public void testEntriesWithSamePropValuesProducesSameHashcode() {
    Entry entry1 = new Entry(1.0f, 0.0f);
    Entry entry2 = new Entry(1.0f, 0.0f);

    assertThat(entry1.hashCode() == entry2.hashCode(), is(true));
  }

  @Test
  public void testEntriesWithDifferentPropValuesProduceDifferentHashcodes() {
    // Different x value, same y value.
    Entry entry1 = new Entry(1.0f, 0.0f);
    Entry entry2 = new Entry(0.0f, 0.0f);

    assertThat(entry1.hashCode() == entry2.hashCode(), is(false));

    // Same x value, different y value.
    entry1 = new Entry(0.0f, 1.0f);
    entry2 = new Entry(0.0f, 0.0f);

    assertThat(entry1.hashCode() == entry2.hashCode(), is(false));

    // Different x and y value.
    entry1 = new Entry(0.0f, 1.0f);
    entry2 = new Entry(1.0f, 0.0f);

    assertThat(entry1.hashCode() == entry2.hashCode(), is(false));
  }

}
