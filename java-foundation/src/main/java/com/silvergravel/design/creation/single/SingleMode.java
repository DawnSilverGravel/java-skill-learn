package com.silvergravel.design.creation.single;

/**
 *
 * @author DawnStar
 * @since : 2024/5/29
 */
public class SingleMode {
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            Thread thread = new Thread(() -> {
                LazySingle.getInstance("test" + finalI).test();
                LazySingle.LazySingleHolder.INSTANCE.test();
            });

            thread.start();
        }
    }

    public static class LazySingle {

        public static class LazySingleHolder {
            public static final LazySingle INSTANCE = new LazySingle("Test");
        }
        public volatile static LazySingle instance;

        private final String name;

        private LazySingle(String name) {
            this.name = name;
        }

        public void test() {
            System.out.println(name);
        }

        public static LazySingle getInstance(String name) {
            if (instance == null) {
                synchronized (SingleMode.class) {
                    if (instance == null) {
                        instance = new LazySingle(name);
                    }
                }
            }
            return instance;
        }
    }
}
