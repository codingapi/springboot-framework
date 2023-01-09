package com.codingapi.springboot.framework.trigger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TriggerHandlerContextTest {

    @Test
    void addTrigger() {
        TriggerContext.getInstance().addTrigger(new TriggerHandler<MyTrigger>() {
            @Override
            public boolean preTrigger(MyTrigger trigger) {
                return false;
            }

            @Override
            public void trigger(MyTrigger trigger) {

            }

            @Override
            public boolean remove() {
                return false;
            }
        });

        TriggerContext.getInstance().addTrigger(new TriggerHandler<MyTrigger2>() {
            @Override
            public boolean preTrigger(MyTrigger2 trigger) {
                return false;
            }

            @Override
            public void trigger(MyTrigger2 trigger) {

            }

            @Override
            public boolean remove() {
                return false;
            }
        });

        assertFalse(TriggerContext.getInstance().isEmpty(MyTrigger.class));
        TriggerContext.getInstance().clear(MyTrigger.class);
        assertTrue(TriggerContext.getInstance().isEmpty(MyTrigger.class));
    }

    @Test
    void trigger() {
        TriggerContext.getInstance().addTrigger(new TriggerHandler<MyTrigger>() {
            @Override
            public boolean preTrigger(MyTrigger trigger) {
                return true;
            }

            @Override
            public void trigger(MyTrigger trigger) {
                System.out.println(trigger.getName());
            }

            @Override
            public boolean remove() {
                return true;
            }
        });

        MyTrigger trigger = new MyTrigger("bob");

        TriggerContext.getInstance().trigger(trigger);
        assertTrue(TriggerContext.getInstance().isEmpty(MyTrigger.class));

    }

    @AllArgsConstructor
    private static class MyTrigger implements Trigger{
        @Getter
        private final String name;
    }

    @AllArgsConstructor
    private static class MyTrigger2 implements Trigger{
        @Getter
        private final String name;
    }


}