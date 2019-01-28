package akeefer.service.impl;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class IntervalIteratorTest {
    @Test
    public void testIterator() throws Exception {
        DateTime base = new DateTime(2015, 4, 6, 0, 0, 0, 0);
        PersonServiceImpl.IntervalIterator iterator = new PersonServiceImpl.IntervalIterator(
                new Interval(base, base.plusWeeks(1)), Days.ONE);

        List<Interval> intervalle = new ArrayList<>();
        for (; iterator.hasNext(); ) {
            intervalle.add(iterator.next());
        }

        assertThat(intervalle, contains(
                new Interval(base.plusDays(6), base.plusDays(7)),
                new Interval(base.plusDays(5), base.plusDays(6)),
                new Interval(base.plusDays(4), base.plusDays(5)),
                new Interval(base.plusDays(3), base.plusDays(4)),
                new Interval(base.plusDays(2), base.plusDays(3)),
                new Interval(base.plusDays(1), base.plusDays(2)),
                new Interval(base, base.plusDays(1))
        ));
    }
}