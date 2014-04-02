import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bulat.fattahov 2013
 */
public class PingPongTest {

    @org.junit.Test
    public void testOneThread() throws Exception {

        final int pingCount = 4;
        PingPong pingPong = new PingPong() {
            AtomicInteger counter = new AtomicInteger();
            STATE last = STATE.NEW;

            @Override
            void report(STATE state) {
                assert !last.equals(state);
                super.report(state);
                last = state;
                if (state.equals(STATE.PING)) {
                    counter.incrementAndGet();
                }
            }
        };

        for (int i = 0; i < pingCount; i++) {
            pingPong.ping();
        }

        for (int i = 0; i < 2 * pingCount; i++) {
            pingPong.pong();
        }
    }

    @org.junit.Test
    public void testMultiThread() throws Exception {
        final int pingCount = 20;
        final int threadCount = 10;

        final PingPong pingPong = new PingPong() {
            AtomicInteger counter = new AtomicInteger();
            STATE last = STATE.NEW;

            @Override
            void report(STATE state) {
                assert !last.equals(state);
                last = state;
                if (state.equals(STATE.PING)) {
                    counter.incrementAndGet();
                }
            }
        };

        final CyclicBarrier barrier = new CyclicBarrier(threadCount);
        final CyclicBarrier finalBarrier = new CyclicBarrier(threadCount + 1);

        for (int i = 0; i < threadCount; i++) {
            final PingPong.STATE type = i % 2 == 1 ? PingPong.STATE.PING : PingPong.STATE.PONG;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                        for (int i = 0; i < pingCount; i++) {
                            if (type.equals(PingPong.STATE.PING)) {
                                pingPong.ping();
                            } else {
                                pingPong.pong();
                            }
                        }
                        finalBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        finalBarrier.await();
        System.out.println(pingPong);
    }
}
