package com.studyolle.studyolle;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class TestJava {

    public static void main ( String[] args ) {
        TestJava testJava = new TestJava();
        testJava.run();
    }

    private void run () {
        int baseNumber = 10;
        class LocalClass {
            void printBaseNumber() {
                int baseNumber = 11;
            }
        }

        Consumer<Integer> integerConsumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer baseNumber) {
                System.out.println(baseNumber);
            }
        };

        IntConsumer printInt = (i) -> {
            System.out.println(i + baseNumber);
        };

        printInt.accept( 10 );

    }


}
